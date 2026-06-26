package cl.duoc.producto.dto;

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

    @Schema(description = "Fecha en que se genero el error", example = "2026-06-14")
    private LocalDate timestamp;

    @Schema(description = "Codigo HTTP del error", example = "404")
    private int status;

    @Schema(description = "Nombre del error HTTP", example = "Not Found")
    private String error;

    @Schema(description = "Detalle del error", example = "Producto no encontrado con ID: 1")
    private String message;

    @Schema(description = "Ruta donde ocurrio el error", example = "/api/v1/productos/1")
    private String path;

    @Schema(description = "Clase de excepcion capturada", example = "ResourceNotFoundException")
    private String claseException;
}