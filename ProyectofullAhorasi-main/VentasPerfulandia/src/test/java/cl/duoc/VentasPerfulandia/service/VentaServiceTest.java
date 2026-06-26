package cl.duoc.VentasPerfulandia.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import cl.duoc.VentasPerfulandia.client.FacturacionClient;
import cl.duoc.VentasPerfulandia.client.OrderClient;
import cl.duoc.VentasPerfulandia.dto.request.VentaCreateRequest;
import cl.duoc.VentasPerfulandia.dto.request.VentaEstadoRequest;
import cl.duoc.VentasPerfulandia.dto.response.FacturaResponse;
import cl.duoc.VentasPerfulandia.dto.response.OrderItemResponse;
import cl.duoc.VentasPerfulandia.dto.response.OrderResponse;
import cl.duoc.VentasPerfulandia.dto.response.PagoResponse;
import cl.duoc.VentasPerfulandia.dto.response.VentaResponse;
import cl.duoc.VentasPerfulandia.exception.ResourceNotFoundException;
import cl.duoc.VentasPerfulandia.model.VentaDetalleModel;
import cl.duoc.VentasPerfulandia.model.VentaModel;
import cl.duoc.VentasPerfulandia.repository.VentaRepository;

@ExtendWith(MockitoExtension.class)
class VentaServiceTest {

    @Mock
    private VentaRepository ventaRepository;

    @Mock
    private OrderClient orderClient;

    @Mock
    private FacturacionClient facturacionClient;

    @InjectMocks
    private VentaService ventaService;

    @Test
    void listarVentasRetornaVentas() {
        when(ventaRepository.findAll()).thenReturn(List.of(ventaRegistrada()));

        List<VentaResponse> ventas = ventaService.listarVentas();

        assertEquals(1, ventas.size());
        assertEquals(1L, ventas.get(0).getIdVenta());
        assertEquals("CONFIRMADA", ventas.get(0).getEstadoVenta());
    }

    @Test
    void confirmarVentaGuardaVentaCuandoDatosSonValidos() {
        VentaCreateRequest request = new VentaCreateRequest(10L, 20L, 30L, "WEB");
        when(ventaRepository.existsByIdPedido(10L)).thenReturn(false);
        when(orderClient.obtenerPedidoPorId(10L)).thenReturn(pedidoValido());
        when(facturacionClient.obtenerPagoPorId(20L)).thenReturn(pagoValido());
        when(facturacionClient.obtenerFacturaPorId(30L)).thenReturn(facturaValida());
        when(ventaRepository.save(any(VentaModel.class))).thenAnswer(invocation -> {
            VentaModel venta = invocation.getArgument(0);
            venta.setIdVenta(1L);
            venta.getDetalles().get(0).setIdDetalleVenta(100L);
            return venta;
        });

        VentaResponse response = ventaService.confirmarVenta(request);

        assertEquals(1L, response.getIdVenta());
        assertEquals(10L, response.getIdPedido());
        assertEquals("CONFIRMADA", response.getEstadoVenta());
        assertEquals(1, response.getDetalles().size());
        assertEquals(39990.0, response.getTotalVenta());
    }

    @Test
    void confirmarVentaLanzaExcepcionSiYaExistePedido() {
        VentaCreateRequest request = new VentaCreateRequest(10L, 20L, 30L, "WEB");
        when(ventaRepository.existsByIdPedido(10L)).thenReturn(true);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> ventaService.confirmarVenta(request));

        assertTrue(exception.getMessage().contains("Ya existe una venta registrada"));
    }

    @Test
    void obtenerVentaPorIdRetornaVenta() {
        when(ventaRepository.findById(1L)).thenReturn(Optional.of(ventaRegistrada()));

        VentaResponse response = ventaService.obtenerVentaPorId(1L);

        assertEquals(1L, response.getIdVenta());
        assertEquals(10L, response.getIdPedido());
    }

    @Test
    void obtenerVentaPorIdLanzaExcepcionSiNoExiste() {
        when(ventaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> ventaService.obtenerVentaPorId(99L));
    }

    @Test
    void actualizarEstadoGuardaNuevoEstado() {
        VentaModel venta = ventaRegistrada();
        when(ventaRepository.findById(1L)).thenReturn(Optional.of(venta));
        when(ventaRepository.save(venta)).thenReturn(venta);

        VentaResponse response = ventaService.actualizarEstado(1L, new VentaEstadoRequest("DESPACHADA"));

        assertEquals("DESPACHADA", response.getEstadoVenta());
    }

    @Test
    void anularVentaMarcaEstadoAnulada() {
        VentaModel venta = ventaRegistrada();
        when(ventaRepository.findById(1L)).thenReturn(Optional.of(venta));
        when(ventaRepository.save(venta)).thenReturn(venta);

        VentaResponse response = ventaService.anularVenta(1L);

        assertEquals("ANULADA", response.getEstadoVenta());
    }

    @Test
    void eliminarVentaEliminaModeloExistente() {
        VentaModel venta = ventaRegistrada();
        when(ventaRepository.findById(1L)).thenReturn(Optional.of(venta));

        ventaService.eliminarVenta(1L);

        verify(ventaRepository).delete(venta);
    }

    private VentaModel ventaRegistrada() {
        VentaModel venta = VentaModel.builder()
                .idVenta(1L)
                .idPedido(10L)
                .idUsuario(5L)
                .idPago(20L)
                .idFactura(30L)
                .totalVenta(39990.0)
                .estadoVenta("CONFIRMADA")
                .canalVenta("WEB")
                .fechaVenta(LocalDateTime.now())
                .build();

        VentaDetalleModel detalle = VentaDetalleModel.builder()
                .idDetalleVenta(100L)
                .idProducto(7L)
                .nombreProducto("Perfume Ambar")
                .cantidad(2)
                .precioUnitario(19995.0)
                .subtotal(39990.0)
                .venta(venta)
                .build();
        venta.setDetalles(List.of(detalle));
        return venta;
    }

    private OrderResponse pedidoValido() {
        return OrderResponse.builder()
                .idPedido(10L)
                .idUsuario(5L)
                .estado("CREADO")
                .items(List.of(OrderItemResponse.builder()
                        .idItem(1L)
                        .idProducto(7L)
                        .nombreProducto("Perfume Ambar")
                        .cantidad(2)
                        .precio(19995.0)
                        .build()))
                .build();
    }

    private PagoResponse pagoValido() {
        return new PagoResponse(20L, 5L, 10L, 39990.0, "TARJETA", "COMPLETADO");
    }

    private FacturaResponse facturaValida() {
        return new FacturaResponse(30L, 5L, 20L, 39990.0, "Factura venta", "EMITIDA");
    }
}
