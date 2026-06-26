package cl.duoc.TokenPerfulandia.exception;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import cl.duoc.TokenPerfulandia.dto.DtoApiError;
import jakarta.servlet.http.HttpServletRequest;

// Permite manejar excepciones de manera global. Evita try-catch en cada metodo.
@RestControllerAdvice
public class GlobalExceptionHandler {

    // Se activa cuando usamos @Valid y algun dato viene mal.
    // Devuelve 400 Bad Request.
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationErrors(MethodArgumentNotValidException ex) {

        Map<String, String> errores = new HashMap<>();

        ex.getBindingResult()
                .getFieldErrors()
                .forEach(error -> errores.put(error.getField(), error.getDefaultMessage()));

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errores);
    }

    // Maneja errores controlados con codigo HTTP, por ejemplo login incorrecto.
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<DtoApiError> handleResponseStatus(
            ResponseStatusException ex,
            HttpServletRequest request
    ) {

        HttpStatus status = HttpStatus.valueOf(ex.getStatusCode().value());

        DtoApiError error = DtoApiError.builder()
                .timestamp(LocalDate.now())
                .status(status.value())
                .error(status.getReasonPhrase())
                .message(ex.getReason())
                .path(request.getRequestURI())
                .claseException("ResponseStatusException")
                .build();

        return ResponseEntity.status(status).body(error);
    }

    // Maneja errores de tipo RuntimeException.
    // Devuelve 409 Conflict.
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleRuntime(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    }
}
