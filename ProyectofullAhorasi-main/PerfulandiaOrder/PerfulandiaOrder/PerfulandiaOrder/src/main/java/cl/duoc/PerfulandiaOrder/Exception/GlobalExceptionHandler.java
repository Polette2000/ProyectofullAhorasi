package cl.duoc.PerfulandiaOrder.Exception;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import cl.duoc.PerfulandiaOrder.dto.DtoApiError;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

// @RestControllerAdvice permite manejar excepciones de manera global. Evita try-catch
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // Se activa cuando en el Controller usamos @Valid y algún dato viene mal
    // Devuelve un error 400 Bad Request
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationErrors(
            MethodArgumentNotValidException ex) {

        Map<String, String> errores = new HashMap<>();

        ex.getBindingResult()
                .getFieldErrors()
                .forEach(error -> {
                    errores.put(error.getField(), error.getDefaultMessage());
                });

        log.error("se registra error", ex);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errores);
    }

    // Maneja errores cuando no se encuentra un recurso. Ej: id 5 no encontrado
    // Devuelve 404 Not Found
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<DtoApiError> handleNotFound(
            ResourceNotFoundException ex,
            HttpServletRequest request) {

        DtoApiError error = DtoApiError.builder()
                .timestamp(LocalDate.now())
                .status(HttpStatus.NOT_FOUND.value())
                .error(HttpStatus.NOT_FOUND.getReasonPhrase())
                .message(ex.getMessage())
                .path(request.getRequestURI())
                .claseException("ResourceNotFoundException")
                .build();

        log.error("se registra error", ex);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    // Maneja errores de tipo RuntimeException. Ej: cuando se crea un pedido duplicado
    // Devuelve 409 Conflict
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleRuntime(RuntimeException ex) {
        log.error("se registra error", ex);
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    }
}
