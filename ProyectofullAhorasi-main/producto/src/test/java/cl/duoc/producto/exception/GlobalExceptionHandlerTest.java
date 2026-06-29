package cl.duoc.producto.exception;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import cl.duoc.producto.dto.DtoApiError;
import cl.duoc.producto.dto.request.ProductoCreateRequest;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void resourceNotFoundDebeResponder404() {
        MockHttpServletRequest request = request("/api/v1/productos/99");

        ResponseEntity<DtoApiError> response = handler.handleResourceNotFound(
                new ResourceNotFoundException("Producto no encontrado"), request);

        assertError(response, HttpStatus.NOT_FOUND, "Producto no encontrado", request.getRequestURI());
    }

    @Test
    void runtimeDebeResponder400() {
        MockHttpServletRequest request = request("/api/v1/productos");

        ResponseEntity<DtoApiError> response = handler.handleRuntime(
                new IllegalArgumentException("Regla invalida"), request);

        assertError(response, HttpStatus.BAD_REQUEST, "Regla invalida", request.getRequestURI());
    }

    @Test
    void validacionDebeUsarPrimerMensajeDeCampo() {
        MockHttpServletRequest request = request("/api/v1/productos");
        BeanPropertyBindingResult binding = new BeanPropertyBindingResult(
                new ProductoCreateRequest(), "request");
        binding.addError(new FieldError("request", "nombre", "El nombre es obligatorio"));

        ResponseEntity<DtoApiError> response = handler.handleValidation(
                new MethodArgumentNotValidException(methodParameter(), binding), request);

        assertError(response, HttpStatus.BAD_REQUEST, "El nombre es obligatorio", request.getRequestURI());
    }

    @Test
    void validacionSinErroresDeCampoDebeUsarMensajeGenerico() {
        MockHttpServletRequest request = request("/api/v1/productos");
        BeanPropertyBindingResult binding = new BeanPropertyBindingResult(
                new ProductoCreateRequest(), "request");

        ResponseEntity<DtoApiError> response = handler.handleValidation(
                new MethodArgumentNotValidException(methodParameter(), binding), request);

        assertError(response, HttpStatus.BAD_REQUEST, "Datos invalidos", request.getRequestURI());
    }

    private MockHttpServletRequest request(String uri) {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI(uri);
        return request;
    }

    private MethodParameter methodParameter() {
        try {
            return new MethodParameter(
                    GlobalExceptionHandlerTest.class.getDeclaredMethod(
                            "metodoValidado", ProductoCreateRequest.class),
                    0);
        } catch (NoSuchMethodException ex) {
            throw new IllegalStateException(ex);
        }
    }

    @SuppressWarnings("unused")
    private void metodoValidado(ProductoCreateRequest request) {
        // Firma auxiliar para construir MethodArgumentNotValidException de forma realista.
    }

    private void assertError(ResponseEntity<DtoApiError> response, HttpStatus status,
            String message, String path) {
        assertThat(response.getStatusCode()).isEqualTo(status);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo(status.value());
        assertThat(response.getBody().getMessage()).isEqualTo(message);
        assertThat(response.getBody().getPath()).isEqualTo(path);
        assertThat(response.getBody().getTimestamp()).isNotNull();
    }
}
