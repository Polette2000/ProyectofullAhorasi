package cl.duoc.PerfulandiaOrder.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import cl.duoc.PerfulandiaOrder.Client.ProductoClient;
import cl.duoc.PerfulandiaOrder.Repository.OrderRepository;
import cl.duoc.PerfulandiaOrder.Model.OrderModel;
import cl.duoc.PerfulandiaOrder.Model.OrderItem;
import cl.duoc.PerfulandiaOrder.Exception.ResourceNotFoundException;
import cl.duoc.PerfulandiaOrder.dto.Request.OrderCreateRequest;
import cl.duoc.PerfulandiaOrder.dto.Request.OrderUpdateRequest;
import cl.duoc.PerfulandiaOrder.dto.Request.OrderItemUpdateRequest;
import cl.duoc.PerfulandiaOrder.dto.Response.OrderResponse;
import cl.duoc.PerfulandiaOrder.dto.Response.OrderItemResponse;
import cl.duoc.PerfulandiaOrder.dto.Response.ProductoResponse;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final ProductoClient productoClient;

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

        return orderRepository.findByIdUsuario(idUsuario)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public OrderResponse obtenerPedidoPorId(Long idPedido) {
        log.info("Buscando pedido ID: {}", idPedido);

        return orderRepository.findById(idPedido)
                .map(this::mapToDTO)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Pedido no encontrado con ID: " + idPedido));
    }

    public OrderResponse actualizarEstadoPedido(Long idPedido, OrderUpdateRequest request) {
        log.info("Actualizando estado del pedido ID: {} a '{}'", idPedido, request.getEstado());

        OrderModel pedido = orderRepository.findById(idPedido)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Pedido no encontrado con ID: " + idPedido));

        pedido.setEstado(request.getEstado());

        return mapToDTO(orderRepository.save(pedido));
    }

    public OrderResponse actualizarItemPedido(Long idPedido, Long idItem, OrderItemUpdateRequest request) {
        log.info("Actualizando ítem ID: {} del pedido ID: {}", idItem, idPedido);

        OrderModel pedido = orderRepository.findById(idPedido)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Pedido no encontrado con ID: " + idPedido));

        OrderItem item = pedido.getItems().stream()
                .filter(i -> i.getIdItem().equals(idItem))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Ítem no encontrado con ID: " + idItem));

        item.setCantidad(request.getCantidad());

        return mapToDTO(orderRepository.save(pedido));
    }

    public void eliminarPedido(Long idPedido) {
        log.info("Eliminando pedido ID: {}", idPedido);

        if (!orderRepository.existsById(idPedido)) {
            throw new ResourceNotFoundException(
                    "Pedido no encontrado con ID: " + idPedido);
        }

        orderRepository.deleteById(idPedido);
        log.info("Pedido ID: {} eliminado correctamente", idPedido);
    }
}
