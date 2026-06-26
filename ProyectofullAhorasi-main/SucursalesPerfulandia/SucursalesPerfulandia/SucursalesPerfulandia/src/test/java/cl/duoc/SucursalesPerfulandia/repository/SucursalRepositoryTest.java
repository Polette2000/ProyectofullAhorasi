package cl.duoc.SucursalesPerfulandia.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import cl.duoc.SucursalesPerfulandia.model.Comuna;
import cl.duoc.SucursalesPerfulandia.model.Sucursal;

@DataJpaTest
@ActiveProfiles("test")
class SucursalRepositoryTest {

    @Autowired
    private SucursalRepository sucursalRepository;

    @Autowired
    private ComunaRepository comunaRepository;

    private Comuna guardarComuna(String nombre, String region) {
        Comuna comuna = new Comuna();
        comuna.setNombreComuna(nombre);
        comuna.setRegion(region);
        return comunaRepository.save(comuna);
    }

    private Sucursal guardarSucursal(Comuna comuna, String nombre) {
        Sucursal sucursal = new Sucursal();
        sucursal.setNombreSucursal(nombre);
        sucursal.setDireccion("Av. Principal 123");
        sucursal.setTelefono("+56912345678");
        sucursal.setHorarioAtencion("09:00 a 18:00");
        sucursal.setComuna(comuna);
        return sucursalRepository.save(sucursal);
    }

    @Test
    void findByComunaIdComuna_debeRetornarSucursalesDeLaComuna() {
        Comuna santiago = guardarComuna("Santiago", "Metropolitana");
        Comuna providencia = guardarComuna("Providencia", "Metropolitana");
        guardarSucursal(santiago, "Sucursal Centro");
        guardarSucursal(providencia, "Sucursal Providencia");

        List<Sucursal> resultado = sucursalRepository.findByComunaIdComuna(santiago.getIdComuna());

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getNombreSucursal()).isEqualTo("Sucursal Centro");
    }

    @Test
    void findByComunaIdComuna_debeRetornarListaVaciaCuandoNoHaySucursales() {
        Comuna santiago = guardarComuna("Santiago", "Metropolitana");

        List<Sucursal> resultado = sucursalRepository.findByComunaIdComuna(santiago.getIdComuna());

        assertThat(resultado).isEmpty();
    }

    @Test
    void save_debePersistirSucursal() {
        Comuna santiago = guardarComuna("Santiago", "Metropolitana");

        Sucursal resultado = guardarSucursal(santiago, "Sucursal Centro");

        assertThat(resultado.getIdSucursal()).isNotNull();
        assertThat(resultado.getComuna().getNombreComuna()).isEqualTo("Santiago");
    }
}
