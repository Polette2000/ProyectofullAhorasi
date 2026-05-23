package cl.duoc.Perfulandiabilling.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

// Esta clase se usa como respuesta cuando ocurre un error en la API Billing.
// Permite entregar información detallada al cliente sobre el fallo.
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DtoApiError {

    // Momento en que ocurrió el error
    private LocalDate timestamp;

    // Código HTTP (ejemplo: 404, 400, 500)
    private int status;

    // Texto del error (ejemplo: "Not Found", "Bad Request")
    private String error;

    // Mensaje detallado del error
    private String message;

    // Endpoint que se llamó y produjo el error
    private String path;

    // Clase de la excepción capturada (ejemplo: "PaymentNotFoundException")
    private String claseException;
}
