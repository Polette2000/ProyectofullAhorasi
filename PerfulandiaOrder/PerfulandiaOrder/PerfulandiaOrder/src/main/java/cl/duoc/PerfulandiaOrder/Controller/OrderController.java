package cl.duoc.PerfulandiaOrder.Controller;

import cl.duoc.PerfulandiaOrder.dto.Request.OrderCreateRequest;
import cl.duoc.PerfulandiaOrder.dto.Request.OrderItemUpdateRequest;
import cl.duoc.PerfulandiaOrder.dto.Request.OrderUpdateRequest;
import cl.duoc.PerfulandiaOrder.dto.Response.OrderResponse;
import cl.duoc.PerfulandiaOrder.Service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/pedidos")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    // Crear un nuevo pedido
    @PostMapping("/usuario/{idUsuario}")
    public ResponseEntity<OrderResponse> crearPedido(
            @PathVariable Long idUsuario,
            @Valid @RequestBody OrderCreateRequest request) {

        return ResponseEntity.ok(orderService.crearPedido(idUsuario, request));
    }

    // Obtener todos los pedidos de un usuario
    @GetMapping("/usuario/{idUsuario}")
    public ResponseEntity<List<OrderResponse>> obtenerPedidosPorUsuario(
            @PathVariable Long idUsuario) {

        return ResponseEntity.ok(orderService.obtenerPedidosPorUsuario(idUsuario));
    }

    // Obtener un pedido específico por ID
    @GetMapping("/{idPedido}")
    public ResponseEntity<OrderResponse> obtenerPedidoPorId(
            @PathVariable Long idPedido) {

        return ResponseEntity.ok(orderService.obtenerPedidoPorId(idPedido));
    }

    // Actualizar estado de un pedido
    @PutMapping("/{idPedido}/estado")
    public ResponseEntity<OrderResponse> actualizarEstadoPedido(
            @PathVariable Long idPedido,
            @Valid @RequestBody OrderUpdateRequest request) {

        return ResponseEntity.ok(orderService.actualizarEstadoPedido(idPedido, request));
    }

    // Actualizar un ítem dentro de un pedido
    @PutMapping("/{idPedido}/items/{idItem}")
    public ResponseEntity<OrderResponse> actualizarItemPedido(
            @PathVariable Long idPedido,
            @PathVariable Long idItem,
            @Valid @RequestBody OrderItemUpdateRequest request) {

        return ResponseEntity.ok(orderService.actualizarItemPedido(idPedido, idItem, request));
    }

    // Eliminar un pedido
    @DeleteMapping("/{idPedido}")
    public ResponseEntity<Void> eliminarPedido(@PathVariable Long idPedido) {
        orderService.eliminarPedido(idPedido);
        return ResponseEntity.noContent().build();
    }
}
