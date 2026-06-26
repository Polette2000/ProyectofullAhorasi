package cl.duoc.PerfulandiaOrder.Controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cl.duoc.PerfulandiaOrder.Service.OrderService;
import cl.duoc.PerfulandiaOrder.dto.Request.OrderCreateRequest;
import cl.duoc.PerfulandiaOrder.dto.Request.OrderItemUpdateRequest;
import cl.duoc.PerfulandiaOrder.dto.Request.OrderUpdateRequest;
import cl.duoc.PerfulandiaOrder.dto.Response.OrderResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/pedidos")
@RequiredArgsConstructor
@Tag(name = "Pedidos", description = "Gestion de pedidos y sus items")
public class OrderController {

    private final OrderService orderService;

    private static final String ORDER_REQUEST_EXAMPLE = """
            {
              "items": [
                {
                  "idProducto": 10,
                  "cantidad": 2
                }
              ]
            }
            """;

    private static final String ORDER_RESPONSE_EXAMPLE = """
            {
              "idPedido": 1,
              "idUsuario": 5,
              "fechaCreacion": "2026-06-25T19:30:00",
              "estado": "CREADO",
              "items": [
                {
                  "idItem": 1,
                  "idProducto": 10,
                  "nombreProducto": "Perfume Floral",
                  "cantidad": 2,
                  "precio": 12990.0
                }
              ]
            }
            """;

    private static final String ORDERS_LIST_RESPONSE_EXAMPLE = """
            [
              {
                "idPedido": 1,
                "idUsuario": 5,
                "fechaCreacion": "2026-06-25T19:30:00",
                "estado": "CREADO",
                "items": [
                  {
                    "idItem": 1,
                    "idProducto": 10,
                    "nombreProducto": "Perfume Floral",
                    "cantidad": 2,
                    "precio": 12990.0
                  }
                ]
              }
            ]
            """;

    private static final String ESTADO_REQUEST_EXAMPLE = """
            {
              "estado": "PAGADO"
            }
            """;

    private static final String ITEM_UPDATE_REQUEST_EXAMPLE = """
            {
              "cantidad": 3
            }
            """;

    @PostMapping("/usuario/{idUsuario}")
    @Operation(summary = "Crear pedido", description = "Crea un pedido para un usuario con uno o mas productos.")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Items que tendra el pedido",
            content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(value = ORDER_REQUEST_EXAMPLE)))
    @ApiResponse(responseCode = "200", description = "Pedido creado",
            content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(value = ORDER_RESPONSE_EXAMPLE)))
    public ResponseEntity<OrderResponse> crearPedido(
            @PathVariable Long idUsuario,
            @Valid @RequestBody OrderCreateRequest request) {

        return ResponseEntity.ok(orderService.crearPedido(idUsuario, request));
    }

    @GetMapping("/usuario/{idUsuario}")
    @Operation(summary = "Listar pedidos de usuario", description = "Obtiene los pedidos asociados a un usuario.")
    @ApiResponse(responseCode = "200", description = "Pedidos encontrados",
            content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(value = ORDERS_LIST_RESPONSE_EXAMPLE)))
    public ResponseEntity<?> obtenerPedidosPorUsuario(
            @PathVariable Long idUsuario) {

        List<OrderResponse> pedidos = orderService.obtenerPedidosPorUsuario(idUsuario);

        if (pedidos.isEmpty()) {
            return ResponseEntity.ok("El usuario no tiene pedidos creados");
        }

        return ResponseEntity.ok(pedidos);
    }

    @GetMapping("/{idPedido}")
    @Operation(summary = "Buscar pedido por ID", description = "Obtiene el detalle de un pedido especifico.")
    @ApiResponse(responseCode = "200", description = "Pedido encontrado",
            content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(value = ORDER_RESPONSE_EXAMPLE)))
    public ResponseEntity<OrderResponse> obtenerPedidoPorId(
            @PathVariable Long idPedido) {

        return ResponseEntity.ok(orderService.obtenerPedidoPorId(idPedido));
    }

    @PutMapping("/{idPedido}/estado")
    @Operation(summary = "Actualizar estado del pedido", description = "Cambia el estado de un pedido existente.")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Nuevo estado del pedido",
            content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(value = ESTADO_REQUEST_EXAMPLE)))
    @ApiResponse(responseCode = "200", description = "Pedido actualizado",
            content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(value = ORDER_RESPONSE_EXAMPLE)))
    public ResponseEntity<OrderResponse> actualizarEstadoPedido(
            @PathVariable Long idPedido,
            @Valid @RequestBody OrderUpdateRequest request) {

        return ResponseEntity.ok(orderService.actualizarEstadoPedido(idPedido, request));
    }

    @PutMapping("/{idPedido}/items/{idItem}")
    @Operation(summary = "Actualizar item del pedido", description = "Actualiza la cantidad de un item dentro del pedido.")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Nueva cantidad del item",
            content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(value = ITEM_UPDATE_REQUEST_EXAMPLE)))
    @ApiResponse(responseCode = "200", description = "Item actualizado dentro del pedido",
            content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(value = ORDER_RESPONSE_EXAMPLE)))
    public ResponseEntity<OrderResponse> actualizarItemPedido(
            @PathVariable Long idPedido,
            @PathVariable Long idItem,
            @Valid @RequestBody OrderItemUpdateRequest request) {

        return ResponseEntity.ok(orderService.actualizarItemPedido(idPedido, idItem, request));
    }

    @DeleteMapping("/{idPedido}")
    @Operation(summary = "Eliminar pedido", description = "Elimina un pedido por ID.")
    @ApiResponse(responseCode = "200", description = "Pedido eliminado",
            content = @Content(mediaType = "text/plain",
                    examples = @ExampleObject(value = "Pedido eliminado correctamente")))
    public ResponseEntity<String> eliminarPedido(@PathVariable Long idPedido) {
        orderService.eliminarPedido(idPedido);
        return ResponseEntity.ok("Pedido eliminado correctamente");
    }
}
