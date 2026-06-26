package cl.duoc.Inventory.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cl.duoc.Inventory.dto.DtoApiError;
import cl.duoc.Inventory.dto.request.InventoryCreateRequest;
import cl.duoc.Inventory.dto.request.InventoryUpdateRequest;
import cl.duoc.Inventory.dto.response.InventoryResponse;
import cl.duoc.Inventory.service.InventoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

// Indica que esta clase es un controlador REST
@RestController
// Define la ruta base del controlador
@RequestMapping("/api/v1/inventory")
// Genera constructor con atributos final
@RequiredArgsConstructor
// Documentacion Swagger del controlador
@Tag(name = "Inventory Controller", description = "Endpoints para gestionar el inventario de productos en sucursales")
// Indica en Swagger que los endpoints requieren token JWT
@SecurityRequirement(name = "bearerAuth")
public class InventoryController {

    // Inyecta el servicio de inventario
    private final InventoryService inventoryService;

    private static final String INVENTORY_RESPONSE_EXAMPLE = """
            {
              "idInventory": 1,
              "idProducto": 1,
              "nombreProducto": "Perfume Floral Primavera",
              "idSucursal": 1,
              "nombreSucursal": "Sucursal Centro",
              "stockDisponible": 25,
              "fechaActualizacion": "2026-06-25T19:30:00"
            }
            """;

    private static final String INVENTORY_LIST_RESPONSE_EXAMPLE = """
            [
              {
                "idInventory": 1,
                "idProducto": 1,
                "nombreProducto": "Perfume Floral Primavera",
                "idSucursal": 1,
                "nombreSucursal": "Sucursal Centro",
                "stockDisponible": 25,
                "fechaActualizacion": "2026-06-25T19:30:00"
              }
            ]
            """;

    private static final String INVENTORY_REQUEST_EXAMPLE = """
            {
              "idProducto": 1,
              "idSucursal": 1,
              "stockDisponible": 25
            }
            """;

