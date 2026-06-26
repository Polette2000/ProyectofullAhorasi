package cl.duoc.PerfulandiaOrder.Client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.reactive.function.client.WebClient;

import cl.duoc.PerfulandiaOrder.Exception.ResourceNotFoundException;
import cl.duoc.PerfulandiaOrder.dto.Response.ProductoResponse;
import jakarta.servlet.http.HttpServletRequest;

@Component
public class ProductoClient {

    private final WebClient webClient;

    public ProductoClient(WebClient.Builder webClientBuilder,
            @Value("${producto.service.url}") String productoServiceUrl) {
        this.webClient = webClientBuilder.baseUrl(productoServiceUrl).build();
    }

    public ProductoResponse obtenerProductoPorId(Long idProducto) {
        try {
            String token = obtenerTokenActual();

            return webClient.get()
                    .uri("/api/v1/productos/{idProducto}", idProducto)
                    .header(HttpHeaders.AUTHORIZATION, token)
                    .retrieve()
                    .bodyToMono(ProductoResponse.class)
                    .block();
        } catch (Exception ex) {
            throw new ResourceNotFoundException("Producto no encontrado con ID: " + idProducto);
        }
    }

    private String obtenerTokenActual() {
        ServletRequestAttributes attributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        if (attributes == null) {
            return "";
        }

        HttpServletRequest request = attributes.getRequest();
        String token = request.getHeader(HttpHeaders.AUTHORIZATION);

        return token != null ? token : "";
    }
}
