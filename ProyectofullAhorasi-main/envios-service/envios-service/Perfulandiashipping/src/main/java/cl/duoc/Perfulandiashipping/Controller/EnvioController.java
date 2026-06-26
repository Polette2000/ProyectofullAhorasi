package cl.duoc.Perfulandiashipping.Controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cl.duoc.Perfulandiashipping.Service.EnvioService;
import cl.duoc.Perfulandiashipping.dto.Request.EnvioEstadoRequest;
import cl.duoc.Perfulandiashipping.dto.Request.EnvioRequest;
import cl.duoc.Perfulandiashipping.dto.Response.EnvioResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/envios")
@RequiredArgsConstructor
@Tag(name = "Envios", description = "Gestion de envios y seguimiento")
public class EnvioController {

    private final EnvioService envioService;

    private static final String ENVIO_REQUEST_EXAMPLE = """
            {
              "idOrden": 1,
              "direccion": "Av. Siempre Viva 742, Santiago",
              "numeroSeguimiento": "ENV-2026-0001",
              "estado": "PENDIENTE",
              "fechaEstimadaInicio": "2026-06-25",
              "fechaEstimadaFin": "2026-06-30"
            }
            """;

    private static final String ENVIO_RESPONSE_EXAMPLE = """
            {
              "idEnvio": 1,
              "idOrden": 1,
              "idUsuario": 5,
              "estadoPedido": "PAGADO",
              "direccion": "Av. Siempre Viva 742, Santiago",
              "numeroSeguimiento": "ENV-2026-0001",
              "estado": "PENDIENTE",
              "fechaEstimadaInicio": "2026-06-25",
              "fechaEstimadaFin": "2026-06-30"
            }
            """;

    private static final String ENVIOS_LIST_RESPONSE_EXAMPLE = """
            [
              {
                "idEnvio": 1,
                "idOrden": 1,
                "idUsuario": 5,
                "estadoPedido": "PAGADO",
                "direccion": "Av. Siempre Viva 742, Santiago",
                "numeroSeguimiento": "ENV-2026-0001",
                "estado": "PENDIENTE",
                "fechaEstimadaInicio": "2026-06-25",
                "fechaEstimadaFin": "2026-06-30"
              }
            ]
            """;

    private static final String ESTADO_REQUEST_EXAMPLE = """
            {
              "estado": "EN_TRANSITO"
            }
            """;

    @GetMapping("/{idEnvio}")
    @Operation(summary = "Buscar envio por ID", description = "Obtiene un envio especifico por su ID.")
    @ApiResponse(responseCode = "200", description = "Envio encontrado",
            content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(value = ENVIO_RESPONSE_EXAMPLE)))
    public ResponseEntity<EnvioResponse> obtenerEnvio(@PathVariable Long idEnvio) {
        return ResponseEntity.ok(envioService.obtenerEnvioPorId(idEnvio));
    }

    @GetMapping
    @Operation(summary = "Listar envios", description = "Obtiene todos los envios registrados.")
    @ApiResponse(responseCode = "200", description = "Envios encontrados",
            content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(value = ENVIOS_LIST_RESPONSE_EXAMPLE)))
    public ResponseEntity<?> listarEnvios() {
        List<EnvioResponse> envios = envioService.listarEnvios();
        if (envios.isEmpty()) {
            return ResponseEntity.ok("No hay envios registrados");
        }
        return ResponseEntity.ok(envios);
    }

    @PostMapping
    @Operation(summary = "Crear envio", description = "Crea un envio para una orden pagada.")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Datos necesarios para crear el envio",
            content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(value = ENVIO_REQUEST_EXAMPLE)))
    @ApiResponse(responseCode = "201", description = "Envio creado",
            content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(value = ENVIO_RESPONSE_EXAMPLE)))
    public ResponseEntity<EnvioResponse> crearEnvio(
            @Valid @RequestBody EnvioRequest request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(envioService.crearEnvio(request));
    }

    @PatchMapping("/{idEnvio}/estado")
    @Operation(summary = "Actualizar estado de envio", description = "Actualiza el estado logistico de un envio.")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Nuevo estado del envio",
            content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(value = ESTADO_REQUEST_EXAMPLE)))
    @ApiResponse(responseCode = "200", description = "Envio actualizado",
            content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(value = ENVIO_RESPONSE_EXAMPLE)))
    public ResponseEntity<EnvioResponse> actualizarEstado(
            @PathVariable Long idEnvio,
            @Valid @RequestBody EnvioEstadoRequest request) {

        return ResponseEntity.ok(
                envioService.actualizarEstado(idEnvio, request.getEstado())
        );
    }

    @DeleteMapping("/{idEnvio}")
    @Operation(summary = "Eliminar envio", description = "Elimina un envio por ID.")
    @ApiResponse(responseCode = "200", description = "Envio eliminado",
            content = @Content(mediaType = "text/plain",
                    examples = @ExampleObject(value = "Envio eliminado correctamente")))
    public ResponseEntity<String> eliminarEnvio(@PathVariable Long idEnvio) {
        envioService.eliminarEnvio(idEnvio);
        return ResponseEntity.ok("Envio eliminado correctamente");
    }

    @GetMapping("/seguimiento/{numeroSeguimiento}")
    @Operation(summary = "Buscar envio por seguimiento", description = "Obtiene un envio usando su numero de seguimiento.")
    @ApiResponse(responseCode = "200", description = "Envio encontrado",
            content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(value = ENVIO_RESPONSE_EXAMPLE)))
    public ResponseEntity<EnvioResponse> buscarPorNumeroSeguimiento(
            @PathVariable String numeroSeguimiento) {

        return ResponseEntity.ok(
                envioService.obtenerPorNumeroSeguimiento(numeroSeguimiento)
        );
    }
}
