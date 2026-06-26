package cl.duoc.Perfulandiabilling.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import cl.duoc.Perfulandiabilling.Repository.PagoRepository;
import cl.duoc.Perfulandiabilling.Repository.FacturaRepository;
import cl.duoc.Perfulandiabilling.Client.OrderClient;
import cl.duoc.Perfulandiabilling.Client.SucursalClient;
import cl.duoc.Perfulandiabilling.Model.PagoModel;
import cl.duoc.Perfulandiabilling.Model.FacturaModel;
import cl.duoc.Perfulandiabilling.Model.FacturaProductoModel;
import cl.duoc.Perfulandiabilling.Exception.ResourceNotFoundException;
import cl.duoc.Perfulandiabilling.dto.Request.PagoCreateRequest;
import cl.duoc.Perfulandiabilling.dto.Request.FacturaCreateRequest;
import cl.duoc.Perfulandiabilling.dto.Response.PagoResponse;
import cl.duoc.Perfulandiabilling.dto.Response.FacturaResponse;
import cl.duoc.Perfulandiabilling.dto.Response.FacturaProductoResponse;
import cl.duoc.Perfulandiabilling.dto.Response.OrderItemResponse;
import cl.duoc.Perfulandiabilling.dto.Response.OrderResponse;
import cl.duoc.Perfulandiabilling.dto.Response.SucursalResponse;

@Slf4j
@Service
@RequiredArgsConstructor
public class FacturacionService {

    private final PagoRepository paymentRepository;
    private final FacturaRepository invoiceRepository;
    private final OrderClient orderClient;
    private final SucursalClient sucursalClient;

    private PagoResponse mapToPaymentDTO(PagoModel payment) {
        return new PagoResponse(
                payment.getIdPago(),
                payment.getIdUsuario(),
                payment.getIdOrden(),
                payment.getMonto(),
                payment.getMetodo(),
                payment.getEstado()
        );
    }

    private FacturaResponse mapToInvoiceDTO(FacturaModel invoice) {
        return new FacturaResponse(
                invoice.getIdFactura(),
                invoice.getIdUsuario(),
                invoice.getIdSucursal(),
                invoice.getNombreSucursal(),
                invoice.getIdPago(),
                invoice.getProductos().stream()
                        .map(this::mapToInvoiceProductDTO)
                        .collect(Collectors.toList()),
                invoice.getCantidadTotal(),
                invoice.getMontoTotal(),
                invoice.getDescripcion(),
                invoice.getEstado(),
                invoice.getFechaEmision()
        );
    }

    private FacturaProductoResponse mapToInvoiceProductDTO(FacturaProductoModel producto) {
        return new FacturaProductoResponse(
                producto.getIdProducto(),
                producto.getNombreProducto(),
                producto.getCantidad(),
                producto.getPrecioUnitario(),
                producto.getSubtotal()
        );
    }

    private FacturaProductoModel mapToInvoiceProduct(OrderItemResponse item) {
        double precioUnitario = item.getPrecio() == null ? 0.0 : item.getPrecio();
        int cantidad = item.getCantidad() == null ? 0 : item.getCantidad();
        return FacturaProductoModel.builder()
                .idProducto(item.getIdProducto())
                .nombreProducto(item.getNombreProducto())
                .cantidad(cantidad)
                .precioUnitario(precioUnitario)
                .subtotal(precioUnitario * cantidad)
                .build();
    }

    public PagoResponse registrarPago(PagoCreateRequest request) {
        log.info("Registrando pago para usuario ID: {}", request.getIdUsuario());
        OrderResponse pedido = orderClient.obtenerPedido(request.getIdOrden());

        PagoModel pago = PagoModel.builder()
                .idUsuario(pedido.getIdUsuario())
                .idOrden(request.getIdOrden())
                .monto(request.getMonto())
                .metodo(request.getMetodo())
                .estado("COMPLETADO")
                .fechaRegistro(LocalDateTime.now())
                .build();

        PagoModel saved = paymentRepository.save(pago);
        log.info("Pago registrado con ID: {}", saved.getIdPago());
        return mapToPaymentDTO(saved);
    }

