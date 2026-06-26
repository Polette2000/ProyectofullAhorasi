package cl.duoc.PerfulandiaCart.controller;

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

import cl.duoc.PerfulandiaCart.dto.request.CarritoCreateRequest;
import cl.duoc.PerfulandiaCart.dto.request.CarritoUpdateRequest;
import cl.duoc.PerfulandiaCart.dto.response.ItemCarritoResponse;
import cl.duoc.PerfulandiaCart.service.CarritoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/carrito")
@RequiredArgsConstructor
@Tag(name = "Carrito", description = "Gestion del carrito de compras")
public class CarritoController {

    private final CarritoService carritoService;

    private static final String ITEM_RESPONSE_EXAMPLE = """
            {
              "idItemCarrito": 1,
              "idProducto": 10,
              "nombreProducto": "Perfume Floral",
              "precioProducto": 12990,
              "cantidad": 2,
              "subtotal": 25980,
              "idUsuario": 5
            }
            """;

    private static final String ITEMS_LIST_RESPONSE_EXAMPLE = """
            [
              {
                "idItemCarrito": 1,
                "idProducto": 10,
                "nombreProducto": "Perfume Floral",
                "precioProducto": 12990,
                "cantidad": 2,
                "subtotal": 25980,
                "idUsuario": 5
              }
            ]
            """;

    private static final String CREATE_REQUEST_EXAMPLE = """
            {
              "idProducto": 10,
              "cantidad": 2
            }
            """;

    private static final String UPDATE_REQUEST_EXAMPLE = """
            {
              "cantidad": 4
            }
            """;

    // Agrega un producto al carrito del usuario y devuelve el item guardado.
    @PostMapping("/{idUsuario}")
    @Operation(summary = "Agregar item al carrito", description = "Agrega un producto al carrito de un usuario.")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Producto y cantidad que se agregaran al carrito",
            content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(value = CREATE_REQUEST_EXAMPLE)))
    @ApiResponse(responseCode = "200", description = "Item agregado",
            content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(value = ITEM_RESPONSE_EXAMPLE)))
    public ItemCarritoResponse agregarItem(
            @PathVariable Long idUsuario,
            @Valid @RequestBody CarritoCreateRequest request) {
        return carritoService.agregarItem(idUsuario, request);
    }

    // Lista los items del carrito de un usuario.
    @GetMapping("/{idUsuario}")
    @Operation(summary = "Listar carrito", description = "Obtiene todos los productos del carrito de un usuario.")
    @ApiResponse(responseCode = "200", description = "Carrito encontrado",
            content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(value = ITEMS_LIST_RESPONSE_EXAMPLE)))
    public ResponseEntity<?> listarCarrito(@PathVariable Long idUsuario) {
        List<ItemCarritoResponse> carrito = carritoService.listarCarrito(idUsuario);
        if (carrito.isEmpty()) {
            return ResponseEntity.ok("El usuario no tiene productos en el carrito");
        }
        return ResponseEntity.ok(carrito);
    }

    // Actualiza la cantidad de un item del carrito y devuelve el item actualizado.
    @PutMapping("/{idUsuario}/items/{idItemCarrito}")
    @Operation(summary = "Actualizar item del carrito", description = "Actualiza la cantidad de un producto del carrito.")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Nueva cantidad del item",
            content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(value = UPDATE_REQUEST_EXAMPLE)))
    @ApiResponse(responseCode = "200", description = "Item actualizado",
            content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(value = ITEM_RESPONSE_EXAMPLE)))
    public ItemCarritoResponse actualizarItem(
            @PathVariable Long idUsuario,
            @PathVariable Long idItemCarrito,
            @Valid @RequestBody CarritoUpdateRequest request) {
        return carritoService.actualizarItem(idUsuario, idItemCarrito, request);
    }

    // Elimina un item del carrito y devuelve un mensaje de confirmacion.
    @DeleteMapping("/{idUsuario}/items/{idItemCarrito}")
    @Operation(summary = "Eliminar item del carrito", description = "Elimina un producto del carrito del usuario.")
    @ApiResponse(responseCode = "200", description = "Item eliminado",
            content = @Content(mediaType = "text/plain",
                    examples = @ExampleObject(value = "Item eliminado correctamente")))
    public String eliminarItem(
            @PathVariable Long idUsuario,
            @PathVariable Long idItemCarrito) {
        carritoService.eliminarItem(idUsuario, idItemCarrito);
        return "Item eliminado correctamente";
    }

}
