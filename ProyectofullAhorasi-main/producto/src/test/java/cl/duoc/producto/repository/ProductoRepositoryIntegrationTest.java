package cl.duoc.producto.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import cl.duoc.producto.model.Producto;

@DataJpaTest
@ActiveProfiles("test")
class ProductoRepositoryIntegrationTest {

    @Autowired
    private ProductoRepository productoRepository;

    @BeforeEach
    void limpiarBaseDeDatos() {
        productoRepository.deleteAll();
    }

    @Test
    void guardarYBuscarProductoPorId() {
        Producto guardado = productoRepository.save(Producto.builder()
                .nombre("Perfume Integracion")
                .descripcion("Producto guardado con JPA y H2")
                .precio(21990)
                .build());

        Producto encontrado = productoRepository.findById(guardado.getIdProducto()).orElseThrow();

        assertThat(encontrado.getNombre()).isEqualTo("Perfume Integracion");
        assertThat(encontrado.getPrecio()).isEqualTo(21990);
    }

    @Test
    void buscarProductosPorNombreSinDistinguirMayusculas() {
        productoRepository.save(Producto.builder()
                .nombre("Perfume Floral")
                .descripcion("Aroma floral")
                .precio(18990)
                .build());

        List<Producto> encontrados =
                productoRepository.findByNombreContainingIgnoreCase("floral");

        assertThat(encontrados)
                .extracting(Producto::getNombre)
                .containsExactly("Perfume Floral");
    }

    @Test
    void eliminarProductoPersistido() {
        Producto guardado = productoRepository.save(Producto.builder()
                .nombre("Perfume Eliminable")
                .descripcion("Producto de prueba")
                .precio(9990)
                .build());

        productoRepository.deleteById(guardado.getIdProducto());

        assertThat(productoRepository.findById(guardado.getIdProducto())).isEmpty();
    }
}
