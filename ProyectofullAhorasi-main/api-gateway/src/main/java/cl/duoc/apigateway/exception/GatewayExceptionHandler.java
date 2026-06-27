package cl.duoc.apigateway.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpStatusCodeException;

@RestControllerAdvice
public class GatewayExceptionHandler {

    @ExceptionHandler(HttpStatusCodeException.class)
    public ResponseEntity<byte[]> handleDownstreamHttpError(HttpStatusCodeException exception) {
        return ResponseEntity
                .status(exception.getStatusCode())
                .headers(exception.getResponseHeaders())
                .body(exception.getResponseBodyAsByteArray());
    }
}
