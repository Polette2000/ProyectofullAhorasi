package cl.duoc.proveedor.controller;

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

import cl.duoc.proveedor.dto.DtoApiError;
import cl.duoc.proveedor.dto.request.ProveedorCreateRequest;
import cl.duoc.proveedor.dto.request.ProveedorUpdateRequest;
import cl.duoc.proveedor.dto.response.ProveedorResponse;
import cl.duoc.proveedor.service.ProveedorService;
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
// Ruta base del microservicio proveedor
@RequestMapping("/api/v1/proveedores")
// Genera constructor automaticamente
@RequiredArgsConstructor
// Agrupa los endpoints en Swagger
@Tag(name = "Proveedor Controller", description = "Endpoints para gestionar proveedores de Perfulandia")
// Indica que los endpoints requieren token JWT
@SecurityRequirement(name = "bearerAuth")
public class ProveedorController {

    // Inyecta automaticamente ProveedorService
    private final ProveedorService proveedorService;

    // Define endpoint GET /api/v1/proveedores
    @GetMapping
    // Documentacion Swagger
    @Operation(summary = "Listar proveedores", description = "Obtiene todos los proveedores registrados")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operacion exitosa",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            array = @ArraySchema(schema = @Schema(implementation = ProveedorResponse.class)),
                            examples = @ExampleObject(value = """
                                    [
                                      {
                                        "idProveedor": 1,
                                        "nombre": "Aromas Chile SpA",
                                        "correo": "contacto@aromaschile.cl",
                                        "telefono": "+56912345678",
                                        "direccion": "Av. Principal 123, Santiago"
                                      }
                                    ]
                                    """))),
            @ApiResponse(responseCode = "401", description = "Token JWT no enviado o invalido", content = @Content),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    public ResponseEntity<List<ProveedorResponse>> listarProveedores() {

        // Llama al servicio para obtener todos los proveedores
        List<ProveedorResponse> proveedores = proveedorService.listarProveedores();

        // Retorna HTTP 200 OK con la lista de proveedores
        return ResponseEntity.ok(proveedores);
    }

    // Define endpoint GET /api/v1/proveedores/{idProveedor}
    @GetMapping("/{idProveedor}")
    // Documentacion Swagger
    @Operation(summary = "Buscar proveedor por ID", description = "Obtiene un proveedor usando su identificador")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operacion exitosa",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ProveedorResponse.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "idProveedor": 1,
                                      "nombre": "Aromas Chile SpA",
                                      "correo": "contacto@aromaschile.cl",
                                      "telefono": "+56912345678",
                                      "direccion": "Av. Principal 123, Santiago"
                                    }
                                    """))),
            @ApiResponse(responseCode = "401", description = "Token JWT no enviado o invalido", content = @Content),
            @ApiResponse(responseCode = "404", description = "Proveedor no encontrado",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = DtoApiError.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "timestamp": "2026-06-14",
                                      "status": 404,
                                      "error": "Not Found",
                                      "message": "Proveedor no encontrado con ID: 1",
                                      "path": "/api/v1/proveedores/1",
                                      "claseException": "ResourceNotFoundException"
                                    }
                                    """))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    public ResponseEntity<ProveedorResponse> buscarPorId(
            // Recibe el ID del proveedor desde la URL
            @Parameter(description = "ID del proveedor", example = "1", required = true)
            @PathVariable Long idProveedor
    ) {

        // Llama al servicio para buscar el proveedor por ID
        ProveedorResponse proveedor = proveedorService.buscarPorId(idProveedor);

        // Retorna HTTP 200 OK con el proveedor encontrado
        return ResponseEntity.ok(proveedor);
    }

    // Define endpoint POST /api/v1/proveedores
    @PostMapping
    // Documentacion Swagger
    @Operation(summary = "Crear proveedor", description = "Registra un nuevo proveedor en la base de datos")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Proveedor creado correctamente",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ProveedorResponse.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "idProveedor": 1,
                                      "nombre": "Aromas Chile SpA",
                                      "correo": "contacto@aromaschile.cl",
                                      "telefono": "+56912345678",
                                      "direccion": "Av. Principal 123, Santiago"
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
                                      "message": "El correo debe tener un formato valido",
                                      "path": "/api/v1/proveedores",
                                      "claseException": "MethodArgumentNotValidException"
                                    }
                                    """))),
            @ApiResponse(responseCode = "401", description = "Token JWT no enviado o invalido", content = @Content),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    public ResponseEntity<ProveedorResponse> crearProveedor(
            // Recibe el JSON del body y valida sus campos
            @Valid @RequestBody ProveedorCreateRequest request
    ) {

        // Llama al servicio para crear el proveedor
        ProveedorResponse proveedorCreado = proveedorService.crearProveedor(request);

        // Retorna HTTP 201 Created con el proveedor creado
        return ResponseEntity.status(HttpStatus.CREATED).body(proveedorCreado);
    }

    // Define endpoint PUT /api/v1/proveedores/{idProveedor}
    @PutMapping("/{idProveedor}")
    // Documentacion Swagger
    @Operation(summary = "Actualizar proveedor", description = "Actualiza los datos de un proveedor existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Proveedor actualizado correctamente",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ProveedorResponse.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "idProveedor": 1,
                                      "nombre": "Aromas Chile SpA",
                                      "correo": "ventas@aromaschile.cl",
                                      "telefono": "+56987654321",
                                      "direccion": "Av. Principal 456, Santiago"
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
                                      "message": "El nombre del proveedor es obligatorio",
                                      "path": "/api/v1/proveedores/1",
                                      "claseException": "MethodArgumentNotValidException"
                                    }
                                    """))),
            @ApiResponse(responseCode = "401", description = "Token JWT no enviado o invalido", content = @Content),
            @ApiResponse(responseCode = "404", description = "Proveedor no encontrado",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = DtoApiError.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "timestamp": "2026-06-14",
                                      "status": 404,
                                      "error": "Not Found",
                                      "message": "Proveedor no encontrado con ID: 1",
                                      "path": "/api/v1/proveedores/1",
                                      "claseException": "ResourceNotFoundException"
                                    }
                                    """))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    public ResponseEntity<ProveedorResponse> actualizarProveedor(
            // Recibe el ID del proveedor desde la URL
            @Parameter(description = "ID del proveedor", example = "1", required = true)
            @PathVariable Long idProveedor,

            // Recibe el JSON del body y valida sus campos
            @Valid @RequestBody ProveedorUpdateRequest request
    ) {

        // Llama al servicio para actualizar el proveedor
        ProveedorResponse proveedorActualizado = proveedorService.actualizarProveedor(idProveedor, request);

        // Retorna HTTP 200 OK con el proveedor actualizado
        return ResponseEntity.ok(proveedorActualizado);
    }

    // Define endpoint DELETE /api/v1/proveedores/{idProveedor}
    @DeleteMapping("/{idProveedor}")
    // Documentacion Swagger
    @Operation(summary = "Eliminar proveedor", description = "Elimina un proveedor registrado por su identificador")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Proveedor eliminado correctamente",
                    content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE,
                            schema = @Schema(implementation = String.class),
                            examples = @ExampleObject(value = "Proveedor eliminado correctamente"))),
            @ApiResponse(responseCode = "401", description = "Token JWT no enviado o invalido", content = @Content),
            @ApiResponse(responseCode = "404", description = "Proveedor no encontrado",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = DtoApiError.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "timestamp": "2026-06-14",
                                      "status": 404,
                                      "error": "Not Found",
                                      "message": "Proveedor no encontrado con ID: 1",
                                      "path": "/api/v1/proveedores/1",
                                      "claseException": "ResourceNotFoundException"
                                    }
                                    """))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    public ResponseEntity<String> eliminarProveedor(
            // Recibe el ID del proveedor desde la URL
            @Parameter(description = "ID del proveedor", example = "1", required = true)
            @PathVariable Long idProveedor
    ) {

        // Llama al servicio para eliminar el proveedor
        proveedorService.eliminarProveedor(idProveedor);

        // Retorna HTTP 200 OK con mensaje de confirmacion
        return ResponseEntity.ok("Proveedor eliminado correctamente");
    }

    // Define endpoint GET /api/v1/proveedores/buscar?nombre={nombre}
    @GetMapping("/buscar")
    // Documentacion Swagger
    @Operation(summary = "Buscar proveedores por nombre", description = "Obtiene proveedores que coinciden con el nombre enviado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operacion exitosa",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            array = @ArraySchema(schema = @Schema(implementation = ProveedorResponse.class)),
                            examples = @ExampleObject(value = """
                                    [
                                      {
                                        "idProveedor": 1,
                                        "nombre": "Aromas Chile SpA",
                                        "correo": "contacto@aromaschile.cl",
                                        "telefono": "+56912345678",
                                        "direccion": "Av. Principal 123, Santiago"
                                      }
                                    ]
                                    """))),
            @ApiResponse(responseCode = "401", description = "Token JWT no enviado o invalido", content = @Content),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    public ResponseEntity<List<ProveedorResponse>> buscarPorNombre(
            // Recibe el nombre o parte del nombre desde query param
            @Parameter(description = "Nombre o parte del nombre del proveedor", example = "Aromas", required = true)
            @RequestParam String nombre
    ) {

        // Llama al servicio para buscar proveedores por nombre
        List<ProveedorResponse> proveedores = proveedorService.buscarPorNombre(nombre);

        // Retorna HTTP 200 OK con la lista de proveedores encontrados
        return ResponseEntity.ok(proveedores);
    }
}