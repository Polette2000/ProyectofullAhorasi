package cl.duoc.SucursalesPerfulandia.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cl.duoc.SucursalesPerfulandia.dto.response.ComunaResponse;
import cl.duoc.SucursalesPerfulandia.service.ComunaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/comunas")
@RequiredArgsConstructor
@Tag(name = "Comunas", description = "Consulta de comunas disponibles")
public class ComunaController {

    private final ComunaService comunaService;

    private static final String COMUNAS_LIST_RESPONSE_EXAMPLE = """
            [
              {
                "idComuna": 1,
                "nombreComuna": "Santiago",
                "region": "Metropolitana"
              },
              {
                "idComuna": 2,
                "nombreComuna": "Providencia",
                "region": "Metropolitana"
              }
            ]
            """;

    // Lista las comunas disponibles para saber que idComuna usar en sucursales.
    @GetMapping
    @Operation(summary = "Listar comunas", description = "Obtiene las comunas disponibles para asociarlas a sucursales.")
    @ApiResponse(responseCode = "200", description = "Comunas encontradas",
            content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(value = COMUNAS_LIST_RESPONSE_EXAMPLE)))
    public ResponseEntity<?> listarComunas() {
        List<ComunaResponse> comunas = comunaService.listarComunas();
        if (comunas.isEmpty()) {
            return ResponseEntity.ok("No hay comunas registradas");
        }
        return ResponseEntity.ok(comunas);
    }

}
