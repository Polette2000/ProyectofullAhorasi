package cl.duoc.Inventory.dto;

import java.time.LocalDate;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

// Clase usada como respuesta cuando ocurre un error en la API
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "Respuesta estandar para errores de la API")
public class DtoApiError {

    // Fecha en que se genero el error
    @Schema(description = "Fecha en que se genero el error", example = "2026-06-08")
    private LocalDate timestamp;

    // Codigo HTTP del error
    @Schema(description = "Codigo HTTP del error", example = "404")
    private int status;

    // Nombre del error HTTP
    @Schema(description = "Nombre del error HTTP", example = "Not Found")
    private String error;

    // Mensaje explicativo para el cliente
    @Schema(description = "Mensaje explicativo del error", example = "Inventario no encontrado para producto: 1 y sucursal: 1")
    private String message;

    // Ruta del endpoint donde ocurrio el error
    @Schema(description = "Ruta del endpoint donde ocurrio el error", example = "/api/v1/inventory/producto/1/sucursal/1")
    private String path;

    // Clase de excepcion que produjo el error
    @Schema(description = "Clase de excepcion que produjo el error", example = "ResourceNotFoundException")
    private String claseException;
}