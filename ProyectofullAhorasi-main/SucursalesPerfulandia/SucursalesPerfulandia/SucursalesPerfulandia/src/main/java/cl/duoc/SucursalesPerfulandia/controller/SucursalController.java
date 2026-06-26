package cl.duoc.SucursalesPerfulandia.controller;

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

import cl.duoc.SucursalesPerfulandia.dto.request.SucursalCreateRequest;
import cl.duoc.SucursalesPerfulandia.dto.request.SucursalUpdateRequest;
import cl.duoc.SucursalesPerfulandia.dto.response.SucursalResponse;
import cl.duoc.SucursalesPerfulandia.service.SucursalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/sucursales")
@RequiredArgsConstructor
@Tag(name = "Sucursales", description = "Gestion de sucursales")
public class SucursalController {

    private final SucursalService sucursalService;

    private static final String SUCURSAL_REQUEST_EXAMPLE = """
            {
              "nombreSucursal": "Sucursal Centro",
              "direccion": "Av. Libertador 123, Santiago",
              "telefono": "+56912345678",
              "horarioAtencion": "09:00-18:00",
              "idComuna": 1
            }
            """;

    private static final String SUCURSAL_RESPONSE_EXAMPLE = """
            {
              "idSucursal": 2,
              "nombreSucursal": "Sucursal Centro",
              "direccion": "Av. Libertador 123, Santiago",
              "telefono": "+56912345678",
              "horarioAtencion": "09:00-18:00",
              "idComuna": 1,
              "nombreComuna": "Santiago",
              "region": "Metropolitana"
            }
            """;

    private static final String SUCURSALES_LIST_RESPONSE_EXAMPLE = """
            [
              {
                "idSucursal": 2,
                "nombreSucursal": "Sucursal Centro",
                "direccion": "Av. Libertador 123, Santiago",
                "telefono": "+56912345678",
                "horarioAtencion": "09:00-18:00",
                "idComuna": 1,
                "nombreComuna": "Santiago",
                "region": "Metropolitana"
              }
            ]
            """;

    @GetMapping
    @Operation(summary = "Listar sucursales", description = "Obtiene todas las sucursales registradas.")
    @ApiResponse(responseCode = "200", description = "Sucursales encontradas",
            content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(value = SUCURSALES_LIST_RESPONSE_EXAMPLE)))
    public ResponseEntity<?> listarSucursales() {
        List<SucursalResponse> sucursales = sucursalService.listarSucursales();
        if (sucursales.isEmpty()) {
            return ResponseEntity.ok("No hay sucursales registradas");
        }
        return ResponseEntity.ok(sucursales);
    }

    @GetMapping("/comuna/{idComuna}")
    @Operation(summary = "Listar sucursales por comuna", description = "Obtiene sucursales filtradas por comuna.")
    @ApiResponse(responseCode = "200", description = "Sucursales encontradas para la comuna",
            content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(value = SUCURSALES_LIST_RESPONSE_EXAMPLE)))
    public ResponseEntity<?> listarSucursalesPorComuna(@PathVariable Integer idComuna) {
        List<SucursalResponse> sucursales = sucursalService.listarSucursalesPorComuna(idComuna);
        if (sucursales.isEmpty()) {
            return ResponseEntity.ok("No hay sucursales registradas para esta comuna");
        }
        return ResponseEntity.ok(sucursales);
    }

    @GetMapping("/{idSucursal}")
    @Operation(summary = "Buscar sucursal por ID", description = "Obtiene una sucursal especifica por su ID.")
    @ApiResponse(responseCode = "200", description = "Sucursal encontrada",
            content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(value = SUCURSAL_RESPONSE_EXAMPLE)))
    public SucursalResponse buscarSucursalPorId(@PathVariable Integer idSucursal) {
        return sucursalService.buscarSucursalPorId(idSucursal);
    }

    @PostMapping
    @Operation(summary = "Crear sucursal", description = "Registra una nueva sucursal.")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Datos de la sucursal",
            content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(value = SUCURSAL_REQUEST_EXAMPLE)))
    @ApiResponse(responseCode = "200", description = "Sucursal creada",
            content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(value = SUCURSAL_RESPONSE_EXAMPLE)))
    public SucursalResponse crearSucursal(@Valid @RequestBody SucursalCreateRequest request) {
        return sucursalService.crearSucursal(request);
    }

    @PutMapping("/{idSucursal}")
    @Operation(summary = "Actualizar sucursal", description = "Actualiza una sucursal existente.")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Datos actualizados de la sucursal",
            content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(value = SUCURSAL_REQUEST_EXAMPLE)))
    @ApiResponse(responseCode = "200", description = "Sucursal actualizada",
            content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(value = SUCURSAL_RESPONSE_EXAMPLE)))
    public SucursalResponse actualizarSucursal(
            @PathVariable Integer idSucursal,
            @Valid @RequestBody SucursalUpdateRequest request) {
        return sucursalService.actualizarSucursal(idSucursal, request);
    }

    @DeleteMapping("/{idSucursal}")
    @Operation(summary = "Eliminar sucursal", description = "Elimina una sucursal por ID.")
    @ApiResponse(responseCode = "200", description = "Sucursal eliminada",
            content = @Content(mediaType = "text/plain",
                    examples = @ExampleObject(value = "Sucursal eliminada correctamente")))
    public String eliminarSucursal(@PathVariable Integer idSucursal) {
        sucursalService.eliminarSucursal(idSucursal);
        return "Sucursal eliminada correctamente";
    }
}
