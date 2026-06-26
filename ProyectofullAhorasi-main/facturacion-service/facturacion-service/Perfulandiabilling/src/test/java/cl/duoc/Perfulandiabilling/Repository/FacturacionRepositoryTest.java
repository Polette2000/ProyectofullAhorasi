package cl.duoc.Perfulandiabilling.Repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import cl.duoc.Perfulandiabilling.Model.FacturaModel;
import cl.duoc.Perfulandiabilling.Model.FacturaProductoModel;
import cl.duoc.Perfulandiabilling.Model.PagoModel;

@DataJpaTest
@ActiveProfiles("test")
class FacturacionRepositoryTest {

    @Autowired
    private PagoRepository pagoRepository;

    @Autowired
    private FacturaRepository facturaRepository;

    private PagoModel guardarPago(Long idUsuario, Long idOrden) {
        PagoModel pago = PagoModel.builder()
                .idUsuario(idUsuario)
                .idOrden(idOrden)
                .monto(25000.0)
                .metodo("TARJETA")
                .estado("COMPLETADO")
                .fechaRegistro(LocalDateTime.now())
                .build();
        return pagoRepository.save(pago);
    }

    private FacturaModel guardarFactura(Long idUsuario, Long idPago) {
        FacturaModel factura = FacturaModel.builder()
                .idUsuario(idUsuario)
                .idPago(idPago)
                .idSucursal(2L)
                .nombreSucursal("Sucursal Centro")
                .productos(List.of(FacturaProductoModel.builder()
                        .idProducto(100L)
                        .nombreProducto("Perfume Floral")
                        .cantidad(2)
                        .precioUnitario(12500.0)
                        .subtotal(25000.0)
                        .build()))
                .cantidadTotal(2)
                .montoTotal(25000.0)
                .descripcion("Compra de perfumes")
                .estado("EMITIDA")
                .fechaEmision(LocalDateTime.now())
                .build();
        return facturaRepository.save(factura);
    }

    @Test
    void pagoRepository_findByIdUsuario_debeRetornarPagosDelUsuario() {
        guardarPago(5L, 10L);
        guardarPago(5L, 11L);
        guardarPago(6L, 20L);

        List<PagoModel> resultado = pagoRepository.findByIdUsuario(5L);

        assertThat(resultado).hasSize(2);
        assertThat(resultado).allMatch(pago -> pago.getIdUsuario().equals(5L));
    }

    @Test
    void pagoRepository_findByIdOrden_debeRetornarPagosDeOrden() {
        guardarPago(5L, 10L);

        List<PagoModel> resultado = pagoRepository.findByIdOrden(10L);

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getIdUsuario()).isEqualTo(5L);
    }

    @Test
    void pagoRepository_existsByIdUsuarioAndIdOrden_debeRetornarTrueCuandoExiste() {
        guardarPago(5L, 10L);

        boolean resultado = pagoRepository.existsByIdUsuarioAndIdOrden(5L, 10L);

        assertThat(resultado).isTrue();
    }

    @Test
    void pagoRepository_findByIdUsuarioAndIdOrden_debeRetornarPagoCuandoExiste() {
        guardarPago(5L, 10L);

        Optional<PagoModel> resultado = pagoRepository.findByIdUsuarioAndIdOrden(5L, 10L);

        assertThat(resultado).isPresent();
        assertThat(resultado.get().getMonto()).isEqualTo(25000.0);
    }

    @Test
    void facturaRepository_findByIdUsuario_debeRetornarFacturasDelUsuario() {
        PagoModel pago = guardarPago(5L, 10L);
        guardarFactura(5L, pago.getIdPago());

        List<FacturaModel> resultado = facturaRepository.findByIdUsuario(5L);

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getDescripcion()).isEqualTo("Compra de perfumes");
        assertThat(resultado.get(0).getProductos()).hasSize(1);
    }

    @Test
    void facturaRepository_findByIdPago_debeRetornarFacturaDelPago() {
        PagoModel pago = guardarPago(5L, 10L);
        guardarFactura(5L, pago.getIdPago());

        List<FacturaModel> resultado = facturaRepository.findByIdPago(pago.getIdPago());

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getIdUsuario()).isEqualTo(5L);
    }

    @Test
    void facturaRepository_existsByIdUsuarioAndIdPago_debeRetornarTrueCuandoExiste() {
        PagoModel pago = guardarPago(5L, 10L);
        guardarFactura(5L, pago.getIdPago());

        boolean resultado = facturaRepository.existsByIdUsuarioAndIdPago(5L, pago.getIdPago());

        assertThat(resultado).isTrue();
    }

    @Test
    void facturaRepository_findByIdUsuarioAndIdPago_debeRetornarFacturaCuandoExiste() {
        PagoModel pago = guardarPago(5L, 10L);
        guardarFactura(5L, pago.getIdPago());

        Optional<FacturaModel> resultado = facturaRepository.findByIdUsuarioAndIdPago(5L, pago.getIdPago());

        assertThat(resultado).isPresent();
        assertThat(resultado.get().getEstado()).isEqualTo("EMITIDA");
    }
}
