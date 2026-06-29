package cl.duoc.producto.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import cl.duoc.producto.dto.request.ProductoCreateRequest;
import cl.duoc.producto.dto.request.ProductoUpdateRequest;
import cl.duoc.producto.dto.response.ProductoResponse;
import cl.duoc.producto.exception.ResourceNotFoundException;
import cl.duoc.producto.model.Producto;
import cl.duoc.producto.repository.ProductoRepository;

// Permite probar el service con Mockito sin levantar todo Spring Boot.
@ExtendWith(MockitoExtension.class)
class ProductoServiceTest {

    // Simula el repositorio para no depender de MySQL.
    @Mock
    private ProductoRepository productoRepository;

    // Crea ProductoService usando el repositorio simulado.
    @InjectMocks
    private ProductoService productoService;

    // Prueba 1: verifica que listarProductos devuelva todos los productos mapeados a DTO.
    @Test
    void listarProductosDebeRetornarListaDeProductos() {
        // Arrange: se simula que el repositorio encuentra un producto.
        Producto producto = crearProducto(1L, "Perfume Floral Primavera", "Perfume floral de 100 ml", 24990);
        when(productoRepository.findAll()).thenReturn(List.of(producto));

        // Act: se ejecuta el metodo real del service.
        List<ProductoResponse> resultado = productoService.listarProductos();

        // Assert: se valida que la respuesta contenga los datos esperados.
        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getIdProducto()).isEqualTo(1L);
        assertThat(resultado.get(0).getNombre()).isEqualTo("Perfume Floral Primavera");
        assertThat(resultado.get(0).getPrecio()).isEqualTo(24990);
    }

    // Prueba 2: verifica que buscarPorId retorne un producto cuando existe.
    @Test
    void buscarPorIdDebeRetornarProductoCuandoExiste() {
        // Arrange: se simula que existe el producto con ID 1.
        Producto producto = crearProducto(1L, "Perfume Citrico Verano", "Perfume citrico de 100 ml", 29990);
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));

        // Act: se busca el producto desde el service.
        ProductoResponse resultado = productoService.buscarPorId(1L);

        // Assert: se valida que el DTO tenga los datos correctos.
        assertThat(resultado.getIdProducto()).isEqualTo(1L);
        assertThat(resultado.getNombre()).isEqualTo("Perfume Citrico Verano");
        assertThat(resultado.getPrecio()).isEqualTo(29990);
    }

    // Prueba 3: verifica que crearProducto guarde correctamente un nuevo producto.
    @Test
    void crearProductoDebeGuardarYRetornarResponse() {
        // Arrange: se prepara el request y se simula el guardado con ID generado.
        ProductoCreateRequest request = new ProductoCreateRequest(
                "Perfume Dulce Noche",
                "Perfume dulce de 100 ml",
                19990);

        when(productoRepository.save(any(Producto.class))).thenAnswer(invocation -> {
            Producto producto = invocation.getArgument(0);
            producto.setIdProducto(10L);
            return producto;
        });

        // Act: se crea el producto desde el service.
        ProductoResponse resultado = productoService.crearProducto(request);

        // Assert: se valida que se guarde y que retorne el producto creado.
        assertThat(resultado.getIdProducto()).isEqualTo(10L);
        assertThat(resultado.getNombre()).isEqualTo("Perfume Dulce Noche");
        assertThat(resultado.getPrecio()).isEqualTo(19990);
        verify(productoRepository).save(any(Producto.class));
    }

    // Prueba 4: verifica que actualizarProducto cambie los datos cuando el producto existe.
    @Test
    void actualizarProductoDebeModificarDatosCuandoExiste() {
        // Arrange: se prepara un producto existente y un request con nuevos datos.
        Producto productoExistente = crearProducto(5L, "Nombre anterior", "Descripcion anterior", 10000);
        ProductoUpdateRequest request = new ProductoUpdateRequest(
                "Perfume Actualizado",
                "Descripcion actualizada",
                25990);

        when(productoRepository.findById(5L)).thenReturn(Optional.of(productoExistente));
        when(productoRepository.save(any(Producto.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act: se actualiza el producto.
        ProductoResponse resultado = productoService.actualizarProducto(5L, request);

        // Assert: se comprueba que los datos hayan cambiado y se haya guardado.
        assertThat(resultado.getNombre()).isEqualTo("Perfume Actualizado");
        assertThat(resultado.getDescripcion()).isEqualTo("Descripcion actualizada");
        assertThat(resultado.getPrecio()).isEqualTo(25990);
        verify(productoRepository).save(productoExistente);
    }

    // Prueba 5: verifica el error al eliminar un producto que no existe.
    @Test
    void eliminarProductoDebeFallarCuandoNoExiste() {
        // Arrange: se simula que no existe producto con ID 99.
        when(productoRepository.findById(99L)).thenReturn(Optional.empty());

        // Act y Assert: se espera ResourceNotFoundException con el mensaje del service.
        assertThatThrownBy(() -> productoService.eliminarProducto(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Producto no encontrado con ID: 99");

        // Verifica que no se elimina nada si no existe.
        verify(productoRepository, never()).delete(any(Producto.class));
    }

    @Test
    void eliminarProductoDebeEliminarCuandoExiste() {
        Producto producto = crearProducto(7L, "Perfume Eliminable", "Descripcion", 15990);
        when(productoRepository.findById(7L)).thenReturn(Optional.of(producto));

        productoService.eliminarProducto(7L);

        verify(productoRepository).delete(producto);
    }

    @Test
    void buscarPorNombreDebeRetornarCoincidencias() {
        Producto producto = crearProducto(3L, "Perfume Floral", "Aroma floral", 18990);
        when(productoRepository.findByNombreContainingIgnoreCase("floral"))
                .thenReturn(List.of(producto));

        List<ProductoResponse> resultado = productoService.buscarPorNombre("floral");

        assertThat(resultado)
                .extracting(ProductoResponse::getNombre)
                .containsExactly("Perfume Floral");
    }

    @Test
    void buscarPorIdDebeFallarCuandoNoExiste() {
        when(productoRepository.findById(404L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productoService.buscarPorId(404L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Producto no encontrado con ID: 404");
    }

    @Test
    void actualizarProductoDebeFallarCuandoNoExiste() {
        ProductoUpdateRequest request = new ProductoUpdateRequest(
                "Perfume inexistente",
                "Descripcion",
                10000);
        when(productoRepository.findById(404L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productoService.actualizarProducto(404L, request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Producto no encontrado con ID: 404");
        verify(productoRepository, never()).save(any(Producto.class));
    }

    // Metodo auxiliar para no repetir la construccion de productos en cada test.
    private Producto crearProducto(Long idProducto, String nombre, String descripcion, Integer precio) {
        return Producto.builder()
                .idProducto(idProducto)
                .nombre(nombre)
                .descripcion(descripcion)
                .precio(precio)
                .build();
    }
}
