package cl.duoc.Perfulandiashipping.dto;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDate;

@Data
@Builder
public class DtoApiError {

    // Fecha del error
    private LocalDate timestamp;

    // Código HTTP
    private int status;

    // Descripción del error HTTP
    private String error;

    // Mensaje del error
    private String message;

    // Ruta donde ocurrió el error
    private String path;

    // Clase de la excepción
    private String claseException;
}