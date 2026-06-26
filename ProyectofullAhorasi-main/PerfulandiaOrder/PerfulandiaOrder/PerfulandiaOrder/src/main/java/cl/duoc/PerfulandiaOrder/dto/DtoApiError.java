package cl.duoc.PerfulandiaOrder.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

// Esta clase se usa como respuesta cuando ocurre un error en la API
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DtoApiError {

    private LocalDate timestamp;  // Fecha en que ocurrió el error
    private int status;           // Código HTTP (404, 500, etc.)
    private String error;         // Tipo de error (Not Found, Internal Server Error)
    private String message;       // Mensaje descriptivo del error
    private String path;          // Ruta donde ocurrió el error
    private String claseException; // Clase de la excepción lanzada
}