package cl.duoc.Inventory.client;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import cl.duoc.Inventory.dto.response.ProductoResponse;
import cl.duoc.Inventory.exception.ResourceNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class ProductoClient {

    @Qualifier("webClientProducto")
    private final WebClient webClientProducto;

    private final HttpServletRequest httpServletRequest;

    // Busca un producto en producto-service usando su ID.
    public ProductoResponse obtenerProductoPorId(Long idProducto) {
        return webClientProducto.get()
                .uri("/api/v1/productos/{idProducto}", idProducto)
                .header("Authorization", obtenerToken())
                .retrieve()
                .onStatus(
                        status -> status.value() == 404,
                        response -> Mono.error(new ResourceNotFoundException(
                                "Producto no encontrado con ID: " + idProducto)))
                .bodyToMono(ProductoResponse.class)
                .block();
    }

    // Reutiliza el token recibido por inventory-service.
    private String obtenerToken() {
        return httpServletRequest.getHeader("Authorization");
    }
}
