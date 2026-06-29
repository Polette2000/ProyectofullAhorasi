package cl.duoc.producto.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import cl.duoc.producto.dto.request.ProductoCreateRequest;
import cl.duoc.producto.dto.request.ProductoUpdateRequest;
import cl.duoc.producto.dto.response.ProductoResponse;
import cl.duoc.producto.service.ProductoService;

@ExtendWith(MockitoExtension.class)
class ProductoControllerTest {

    @Mock
    private ProductoService productoService;

    @InjectMocks
    private ProductoController productoController;

    @Test
    void listarProductosDebeResponderOk() {
        when(productoService.listarProductos()).thenReturn(List.of(producto()));

        ResponseEntity<List<ProductoResponse>> response = productoController.listarProductos();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).containsExactly(producto());
    }

    @Test
    void buscarPorIdDebeResponderOk() {
        when(productoService.buscarPorId(1L)).thenReturn(producto());

        ResponseEntity<ProductoResponse> response = productoController.buscarPorId(1L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(producto());
    }

    @Test
    void crearProductoDebeResponderCreated() {
        ProductoCreateRequest request = new ProductoCreateRequest("Perfume", "Descripcion", 10000);
        when(productoService.crearProducto(request)).thenReturn(producto());

        ResponseEntity<ProductoResponse> response = productoController.crearProducto(request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isEqualTo(producto());
    }

    @Test
    void actualizarProductoDebeResponderOk() {
        ProductoUpdateRequest request = new ProductoUpdateRequest("Perfume", "Descripcion", 10000);
        when(productoService.actualizarProducto(1L, request)).thenReturn(producto());

        ResponseEntity<ProductoResponse> response = productoController.actualizarProducto(1L, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(producto());
    }

    @Test
    void eliminarProductoDebeResponderOk() {
        ResponseEntity<String> response = productoController.eliminarProducto(1L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo("Producto eliminado correctamente");
        verify(productoService).eliminarProducto(1L);
    }

    @Test
    void buscarPorNombreDebeResponderOk() {
        when(productoService.buscarPorNombre("floral")).thenReturn(List.of(producto()));

        ResponseEntity<List<ProductoResponse>> response = productoController.buscarPorNombre("floral");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).containsExactly(producto());
    }

    private ProductoResponse producto() {
        return ProductoResponse.builder()
                .idProducto(1L)
                .nombre("Perfume")
                .descripcion("Descripcion")
                .precio(10000)
                .build();
    }
}
