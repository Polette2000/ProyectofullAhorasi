package cl.duoc.exception;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import cl.duoc.dto.DtoApiError;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

// Permite manejar excepciones de manera global. Evita try-catch en cada metodo.
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    // Se activa cuando usamos @Valid y algun dato viene mal.
    // Devuelve 400 Bad Request.
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationErrors(MethodArgumentNotValidException ex) {
        log.warn("Error de validacion en request: {}", ex.getMessage());

        Map<String, String> errores = new HashMap<>();

        ex.getBindingResult()
                .getFieldErrors()
                .forEach(error -> errores.put(error.getField(), error.getDefaultMessage()));

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errores);
    }

    // Se activa cuando el JSON viene mal escrito o con formato incorrecto.
    // Devuelve 400 Bad Request.
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<DtoApiError> handleJsonError(
            HttpMessageNotReadableException ex,
            HttpServletRequest request
    ) {
        log.warn("JSON invalido en {}: {}", request.getRequestURI(), ex.getMessage());

        DtoApiError error = DtoApiError.builder()
                .timestamp(LocalDate.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .message("El JSON enviado tiene un formato incorrecto. Revisa los campos y la fecha.")
                .path(request.getRequestURI())
                .claseException("HttpMessageNotReadableException")
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<DtoApiError> handleNotFound(
            ResourceNotFoundException ex,
            HttpServletRequest request
    ) {
        log.warn("Recurso no encontrado en {}: {}", request.getRequestURI(), ex.getMessage());

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
    // Devuelve 400 Bad Request.
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<DtoApiError> handleRuntime(
            RuntimeException ex,
            HttpServletRequest request
    ) {
        log.warn("Error de negocio en {}: {}", request.getRequestURI(), ex.getMessage());

        DtoApiError error = DtoApiError.builder()
                .timestamp(LocalDate.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .message(ex.getMessage())
                .path(request.getRequestURI())
                .claseException("RuntimeException")
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }
}
