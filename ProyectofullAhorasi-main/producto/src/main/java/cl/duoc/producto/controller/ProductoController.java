package cl.duoc.producto.controller;

import java.util.List;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cl.duoc.producto.dto.DtoApiError;
import cl.duoc.producto.dto.request.ProductoCreateRequest;
import cl.duoc.producto.dto.request.ProductoUpdateRequest;
import cl.duoc.producto.dto.response.ProductoResponse;
import cl.duoc.producto.service.ProductoService;
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

// Indica que esta clase expone endpoints REST
@RestController
// Ruta base del microservicio producto
@RequestMapping("/api/v1/productos")
// Genera constructor automaticamente
@RequiredArgsConstructor
// Agrupa los endpoints en Swagger
@Tag(name = "Producto Controller", description = "Endpoints para gestionar productos de Perfulandia")
// Indica que los endpoints requieren token JWT
@SecurityRequirement(name = "bearerAuth")
public class ProductoController {

    // Inyecta automaticamente ProductoService
    private final ProductoService productoService;

    // Define endpoint GET /api/v1/productos
    @GetMapping
    // Documentacion Swagger
    @Operation(summary = "Listar productos", description = "Obtiene todos los productos registrados")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operacion exitosa",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            array = @ArraySchema(schema = @Schema(implementation = ProductoResponse.class)),
                            examples = @ExampleObject(value = """
                                    [
                                      {
                                        "idProducto": 1,
                                        "nombre": "Perfume Floral Primavera",
                                        "descripcion": "Perfume floral de 100 ml",
                                        "precio": 24990
                                      }
                                    ]
                                    """))),
            @ApiResponse(responseCode = "401", description = "Token JWT no enviado o invalido", content = @Content),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    public ResponseEntity<List<ProductoResponse>> listarProductos() {

        // Llama al servicio para obtener todos los productos
        List<ProductoResponse> productos = productoService.listarProductos();

        // Retorna HTTP 200 OK con la lista de productos
        return ResponseEntity.ok(productos);
    }

    // Define endpoint GET /api/v1/productos/{idProducto}
    @GetMapping("/{idProducto}")
    // Documentacion Swagger
    @Operation(summary = "Buscar producto por ID", description = "Obtiene un producto usando su identificador")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operacion exitosa",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ProductoResponse.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "idProducto": 1,
                                      "nombre": "Perfume Floral Primavera",
                                      "descripcion": "Perfume floral de 100 ml",
                                      "precio": 24990
                                    }
                                    """))),
            @ApiResponse(responseCode = "401", description = "Token JWT no enviado o invalido", content = @Content),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = DtoApiError.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "timestamp": "2026-06-14",
                                      "status": 404,
                                      "error": "Not Found",
                                      "message": "Producto no encontrado con ID: 1",
                                      "path": "/api/v1/productos/1",
                                      "claseException": "ResourceNotFoundException"
                                    }
                                    """))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    public ResponseEntity<ProductoResponse> buscarPorId(
            // Recibe el ID del producto desde la URL
            @Parameter(description = "ID del producto", example = "1", required = true)
            @PathVariable Long idProducto
    ) {

        // Llama al servicio para buscar el producto por ID
        ProductoResponse producto = productoService.buscarPorId(idProducto);

        // Retorna HTTP 200 OK con el producto encontrado
        return ResponseEntity.ok(producto);
    }

    // Define endpoint POST /api/v1/productos
    @PostMapping
    // Documentacion Swagger
    @Operation(summary = "Crear producto", description = "Registra un nuevo producto en la base de datos")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Producto creado correctamente",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ProductoResponse.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "idProducto": 1,
                                      "nombre": "Perfume Floral Primavera",
                                      "descripcion": "Perfume floral de 100 ml",
                                      "precio": 24990
                                    }
                                    """))),
            @ApiResponse(responseCode = "400", description = "Datos de entrada invalidos",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = DtoApiError.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "timestamp": "2026-06-14",
                                      "status": 400,
                                      "error": "Bad Request",
                                      "message": "El precio debe ser mayor a cero",
                                      "path": "/api/v1/productos",
                                      "claseException": "MethodArgumentNotValidException"
                                    }
                                    """))),
            @ApiResponse(responseCode = "401", description = "Token JWT no enviado o invalido", content = @Content),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    public ResponseEntity<ProductoResponse> crearProducto(
            // Recibe el JSON del body y valida sus campos
            @Valid @RequestBody ProductoCreateRequest request
    ) {

        // Llama al servicio para crear el producto
        ProductoResponse productoCreado = productoService.crearProducto(request);

        // Retorna HTTP 201 Created con el producto creado
        return ResponseEntity.status(HttpStatus.CREATED).body(productoCreado);
    }

    // Define endpoint PUT /api/v1/productos/{idProducto}
    @PutMapping("/{idProducto}")
    // Documentacion Swagger
    @Operation(summary = "Actualizar producto", description = "Actualiza los datos de un producto existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Producto actualizado correctamente",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ProductoResponse.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "idProducto": 1,
                                      "nombre": "Perfume Citrico Verano",
                                      "descripcion": "Perfume citrico de 100 ml",
                                      "precio": 29990
                                    }
                                    """))),
            @ApiResponse(responseCode = "400", description = "Datos de entrada invalidos",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = DtoApiError.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "timestamp": "2026-06-14",
                                      "status": 400,
                                      "error": "Bad Request",
                                      "message": "El nombre del producto es obligatorio",
                                      "path": "/api/v1/productos/1",
                                      "claseException": "MethodArgumentNotValidException"
                                    }
                                    """))),
            @ApiResponse(responseCode = "401", description = "Token JWT no enviado o invalido", content = @Content),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = DtoApiError.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "timestamp": "2026-06-14",
                                      "status": 404,
                                      "error": "Not Found",
                                      "message": "Producto no encontrado con ID: 1",
                                      "path": "/api/v1/productos/1",
                                      "claseException": "ResourceNotFoundException"
                                    }
                                    """))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    public ResponseEntity<ProductoResponse> actualizarProducto(
            // Recibe el ID del producto desde la URL
            @Parameter(description = "ID del producto", example = "1", required = true)
            @PathVariable Long idProducto,

            // Recibe el JSON del body y valida sus campos
            @Valid @RequestBody ProductoUpdateRequest request
    ) {

        // Llama al servicio para actualizar el producto
        ProductoResponse productoActualizado = productoService.actualizarProducto(idProducto, request);

        // Retorna HTTP 200 OK con el producto actualizado
        return ResponseEntity.ok(productoActualizado);
    }

    // Define endpoint DELETE /api/v1/productos/{idProducto}
    @DeleteMapping("/{idProducto}")
    // Documentacion Swagger
    @Operation(summary = "Eliminar producto", description = "Elimina un producto registrado por su identificador")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Producto eliminado correctamente",
                    content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE,
                            schema = @Schema(implementation = String.class),
                            examples = @ExampleObject(value = "Producto eliminado correctamente"))),
            @ApiResponse(responseCode = "401", description = "Token JWT no enviado o invalido", content = @Content),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = DtoApiError.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "timestamp": "2026-06-14",
                                      "status": 404,
                                      "error": "Not Found",
                                      "message": "Producto no encontrado con ID: 1",
                                      "path": "/api/v1/productos/1",
                                      "claseException": "ResourceNotFoundException"
                                    }
                                    """))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    public ResponseEntity<String> eliminarProducto(
            // Recibe el ID del producto desde la URL
            @Parameter(description = "ID del producto", example = "1", required = true)
            @PathVariable Long idProducto
    ) {

        // Llama al servicio para eliminar el producto
        productoService.eliminarProducto(idProducto);

        // Retorna HTTP 200 OK con mensaje de confirmacion
        return ResponseEntity.ok("Producto eliminado correctamente");
    }

    // Define endpoint GET /api/v1/productos/buscar?nombre={nombre}
    @GetMapping("/buscar")
    // Documentacion Swagger
    @Operation(summary = "Buscar productos por nombre", description = "Obtiene productos que coinciden con el nombre enviado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operacion exitosa",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            array = @ArraySchema(schema = @Schema(implementation = ProductoResponse.class)),
                            examples = @ExampleObject(value = """
                                    [
                                      {
                                        "idProducto": 1,
                                        "nombre": "Perfume Floral Primavera",
                                        "descripcion": "Perfume floral de 100 ml",
                                        "precio": 24990
                                      }
                                    ]
                                    """))),
            @ApiResponse(responseCode = "401", description = "Token JWT no enviado o invalido", content = @Content),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    public ResponseEntity<List<ProductoResponse>> buscarPorNombre(
            // Recibe el nombre o parte del nombre desde query param
            @Parameter(description = "Nombre o parte del nombre del producto", example = "Perfume", required = true)
            @RequestParam String nombre
    ) {

        // Llama al servicio para buscar productos por nombre
        List<ProductoResponse> productos = productoService.buscarPorNombre(nombre);

        // Retorna HTTP 200 OK con la lista de productos encontrados
        return ResponseEntity.ok(productos);
    }
}