package cl.duoc.SucursalesPerfulandia.exception;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import cl.duoc.SucursalesPerfulandia.dto.DtoApiError;
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

    // Maneja errores cuando no se encuentra un recurso.
    // Devuelve 404 Not Found.
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<DtoApiError> handleNotFound(
            ResourceNotFoundException ex,
            HttpServletRequest request
    ) {

        DtoApiError error = DtoApiError.builder()
                .timestamp(LocalDate.now())
                .status(HttpStatus.NOT_FOUND.value())
                .error(HttpStatus.NOT_FOUND.getReasonPhrase())
                .message(ex.getMessage())
                .path(request.getRequestURI())
                .claseException("ResourceNotFoundException")
                .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    // Maneja errores de tipo RuntimeException.
    // Devuelve 409 Conflict.
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleRuntime(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    }
}
