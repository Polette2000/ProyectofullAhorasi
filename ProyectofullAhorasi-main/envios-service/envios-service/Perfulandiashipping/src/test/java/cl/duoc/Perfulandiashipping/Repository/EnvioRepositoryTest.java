package cl.duoc.Perfulandiashipping.Repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import cl.duoc.Perfulandiashipping.Model.EnvioModel;

@DataJpaTest
@ActiveProfiles("test")
class EnvioRepositoryTest {

    @Autowired
    private EnvioRepository envioRepository;

    private EnvioModel guardarEnvio(Long idOrden, String numeroSeguimiento, String estado) {
        EnvioModel envio = new EnvioModel();
        envio.setIdOrden(idOrden);
        envio.setDireccion("Av. Principal 123");
        envio.setNumeroSeguimiento(numeroSeguimiento);
        envio.setEstado(estado);
        envio.setFechaEstimadaInicio(LocalDate.now().plusDays(1));
        envio.setFechaEstimadaFin(LocalDate.now().plusDays(3));
        return envioRepository.save(envio);
    }

    @Test
    void findByNumeroSeguimiento_debeRetornarEnvioCuandoExiste() {
        guardarEnvio(10L, "SEG-001", "PENDIENTE");

        Optional<EnvioModel> resultado = envioRepository.findByNumeroSeguimiento("SEG-001");

        assertThat(resultado).isPresent();
        assertThat(resultado.get().getIdOrden()).isEqualTo(10L);
    }

    @Test
    void findByNumeroSeguimiento_debeRetornarVacioCuandoNoExiste() {
        guardarEnvio(10L, "SEG-001", "PENDIENTE");

        Optional<EnvioModel> resultado = envioRepository.findByNumeroSeguimiento("SEG-999");

        assertThat(resultado).isEmpty();
    }

    @Test
    void existsByIdOrden_debeRetornarTrueCuandoExisteEnvioParaOrden() {
        guardarEnvio(10L, "SEG-001", "PENDIENTE");

        boolean resultado = envioRepository.existsByIdOrden(10L);

        assertThat(resultado).isTrue();
    }

    @Test
    void existsByIdOrden_debeRetornarFalseCuandoNoExisteEnvioParaOrden() {
        guardarEnvio(10L, "SEG-001", "PENDIENTE");

        boolean resultado = envioRepository.existsByIdOrden(99L);

        assertThat(resultado).isFalse();
    }

    @Test
    void save_debePersistirEnvio() {
        EnvioModel resultado = guardarEnvio(10L, "SEG-001", "PENDIENTE");

        assertThat(resultado.getIdEnvio()).isNotNull();
        assertThat(resultado.getDireccion()).isEqualTo("Av. Principal 123");
        assertThat(resultado.getEstado()).isEqualTo("PENDIENTE");
    }
}
