package cl.duoc.proveedor.dto;

import java.time.LocalDate;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Respuesta estandar para errores de la API")
public class DtoApiError {

    @Schema(description = "Fecha en que se genero el error", example = "2026-06-13")
    private LocalDate timestamp;

    @Schema(description = "Codigo HTTP del error", example = "404")
    private int status;

    @Schema(description = "Nombre del error HTTP", example = "Not Found")
    private String error;

    @Schema(description = "Detalle del error", example = "Proveedor no encontrado con ID: 1")
    private String message;

    @Schema(description = "Ruta donde ocurrio el error", example = "/api/v1/proveedores/1")
    private String path;

    @Schema(description = "Clase de excepcion capturada", example = "ResourceNotFoundException")
    private String claseException;
}