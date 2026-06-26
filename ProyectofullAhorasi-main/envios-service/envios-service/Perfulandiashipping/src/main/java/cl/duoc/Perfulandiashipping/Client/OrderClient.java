package cl.duoc.Perfulandiashipping.Client;

import cl.duoc.Perfulandiashipping.Exception.ResourceNotFoundException;
import cl.duoc.Perfulandiashipping.dto.Response.OrderResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
@RequiredArgsConstructor
public class OrderClient {

    private final WebClient.Builder webClientBuilder;
    private final HttpServletRequest httpServletRequest;

    @Value("${order.service.url}")
    private String orderServiceUrl;

    public OrderResponse obtenerPedido(Long idPedido) {
        String token = httpServletRequest.getHeader("Authorization");

        try {
            return webClientBuilder.build()
                    .get()
                    .uri(orderServiceUrl + "/api/v1/pedidos/" + idPedido)
                    .header("Authorization", token)
                    .retrieve()
                    .bodyToMono(OrderResponse.class)
                    .block();
        } catch (Exception e) {
            throw new ResourceNotFoundException("Pedido no encontrado con ID: " + idPedido);
        }
    }
}