    public List<PagoResponse> listarPagos() {
        log.info("Listando todos los pagos");
        List<PagoResponse> respuesta = paymentRepository.findAll()
                .stream()
                .map(this::mapToPaymentDTO)
                .collect(Collectors.toList());

        log.info("Cantidad de pagos encontrados={}", respuesta.size());
        return respuesta;
    }

    public PagoResponse obtenerPagoPorId(Long id) {
        log.info("Buscando pago ID: {}", id);
        return paymentRepository.findById(id)
                .map(this::mapToPaymentDTO)
                .orElseThrow(() -> {
                    log.warn("Pago no encontrado ID: {}", id);
                    return new ResourceNotFoundException("Pago no encontrado con ID: " + id);
                });
    }

    public void eliminarPago(Long id) {
        log.info("Eliminando pago ID: {}", id);
        if (!paymentRepository.existsById(id)) {
            log.warn("Pago no encontrado ID: {}", id);
            throw new ResourceNotFoundException("Pago no encontrado con ID: " + id);
        }
        paymentRepository.deleteById(id);
        log.info("Pago ID: {} eliminado correctamente", id);
    }

    public FacturaResponse generarFactura(FacturaCreateRequest request) {
        log.info("Generando factura para pago ID: {} y sucursal ID: {}", request.getIdPago(), request.getIdSucursal());

        PagoModel pago = paymentRepository.findById(request.getIdPago())
                .orElseThrow(() -> {
                    log.warn("Pago no encontrado ID: {}", request.getIdPago());
                    return new ResourceNotFoundException("Pago no encontrado con ID: " + request.getIdPago());
                });

        OrderResponse pedido = orderClient.obtenerPedido(pago.getIdOrden());
        SucursalResponse sucursal = sucursalClient.obtenerSucursal(request.getIdSucursal());

        List<OrderItemResponse> itemsPedido = pedido.getItems() == null ? List.of() : pedido.getItems();
        if (itemsPedido.isEmpty()) {
            throw new RuntimeException("No se puede generar una factura sin productos");
        }

        List<FacturaProductoModel> productos = itemsPedido.stream()
                .map(this::mapToInvoiceProduct)
                .collect(Collectors.toList());

        int cantidadTotal = productos.stream()
                .mapToInt(FacturaProductoModel::getCantidad)
                .sum();

        double montoTotal = productos.stream()
                .mapToDouble(FacturaProductoModel::getSubtotal)
                .sum();

        FacturaModel factura = FacturaModel.builder()
                .idUsuario(pago.getIdUsuario())
                .idPago(pago.getIdPago())
                .idSucursal(request.getIdSucursal())
                .nombreSucursal(sucursal.getNombreSucursal())
                .productos(productos)
                .cantidadTotal(cantidadTotal)
                .montoTotal(montoTotal)
                .descripcion(request.getDescripcion())
                .estado("EMITIDA")
                .fechaEmision(LocalDateTime.now())
                .build();

        FacturaModel saved = invoiceRepository.save(factura);
        log.info("Factura generada con ID: {}", saved.getIdFactura());
        return mapToInvoiceDTO(saved);
    }

    public List<FacturaResponse> listarFacturas() {
        log.info("Listando todas las facturas");
        List<FacturaResponse> respuesta = invoiceRepository.findAll()
                .stream()
                .map(this::mapToInvoiceDTO)
                .collect(Collectors.toList());

        log.info("Cantidad de facturas encontradas={}", respuesta.size());
        return respuesta;
    }

    public FacturaResponse obtenerFacturaPorId(Long id) {
        log.info("Buscando factura ID: {}", id);
        return invoiceRepository.findById(id)
                .map(this::mapToInvoiceDTO)
                .orElseThrow(() -> {
                    log.warn("Factura no encontrada ID: {}", id);
                    return new ResourceNotFoundException("Factura no encontrada con ID: " + id);
                });
    }

    public void eliminarFactura(Long id) {
        log.info("Eliminando factura ID: {}", id);
        if (!invoiceRepository.existsById(id)) {
            log.warn("Factura no encontrada ID: {}", id);
            throw new ResourceNotFoundException("Factura no encontrada con ID: " + id);
        }
        invoiceRepository.deleteById(id);
        log.info("Factura ID: {} eliminada correctamente", id);
    }
}
