package cl.duoc.PerfulandiaOrder.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import cl.duoc.PerfulandiaOrder.Client.ProductoClient;
import cl.duoc.PerfulandiaOrder.Client.UsuarioClient;
import cl.duoc.PerfulandiaOrder.Exception.ResourceNotFoundException;
import cl.duoc.PerfulandiaOrder.Model.OrderItem;
import cl.duoc.PerfulandiaOrder.Model.OrderModel;
import cl.duoc.PerfulandiaOrder.Repository.OrderRepository;
import cl.duoc.PerfulandiaOrder.dto.Request.OrderCreateRequest;
import cl.duoc.PerfulandiaOrder.dto.Request.OrderItemUpdateRequest;
import cl.duoc.PerfulandiaOrder.dto.Request.OrderUpdateRequest;
import cl.duoc.PerfulandiaOrder.dto.Response.OrderItemResponse;
import cl.duoc.PerfulandiaOrder.dto.Response.OrderResponse;
import cl.duoc.PerfulandiaOrder.dto.Response.ProductoResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductoClient productoClient;
    private final UsuarioClient usuarioClient;

    private OrderResponse mapToDTO(OrderModel pedido) {
        return new OrderResponse(
                pedido.getIdPedido(),
                pedido.getIdUsuario(),
                pedido.getFechaCreacion(),
                pedido.getEstado(),
                pedido.getItems().stream()
                        .map(this::mapItemToDTO)
                        .collect(Collectors.toList())
        );
    }

    private OrderItemResponse mapItemToDTO(OrderItem item) {
        ProductoResponse producto = productoClient.obtenerProductoPorId(item.getIdProducto());

        return OrderItemResponse.builder()
                .idItem(item.getIdItem())
                .idProducto(item.getIdProducto())
                .nombreProducto(producto.getNombre())
                .cantidad(item.getCantidad())
                .precio(item.getPrecio())
                .build();
    }

    public OrderResponse crearPedido(Long idUsuario, OrderCreateRequest request) {
        log.info("Creando pedido para usuario ID: {}", idUsuario);
        usuarioClient.obtenerUsuarioPorId(idUsuario);

        List<OrderItem> items = request.getItems().stream()
                .map(r -> {
                    ProductoResponse producto = productoClient.obtenerProductoPorId(r.getIdProducto());

                    return OrderItem.builder()
                            .idProducto(r.getIdProducto())
                            .cantidad(r.getCantidad())
                            .precio(producto.getPrecio())
                            .build();
                })
                .collect(Collectors.toList());

        OrderModel pedido = OrderModel.builder()
                .idUsuario(idUsuario)
                .fechaCreacion(LocalDateTime.now())
                .estado("CREADO")
                .items(items)
                .build();

        items.forEach(i -> i.setPedido(pedido));

        OrderModel saved = orderRepository.save(pedido);
        log.info("Pedido creado con ID: {}", saved.getIdPedido());

        return mapToDTO(saved);
    }

    public List<OrderResponse> obtenerPedidosPorUsuario(Long idUsuario) {
        log.info("Buscando pedidos del usuario ID: {}", idUsuario);
        usuarioClient.obtenerUsuarioPorId(idUsuario);

        List<OrderResponse> respuesta = orderRepository.findByIdUsuario(idUsuario)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());

        log.info("Cantidad de pedidos encontrados para usuario ID: {} = {}", idUsuario, respuesta.size());
        return respuesta;
    }

    public OrderResponse obtenerPedidoPorId(Long idPedido) {
        log.info("Buscando pedido ID: {}", idPedido);

        return orderRepository.findById(idPedido)
                .map(this::mapToDTO)
                .orElseThrow(() -> {
                    log.warn("Pedido no encontrado ID: {}", idPedido);
                    return new ResourceNotFoundException("Pedido no encontrado con ID: " + idPedido);
                });
    }

    public OrderResponse actualizarEstadoPedido(Long idPedido, OrderUpdateRequest request) {
        log.info("Actualizando estado del pedido ID: {} a '{}'", idPedido, request.getEstado());

        OrderModel pedido = orderRepository.findById(idPedido)
                .orElseThrow(() -> {
                    log.warn("Pedido no encontrado ID: {}", idPedido);
                    return new ResourceNotFoundException("Pedido no encontrado con ID: " + idPedido);
                });

        pedido.setEstado(request.getEstado());

        OrderModel saved = orderRepository.save(pedido);
        log.info("Estado del pedido ID: {} actualizado correctamente", saved.getIdPedido());
        return mapToDTO(saved);
    }

    public OrderResponse actualizarItemPedido(Long idPedido, Long idItem, OrderItemUpdateRequest request) {
        log.info("Actualizando item ID: {} del pedido ID: {}", idItem, idPedido);

        OrderModel pedido = orderRepository.findById(idPedido)
                .orElseThrow(() -> {
                    log.warn("Pedido no encontrado ID: {}", idPedido);
                    return new ResourceNotFoundException("Pedido no encontrado con ID: " + idPedido);
                });

        OrderItem item = pedido.getItems().stream()
                .filter(i -> i.getIdItem().equals(idItem))
                .findFirst()
                .orElseThrow(() -> {
                    log.warn("Item no encontrado ID: {} en pedido ID: {}", idItem, idPedido);
                    return new ResourceNotFoundException("Item no encontrado con ID: " + idItem);
                });

        item.setCantidad(request.getCantidad());

        OrderModel saved = orderRepository.save(pedido);
        log.info("Item ID: {} del pedido ID: {} actualizado correctamente", idItem, saved.getIdPedido());
        return mapToDTO(saved);
    }

    public void eliminarPedido(Long idPedido) {
        log.info("Eliminando pedido ID: {}", idPedido);

        if (!orderRepository.existsById(idPedido)) {
            log.warn("Pedido no encontrado ID: {}", idPedido);
            throw new ResourceNotFoundException("Pedido no encontrado con ID: " + idPedido);
        }

        orderRepository.deleteById(idPedido);
        log.info("Pedido ID: {} eliminado correctamente", idPedido);
    }
}
