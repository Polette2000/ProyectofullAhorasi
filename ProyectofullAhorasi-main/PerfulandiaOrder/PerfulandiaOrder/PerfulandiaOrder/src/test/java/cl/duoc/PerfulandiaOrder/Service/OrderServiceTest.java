package cl.duoc.PerfulandiaOrder.Service;

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

import cl.duoc.PerfulandiaOrder.Client.ProductoClient;
import cl.duoc.PerfulandiaOrder.Client.UsuarioClient;
import cl.duoc.PerfulandiaOrder.Exception.ResourceNotFoundException;
import cl.duoc.PerfulandiaOrder.Model.OrderItem;
import cl.duoc.PerfulandiaOrder.Model.OrderModel;
import cl.duoc.PerfulandiaOrder.Repository.OrderRepository;
import cl.duoc.PerfulandiaOrder.dto.Request.OrderCreateRequest;
import cl.duoc.PerfulandiaOrder.dto.Request.OrderItemCreateRequest;
import cl.duoc.PerfulandiaOrder.dto.Request.OrderItemUpdateRequest;
import cl.duoc.PerfulandiaOrder.dto.Response.OrderResponse;
import cl.duoc.PerfulandiaOrder.dto.Response.ProductoResponse;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ProductoClient productoClient;

    @Mock
    private UsuarioClient usuarioClient;

    @InjectMocks
    private OrderService orderService;

    private ProductoResponse crearProducto() {
        ProductoResponse producto = new ProductoResponse();
        producto.setIdProducto(10L);
        producto.setNombre("Perfume test");
        producto.setPrecio(5000);
        return producto;
    }

    private OrderModel crearPedido() {
        OrderModel pedido = OrderModel.builder()
                .idPedido(1L)
                .idUsuario(5L)
                .fechaCreacion(LocalDateTime.now())
                .estado("CREADO")
                .items(List.of())
                .build();

        OrderItem item = OrderItem.builder()
                .idItem(2L)
                .idProducto(10L)
                .cantidad(2)
                .precio(5000)
                .pedido(pedido)
                .build();
        pedido.setItems(List.of(item));
        return pedido;
    }

    @Test
    void crearPedido_debeCrearPedidoCuandoUsuarioYProductoExisten() {
        OrderCreateRequest request = new OrderCreateRequest(List.of(new OrderItemCreateRequest(10L, 2)));
        when(productoClient.obtenerProductoPorId(10L)).thenReturn(crearProducto());
        when(orderRepository.save(any(OrderModel.class))).thenReturn(crearPedido());

        OrderResponse response = orderService.crearPedido(5L, request);

        assertThat(response.getIdPedido()).isEqualTo(1L);
        assertThat(response.getIdUsuario()).isEqualTo(5L);
        assertThat(response.getEstado()).isEqualTo("CREADO");
        assertThat(response.getItems()).hasSize(1);
        assertThat(response.getItems().get(0).getNombreProducto()).isEqualTo("Perfume test");
        verify(usuarioClient).obtenerUsuarioPorId(5L);
        verify(orderRepository).save(any(OrderModel.class));
    }

    @Test
    void crearPedido_debeLanzarExcepcionCuandoUsuarioNoExiste() {
        OrderCreateRequest request = new OrderCreateRequest(List.of(new OrderItemCreateRequest(10L, 2)));
        when(usuarioClient.obtenerUsuarioPorId(99L))
                .thenThrow(new ResourceNotFoundException("Usuario no encontrado con ID: 99"));

        assertThatThrownBy(() -> orderService.crearPedido(99L, request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Usuario no encontrado con ID: 99");
        verify(orderRepository, never()).save(any(OrderModel.class));
    }

    @Test
    void obtenerPedidoPorId_debeRetornarPedidoCuandoExiste() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(crearPedido()));
        when(productoClient.obtenerProductoPorId(10L)).thenReturn(crearProducto());

        OrderResponse response = orderService.obtenerPedidoPorId(1L);

        assertThat(response.getIdPedido()).isEqualTo(1L);
        assertThat(response.getItems().get(0).getIdProducto()).isEqualTo(10L);
    }

    @Test
    void actualizarItemPedido_debeActualizarCantidadCuandoItemExiste() {
        OrderModel pedido = crearPedido();
        OrderItemUpdateRequest request = new OrderItemUpdateRequest(4);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(pedido));
        when(orderRepository.save(pedido)).thenReturn(pedido);
        when(productoClient.obtenerProductoPorId(10L)).thenReturn(crearProducto());

        OrderResponse response = orderService.actualizarItemPedido(1L, 2L, request);

        assertThat(response.getItems().get(0).getCantidad()).isEqualTo(4);
        verify(orderRepository).save(pedido);
    }

    @Test
    void eliminarPedido_debeLanzarExcepcionCuandoNoExiste() {
        when(orderRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> orderService.eliminarPedido(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Pedido no encontrado con ID: 99");
        verify(orderRepository, never()).deleteById(99L);
    }
}
