package cl.duoc.PerfulandiaCart.service;

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

import cl.duoc.PerfulandiaCart.client.ProductoClient;
import cl.duoc.PerfulandiaCart.client.UsuarioClient;
import cl.duoc.PerfulandiaCart.dto.request.CarritoCreateRequest;
import cl.duoc.PerfulandiaCart.dto.request.CarritoUpdateRequest;
import cl.duoc.PerfulandiaCart.dto.response.ItemCarritoResponse;
import cl.duoc.PerfulandiaCart.dto.response.ProductoResponse;
import cl.duoc.PerfulandiaCart.exception.ResourceNotFoundException;
import cl.duoc.PerfulandiaCart.model.ItemCarrito;
import cl.duoc.PerfulandiaCart.repository.ItemCarritoRepository;

@ExtendWith(MockitoExtension.class)
class CarritoServiceTest {

    @Mock
    private ItemCarritoRepository itemCarritoRepository;

    @Mock
    private ProductoClient productoClient;

    @Mock
    private UsuarioClient usuarioClient;

    @InjectMocks
    private CarritoService carritoService;

    private ProductoResponse crearProducto() {
        ProductoResponse producto = new ProductoResponse();
        producto.setIdProducto(10L);
        producto.setNombre("Perfume test");
        producto.setPrecio(5000);
        return producto;
    }

    private ItemCarrito crearItem() {
        return ItemCarrito.builder()
                .idItemCarrito(1L)
                .idUsuario(5L)
                .idProducto(10L)
                .cantidad(2)
                .build();
    }

    @Test
    void agregarItem_debeCrearItemCuandoProductoNoExisteEnCarrito() {
        CarritoCreateRequest request = new CarritoCreateRequest(10L, 2);
        ProductoResponse producto = crearProducto();

        when(itemCarritoRepository.findByIdUsuarioAndIdProducto(5L, 10L)).thenReturn(Optional.empty());
        when(productoClient.obtenerProductoPorId(10L)).thenReturn(producto);
        when(itemCarritoRepository.save(any(ItemCarrito.class))).thenAnswer(invocation -> {
            ItemCarrito item = invocation.getArgument(0);
            item.setIdItemCarrito(1L);
            return item;
        });

        ItemCarritoResponse response = carritoService.agregarItem(5L, request);

        assertThat(response).isNotNull();
        assertThat(response.getIdItemCarrito()).isEqualTo(1L);
        assertThat(response.getNombreProducto()).isEqualTo("Perfume test");
        assertThat(response.getCantidad()).isEqualTo(2);
        assertThat(response.getSubtotal()).isEqualTo(10000);
        verify(usuarioClient).obtenerUsuarioPorId(5L);
        verify(itemCarritoRepository).save(any(ItemCarrito.class));
    }

    @Test
    void agregarItem_debeSumarCantidadCuandoProductoYaExisteEnCarrito() {
        CarritoCreateRequest request = new CarritoCreateRequest(10L, 3);
        ProductoResponse producto = crearProducto();
        ItemCarrito itemExistente = crearItem();

        when(itemCarritoRepository.findByIdUsuarioAndIdProducto(5L, 10L))
                .thenReturn(Optional.of(itemExistente));
        when(productoClient.obtenerProductoPorId(10L)).thenReturn(producto);
        when(itemCarritoRepository.save(itemExistente)).thenReturn(itemExistente);

        ItemCarritoResponse response = carritoService.agregarItem(5L, request);

        assertThat(response.getCantidad()).isEqualTo(5);
        assertThat(response.getSubtotal()).isEqualTo(25000);
        verify(itemCarritoRepository).save(itemExistente);
    }

    @Test
    void agregarItem_debeLanzarExcepcionCuandoUsuarioNoExiste() {
        CarritoCreateRequest request = new CarritoCreateRequest(10L, 2);
        when(usuarioClient.obtenerUsuarioPorId(99L))
                .thenThrow(new ResourceNotFoundException("Usuario no encontrado con ID: 99"));

        assertThatThrownBy(() -> carritoService.agregarItem(99L, request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Usuario no encontrado con ID: 99");
        verify(itemCarritoRepository, never()).save(any(ItemCarrito.class));
    }

    @Test
    void listarCarrito_debeRetornarItemsDelUsuario() {
        ItemCarrito item = crearItem();
        ProductoResponse producto = crearProducto();

        when(itemCarritoRepository.findByIdUsuario(5L)).thenReturn(List.of(item));
        when(productoClient.obtenerProductoPorId(10L)).thenReturn(producto);

        List<ItemCarritoResponse> response = carritoService.listarCarrito(5L);

        assertThat(response).hasSize(1);
        assertThat(response.get(0).getIdItemCarrito()).isEqualTo(1L);
        assertThat(response.get(0).getSubtotal()).isEqualTo(10000);
        verify(usuarioClient).obtenerUsuarioPorId(5L);
        verify(itemCarritoRepository).findByIdUsuario(5L);
    }

    @Test
    void actualizarItem_debeActualizarCantidadCuandoItemExiste() {
        ItemCarrito item = crearItem();
        ProductoResponse producto = crearProducto();
        CarritoUpdateRequest request = new CarritoUpdateRequest(4);

        when(itemCarritoRepository.findByIdItemCarritoAndIdUsuario(1L, 5L))
                .thenReturn(Optional.of(item));
        when(itemCarritoRepository.save(item)).thenReturn(item);
        when(productoClient.obtenerProductoPorId(10L)).thenReturn(producto);

        ItemCarritoResponse response = carritoService.actualizarItem(5L, 1L, request);

        assertThat(response.getCantidad()).isEqualTo(4);
        assertThat(response.getSubtotal()).isEqualTo(20000);
        verify(itemCarritoRepository).save(item);
    }
}
