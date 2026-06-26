package cl.duoc.VentasPerfulandia.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Service;

import cl.duoc.VentasPerfulandia.client.FacturacionClient;
import cl.duoc.VentasPerfulandia.client.OrderClient;
import cl.duoc.VentasPerfulandia.dto.request.VentaCreateRequest;
import cl.duoc.VentasPerfulandia.dto.request.VentaEstadoRequest;
import cl.duoc.VentasPerfulandia.dto.response.FacturaResponse;
import cl.duoc.VentasPerfulandia.dto.response.OrderItemResponse;
import cl.duoc.VentasPerfulandia.dto.response.OrderResponse;
import cl.duoc.VentasPerfulandia.dto.response.PagoResponse;
import cl.duoc.VentasPerfulandia.dto.response.VentaDetalleResponse;
import cl.duoc.VentasPerfulandia.dto.response.VentaResponse;
import cl.duoc.VentasPerfulandia.exception.ResourceNotFoundException;
import cl.duoc.VentasPerfulandia.model.VentaDetalleModel;
import cl.duoc.VentasPerfulandia.model.VentaModel;
import cl.duoc.VentasPerfulandia.repository.VentaRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class VentaService {

    private static final String ESTADO_CONFIRMADA = "CONFIRMADA";
    private static final String ESTADO_ANULADA = "ANULADA";

    private final VentaRepository ventaRepository;
    private final OrderClient orderClient;
    private final FacturacionClient facturacionClient;

    public List<VentaResponse> listarVentas() {
        return ventaRepository.findAll()
                .stream()
                .map(this::convertirAResponse)
                .toList();
    }

    public VentaResponse confirmarVenta(VentaCreateRequest request) {
        if (ventaRepository.existsByIdPedido(request.getIdPedido())) {
            throw new RuntimeException("Ya existe una venta registrada para el pedido: " + request.getIdPedido());
        }

        OrderResponse pedido = orderClient.obtenerPedidoPorId(request.getIdPedido());
        PagoResponse pago = facturacionClient.obtenerPagoPorId(request.getIdPago());
        FacturaResponse factura = facturacionClient.obtenerFacturaPorId(request.getIdFactura());

        validarDatosDeVenta(pedido, pago, factura);

        VentaModel venta = VentaModel.builder()
                .idPedido(pedido.getIdPedido())
                .idUsuario(pedido.getIdUsuario())
                .idPago(pago.getIdPago())
                .idFactura(factura.getIdFactura())
                .totalVenta(factura.getMontoTotal())
                .estadoVenta(ESTADO_CONFIRMADA)
                .canalVenta(request.getCanalVenta())
                .fechaVenta(LocalDateTime.now())
                .build();

        List<VentaDetalleModel> detalles = pedido.getItems()
                .stream()
                .map(this::crearDetalleDesdePedido)
                .toList();

        detalles.forEach(detalle -> detalle.setVenta(venta));
        venta.setDetalles(detalles);

        VentaModel ventaGuardada = ventaRepository.save(venta);
        return convertirAResponse(ventaGuardada);
    }

    public VentaResponse obtenerVentaPorId(Long idVenta) {
        VentaModel venta = ventaRepository.findById(idVenta)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Venta no encontrada con ID: " + idVenta));

        return convertirAResponse(venta);
    }

    public VentaResponse obtenerVentaPorPedido(Long idPedido) {
        VentaModel venta = ventaRepository.findByIdPedido(idPedido)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Venta no encontrada para el pedido: " + idPedido));

        return convertirAResponse(venta);
    }

    public List<VentaResponse> buscarPorUsuario(Long idUsuario) {
        return ventaRepository.findByIdUsuario(idUsuario)
                .stream()
                .map(this::convertirAResponse)
                .toList();
    }

    public List<VentaResponse> buscarPorEstado(String estadoVenta) {
        return ventaRepository.findByEstadoVenta(estadoVenta)
                .stream()
                .map(this::convertirAResponse)
                .toList();
    }

    public VentaResponse actualizarEstado(Long idVenta, VentaEstadoRequest request) {
        VentaModel venta = ventaRepository.findById(idVenta)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Venta no encontrada con ID: " + idVenta));

        venta.setEstadoVenta(request.getEstadoVenta());
        return convertirAResponse(ventaRepository.save(venta));
    }

    public VentaResponse anularVenta(Long idVenta) {
        VentaModel venta = ventaRepository.findById(idVenta)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Venta no encontrada con ID: " + idVenta));

        venta.setEstadoVenta(ESTADO_ANULADA);
        return convertirAResponse(ventaRepository.save(venta));
    }

    public void eliminarVenta(Long idVenta) {
        VentaModel venta = ventaRepository.findById(idVenta)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Venta no encontrada con ID: " + idVenta));

        ventaRepository.delete(venta);
    }

    private void validarDatosDeVenta(
            OrderResponse pedido,
            PagoResponse pago,
            FacturaResponse factura
    ) {
        if (pedido.getItems() == null || pedido.getItems().isEmpty()) {
            throw new IllegalArgumentException("El pedido no tiene productos para registrar la venta");
        }

        if (!Objects.equals(pago.getIdOrden(), pedido.getIdPedido())) {
            throw new IllegalArgumentException("El pago no pertenece al pedido indicado");
        }

        if (!Objects.equals(pago.getIdUsuario(), pedido.getIdUsuario())) {
            throw new IllegalArgumentException("El pago no pertenece al usuario del pedido");
        }

        if (!Objects.equals(factura.getIdPago(), pago.getIdPago())) {
            throw new IllegalArgumentException("La factura no pertenece al pago indicado");
        }

        if (!Objects.equals(factura.getIdUsuario(), pedido.getIdUsuario())) {
            throw new IllegalArgumentException("La factura no pertenece al usuario del pedido");
        }

        if (!"COMPLETADO".equalsIgnoreCase(pago.getEstado())) {
            throw new IllegalArgumentException("El pago debe estar COMPLETADO para confirmar la venta");
        }

        if (!"EMITIDA".equalsIgnoreCase(factura.getEstado())) {
            throw new IllegalArgumentException("La factura debe estar EMITIDA para confirmar la venta");
        }
    }

    private VentaDetalleModel crearDetalleDesdePedido(OrderItemResponse item) {
        double subtotal = item.getPrecio() * item.getCantidad();

        return VentaDetalleModel.builder()
                .idProducto(item.getIdProducto())
                .nombreProducto(item.getNombreProducto())
                .cantidad(item.getCantidad())
                .precioUnitario(item.getPrecio())
                .subtotal(subtotal)
                .build();
    }

    private VentaResponse convertirAResponse(VentaModel venta) {
        return VentaResponse.builder()
                .idVenta(venta.getIdVenta())
                .idPedido(venta.getIdPedido())
                .idUsuario(venta.getIdUsuario())
                .idPago(venta.getIdPago())
                .idFactura(venta.getIdFactura())
                .totalVenta(venta.getTotalVenta())
                .estadoVenta(venta.getEstadoVenta())
                .canalVenta(venta.getCanalVenta())
                .fechaVenta(venta.getFechaVenta())
                .detalles(venta.getDetalles()
                        .stream()
                        .map(this::convertirDetalleAResponse)
                        .toList())
                .build();
    }

    private VentaDetalleResponse convertirDetalleAResponse(VentaDetalleModel detalle) {
        return VentaDetalleResponse.builder()
                .idDetalleVenta(detalle.getIdDetalleVenta())
                .idProducto(detalle.getIdProducto())
                .nombreProducto(detalle.getNombreProducto())
                .cantidad(detalle.getCantidad())
                .precioUnitario(detalle.getPrecioUnitario())
                .subtotal(detalle.getSubtotal())
                .build();
    }
}