    // Define endpoint GET /api/v1/inventory
    @GetMapping
    // Documentacion Swagger
    @Operation(summary = "Listar inventario", description = "Obtiene todos los registros de inventario disponibles")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operacion exitosa",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            array = @ArraySchema(schema = @Schema(implementation = InventoryResponse.class)),
                            examples = @ExampleObject(value = INVENTORY_LIST_RESPONSE_EXAMPLE))),
            @ApiResponse(responseCode = "401", description = "Token JWT no enviado o invalido", content = @Content),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    public ResponseEntity<?> listarInventario() {

        // Llama al servicio para obtener todos los inventarios
        List<InventoryResponse> inventario = inventoryService.listarInventario();

        // Si no existen registros, devuelve mensaje informativo
        if (inventario.isEmpty()) {
            return ResponseEntity.ok("No hay inventario registrado");
        }

        // Retorna HTTP 200 OK con la lista de inventario
        return ResponseEntity.ok(inventario);
    }

    // Define endpoint POST /api/v1/inventory
    @PostMapping
    // Documentacion Swagger
    @Operation(summary = "Crear inventario", description = "Registra stock de un producto en una sucursal")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Inventario creado correctamente",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = InventoryResponse.class),
                            examples = @ExampleObject(value = INVENTORY_RESPONSE_EXAMPLE))),
            @ApiResponse(responseCode = "400", description = "Datos de entrada invalidos",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = DtoApiError.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "timestamp": "2026-06-25",
                                      "status": 400,
                                      "error": "Bad Request",
                                      "message": "El stock disponible debe ser mayor o igual a cero",
                                      "path": "/api/v1/inventory",
                                      "claseException": "MethodArgumentNotValidException"
                                    }
                                    """))),
            @ApiResponse(responseCode = "401", description = "Token JWT no enviado o invalido", content = @Content),
            @ApiResponse(responseCode = "404", description = "Producto o sucursal no encontrada",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = DtoApiError.class))),
            @ApiResponse(responseCode = "409", description = "Ya existe inventario para ese producto y sucursal",
                    content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE,
                            schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    public ResponseEntity<InventoryResponse> crearInventario(

            // Recibe el JSON del body y valida sus campos
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Datos del inventario a crear",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(value = INVENTORY_REQUEST_EXAMPLE)))
            @Valid @RequestBody InventoryCreateRequest request
    ) {

        // Llama al servicio para crear el inventario
        InventoryResponse inventarioCreado = inventoryService.crearInventario(request);

        // Retorna HTTP 201 Created con el inventario creado
        return ResponseEntity.status(HttpStatus.CREATED).body(inventarioCreado);
    }

    // Define endpoint PUT /api/v1/inventory
    @PutMapping
    // Documentacion Swagger
    @Operation(summary = "Actualizar inventario", description = "Actualiza el stock de un producto en una sucursal")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Inventario actualizado correctamente",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = InventoryResponse.class),
                            examples = @ExampleObject(value = INVENTORY_RESPONSE_EXAMPLE))),
            @ApiResponse(responseCode = "400", description = "Datos de entrada invalidos",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = DtoApiError.class))),
            @ApiResponse(responseCode = "401", description = "Token JWT no enviado o invalido", content = @Content),
            @ApiResponse(responseCode = "404", description = "Inventario, producto o sucursal no encontrada",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = DtoApiError.class))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    public ResponseEntity<InventoryResponse> actualizarInventario(

            // Recibe el JSON del body y valida sus campos
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Datos del inventario a actualizar",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(value = INVENTORY_REQUEST_EXAMPLE)))
            @Valid @RequestBody InventoryUpdateRequest request
    ) {

        // Llama al servicio para actualizar el inventario
        InventoryResponse inventarioActualizado = inventoryService.actualizarInventario(request);

        // Retorna HTTP 200 OK con el inventario actualizado
        return ResponseEntity.ok(inventarioActualizado);
    }

    // Define endpoint DELETE /api/v1/inventory/producto/{idProducto}/sucursal/{idSucursal}
    @DeleteMapping("/producto/{idProducto}/sucursal/{idSucursal}")
    // Documentacion Swagger
    @Operation(summary = "Eliminar inventario", description = "Elimina el inventario asociado a un producto y una sucursal")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Inventario eliminado correctamente",
                    content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE,
                            schema = @Schema(implementation = String.class),
                            examples = @ExampleObject(value = "Inventario eliminado correctamente"))),
            @ApiResponse(responseCode = "401", description = "Token JWT no enviado o invalido", content = @Content),
            @ApiResponse(responseCode = "404", description = "Inventario no encontrado",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = DtoApiError.class))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    public ResponseEntity<String> eliminarInventario(

            // Recibe el ID del producto desde la URL
            @Parameter(description = "ID del producto", example = "1")
            @PathVariable Long idProducto,

            // Recibe el ID de la sucursal desde la URL
            @Parameter(description = "ID de la sucursal", example = "1")
            @PathVariable Long idSucursal
    ) {

        // Llama al servicio para eliminar el inventario
        inventoryService.eliminarInventario(idProducto, idSucursal);

        // Retorna HTTP 200 OK con mensaje de confirmacion
        return ResponseEntity.ok("Inventario eliminado correctamente");
    }

    // Define endpoint GET /api/v1/inventory/producto/{idProducto}
    @GetMapping("/producto/{idProducto}")
    // Documentacion Swagger
    @Operation(summary = "Buscar inventario por producto", description = "Obtiene los registros de inventario asociados a un producto")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operacion exitosa",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            array = @ArraySchema(schema = @Schema(implementation = InventoryResponse.class)),
                            examples = @ExampleObject(value = INVENTORY_LIST_RESPONSE_EXAMPLE))),
            @ApiResponse(responseCode = "401", description = "Token JWT no enviado o invalido", content = @Content),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = DtoApiError.class),
                            examples = @ExampleObject(value = "{\"timestamp\":\"2026-06-08\",\"status\":404,\"error\":\"Not Found\",\"message\":\"Producto no encontrado con ID: 1\",\"path\":\"/api/v1/inventory/producto/1\",\"claseException\":\"ResourceNotFoundException\"}"))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    public ResponseEntity<?> buscarPorProducto(

            // Recibe el ID del producto desde la URL
            @Parameter(description = "ID del producto", example = "1")
            @PathVariable Long idProducto
    ) {

        // Llama al servicio para buscar inventario por producto
        List<InventoryResponse> inventario = inventoryService.buscarPorProducto(idProducto);

        // Si no existen registros, devuelve mensaje informativo
        if (inventario.isEmpty()) {
            return ResponseEntity.ok("No hay inventario registrado para este producto");
        }

        // Retorna HTTP 200 OK con el inventario encontrado
        return ResponseEntity.ok(inventario);
    }

    // Define endpoint GET /api/v1/inventory/sucursal/{idSucursal}
    @GetMapping("/sucursal/{idSucursal}")
    // Documentacion Swagger
    @Operation(summary = "Buscar inventario por sucursal", description = "Obtiene los registros de inventario asociados a una sucursal")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operacion exitosa",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            array = @ArraySchema(schema = @Schema(implementation = InventoryResponse.class)),
                            examples = @ExampleObject(value = INVENTORY_LIST_RESPONSE_EXAMPLE))),
            @ApiResponse(responseCode = "401", description = "Token JWT no enviado o invalido", content = @Content),
            @ApiResponse(responseCode = "404", description = "Sucursal no encontrada",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = DtoApiError.class),
                            examples = @ExampleObject(value = "{\"timestamp\":\"2026-06-08\",\"status\":404,\"error\":\"Not Found\",\"message\":\"Sucursal no encontrada con ID: 1\",\"path\":\"/api/v1/inventory/sucursal/1\",\"claseException\":\"ResourceNotFoundException\"}"))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    public ResponseEntity<?> buscarPorSucursal(

            // Recibe el ID de la sucursal desde la URL
            @Parameter(description = "ID de la sucursal", example = "1")
            @PathVariable Long idSucursal
    ) {

        // Llama al servicio para buscar inventario por sucursal
        List<InventoryResponse> inventario = inventoryService.buscarPorSucursal(idSucursal);

        // Si no existen registros, devuelve mensaje informativo
        if (inventario.isEmpty()) {
            return ResponseEntity.ok("No hay inventario registrado para esta sucursal");
        }

        // Retorna HTTP 200 OK con el inventario encontrado
        return ResponseEntity.ok(inventario);
    }
}
