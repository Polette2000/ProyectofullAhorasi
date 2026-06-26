package cl.duoc.Perfulandiabilling.Service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
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

import cl.duoc.Perfulandiabilling.Client.OrderClient;
import cl.duoc.Perfulandiabilling.Client.SucursalClient;
import cl.duoc.Perfulandiabilling.Exception.ResourceNotFoundException;
import cl.duoc.Perfulandiabilling.Model.FacturaModel;
import cl.duoc.Perfulandiabilling.Model.FacturaProductoModel;
import cl.duoc.Perfulandiabilling.Model.PagoModel;
import cl.duoc.Perfulandiabilling.Repository.FacturaRepository;
import cl.duoc.Perfulandiabilling.Repository.PagoRepository;
import cl.duoc.Perfulandiabilling.dto.Request.FacturaCreateRequest;
import cl.duoc.Perfulandiabilling.dto.Request.PagoCreateRequest;
import cl.duoc.Perfulandiabilling.dto.Response.FacturaResponse;
import cl.duoc.Perfulandiabilling.dto.Response.OrderItemResponse;
import cl.duoc.Perfulandiabilling.dto.Response.OrderResponse;
import cl.duoc.Perfulandiabilling.dto.Response.PagoResponse;
import cl.duoc.Perfulandiabilling.dto.Response.SucursalResponse;

@ExtendWith(MockitoExtension.class)
class FacturacionServiceTest {

    @Mock
    private PagoRepository paymentRepository;

    @Mock
    private FacturaRepository invoiceRepository;

    @Mock
    private OrderClient orderClient;

    @Mock
    private SucursalClient sucursalClient;

    @InjectMocks
    private FacturacionService facturacionService;

    private OrderResponse crearPedido() {
        return new OrderResponse(
                10L,
                5L,
                LocalDateTime.now(),
                "PAGADO",
                List.of(new OrderItemResponse(1L, 100L, "Perfume Floral", 2, 12500.0))
        );
    }

    private SucursalResponse crearSucursal() {
        return new SucursalResponse(2, "Sucursal Centro", "Centro 123", "+56911111111", "09:00 a 18:00", 1, "Santiago", "RM");
    }

    private FacturaProductoModel crearProductoFactura() {
        return FacturaProductoModel.builder()
                .idProducto(100L)
                .nombreProducto("Perfume Floral")
                .cantidad(2)
                .precioUnitario(12500.0)
                .subtotal(25000.0)
                .build();
    }

    private PagoModel crearPago() {
        return PagoModel.builder()
                .idPago(1L)
                .idUsuario(5L)
                .idOrden(10L)
                .monto(25000.0)
                .metodo("TARJETA")
                .estado("COMPLETADO")
                .fechaRegistro(LocalDateTime.now())
                .build();
    }

    private FacturaModel crearFactura() {
        return FacturaModel.builder()
                .idFactura(1L)
                .idUsuario(5L)
                .idPago(1L)
                .idSucursal(2L)
                .nombreSucursal("Sucursal Centro")
                .productos(List.of(crearProductoFactura()))
                .cantidadTotal(2)
                .montoTotal(25000.0)
                .descripcion("Compra de perfumes")
                .estado("EMITIDA")
                .fechaEmision(LocalDateTime.now())
                .build();
    }

    @Test
    void registrarPago_debeCrearPagoCuandoOrdenExiste() {
        PagoCreateRequest request = new PagoCreateRequest(5L, 10L, 25000.0, "TARJETA");
        when(orderClient.obtenerPedido(10L)).thenReturn(crearPedido());
        when(paymentRepository.save(any(PagoModel.class))).thenReturn(crearPago());

        PagoResponse response = facturacionService.registrarPago(request);

        assertThat(response.getIdPago()).isEqualTo(1L);
        assertThat(response.getIdUsuario()).isEqualTo(5L);
        assertThat(response.getEstado()).isEqualTo("COMPLETADO");
        verify(paymentRepository).save(any(PagoModel.class));
    }

    @Test
    void registrarPago_debeLanzarExcepcionCuandoOrdenNoExiste() {
        PagoCreateRequest request = new PagoCreateRequest(5L, 99L, 25000.0, "TARJETA");
        when(orderClient.obtenerPedido(99L))
                .thenThrow(new ResourceNotFoundException("Pedido no encontrado con ID: 99"));

        assertThatThrownBy(() -> facturacionService.registrarPago(request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Pedido no encontrado con ID: 99");
        verify(paymentRepository, never()).save(any(PagoModel.class));
    }

    @Test
    void obtenerPagoPorId_debeRetornarPagoCuandoExiste() {
        when(paymentRepository.findById(1L)).thenReturn(Optional.of(crearPago()));

        PagoResponse response = facturacionService.obtenerPagoPorId(1L);

        assertThat(response.getIdPago()).isEqualTo(1L);
        assertThat(response.getMetodo()).isEqualTo("TARJETA");
    }

    @Test
    void generarFactura_debeCrearFactura() {
        FacturaCreateRequest request = new FacturaCreateRequest(1L, 2L, "Compra de perfumes");
        when(paymentRepository.findById(1L)).thenReturn(Optional.of(crearPago()));
        when(orderClient.obtenerPedido(10L)).thenReturn(crearPedido());
        when(sucursalClient.obtenerSucursal(2L)).thenReturn(crearSucursal());
        when(invoiceRepository.save(any(FacturaModel.class))).thenReturn(crearFactura());

        FacturaResponse response = facturacionService.generarFactura(request);

        assertThat(response.getIdFactura()).isEqualTo(1L);
        assertThat(response.getIdUsuario()).isEqualTo(5L);
        assertThat(response.getIdSucursal()).isEqualTo(2L);
        assertThat(response.getNombreSucursal()).isEqualTo("Sucursal Centro");
        assertThat(response.getCantidadTotal()).isEqualTo(2);
        assertThat(response.getProductos()).hasSize(1);
        assertThat(response.getEstado()).isEqualTo("EMITIDA");
        verify(invoiceRepository).save(any(FacturaModel.class));
    }

    @Test
    void obtenerFacturaPorId_debeRetornarFacturaCuandoExiste() {
        when(invoiceRepository.findById(1L)).thenReturn(Optional.of(crearFactura()));

        FacturaResponse response = facturacionService.obtenerFacturaPorId(1L);

        assertThat(response.getIdFactura()).isEqualTo(1L);
        assertThat(response.getMontoTotal()).isEqualTo(25000.0);
        assertThat(response.getFechaEmision()).isNotNull();
    }
}
