package cl.duoc.Perfulandiashipping.Controller;

import cl.duoc.Perfulandiashipping.dto.Request.EnvioRequest;
import cl.duoc.Perfulandiashipping.dto.Request.EnvioEstadoRequest;
import cl.duoc.Perfulandiashipping.dto.Response.EnvioResponse;
import cl.duoc.Perfulandiashipping.Service.EnvioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/envios")
@RequiredArgsConstructor
public class EnvioController {

    private final EnvioService envioService;

    // Obtener un envío por ID
    @GetMapping("/{idEnvio}")
    public ResponseEntity<EnvioResponse> obtenerEnvio(@PathVariable Long idEnvio) {
        return ResponseEntity.ok(envioService.obtenerEnvioPorId(idEnvio));
    }

    // Listar todos los envíos
    @GetMapping
    public ResponseEntity<List<EnvioResponse>> listarEnvios() {
        return ResponseEntity.ok(envioService.listarEnvios());
    }

    // Crear un envío
    @PostMapping
    public ResponseEntity<EnvioResponse> crearEnvio(
            @Valid @RequestBody EnvioRequest request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(envioService.crearEnvio(request));
    }

    // Actualizar estado de envío
    @PatchMapping("/{idEnvio}/estado")
    public ResponseEntity<EnvioResponse> actualizarEstado(
            @PathVariable Long idEnvio,
            @Valid @RequestBody EnvioEstadoRequest request) {

        return ResponseEntity.ok(
                envioService.actualizarEstado(idEnvio, request.getEstado())
        );
    }

    // Eliminar envío
    @DeleteMapping("/{idEnvio}")
    public ResponseEntity<Void> eliminarEnvio(@PathVariable Long idEnvio) {
        envioService.eliminarEnvio(idEnvio);
        return ResponseEntity.noContent().build();
    }

    // Buscar por número de seguimiento
    @GetMapping("/seguimiento/{numeroSeguimiento}")
    public ResponseEntity<EnvioResponse> buscarPorNumeroSeguimiento(
            @PathVariable String numeroSeguimiento) {

        return ResponseEntity.ok(
                envioService.obtenerPorNumeroSeguimiento(numeroSeguimiento)
        );
    }
}
