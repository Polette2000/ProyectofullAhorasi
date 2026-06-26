package cl.duoc.Inventory.exception;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import cl.duoc.Inventory.dto.DtoApiError;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

// Permite manejar excepciones de forma global y evita repetir try-catch en controllers
@RestControllerAdvice
// Habilita logger SLF4J automaticamente
@Slf4j
public class GlobalExceptionHandler {

    // Se activa cuando @Valid detecta datos incorrectos en el request
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationErrors(MethodArgumentNotValidException ex) {

        // Mapa donde se guardan los errores por nombre de campo
        Map<String, String> errores = new HashMap<>();

        // Recorre los errores de validacion y los agrega al mapa de respuesta
        ex.getBindingResult()
                .getFieldErrors()
                .forEach(error -> errores.put(error.getField(), error.getDefaultMessage()));

        log.error("Error de validacion en inventario: {}", errores, ex);

        // Devuelve error 400 Bad Request con el detalle de validaciones
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errores);
    }

    // Maneja errores cuando no se encuentra un recurso solicitado
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<DtoApiError> handleNotFound(
            ResourceNotFoundException ex,
            HttpServletRequest request
    ) {

        // Construye una respuesta estandarizada para el error 404
        DtoApiError error = DtoApiError.builder()
                .timestamp(LocalDate.now())
                .status(HttpStatus.NOT_FOUND.value())
                .error(HttpStatus.NOT_FOUND.getReasonPhrase())
                .message(ex.getMessage())
                .path(request.getRequestURI())
                .claseException("ResourceNotFoundException")
                .build();

        log.error("Recurso no encontrado en inventario path={}", request.getRequestURI(), ex);

        // Devuelve 404 Not Found con informacion del error
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    // Maneja errores generales de negocio, por ejemplo inventario duplicado
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleRuntime(RuntimeException ex) {
        log.error("Error de negocio en inventario", ex);
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    }
}