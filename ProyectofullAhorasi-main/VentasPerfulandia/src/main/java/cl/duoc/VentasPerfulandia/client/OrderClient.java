package cl.duoc.VentasPerfulandia.client;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import cl.duoc.VentasPerfulandia.dto.response.OrderResponse;
import cl.duoc.VentasPerfulandia.exception.ResourceNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class OrderClient {

    @Qualifier("webClientOrder")
    private final WebClient webClientOrder;

    private final HttpServletRequest httpServletRequest;

    public OrderResponse obtenerPedidoPorId(Long idPedido) {
        return webClientOrder.get()
                .uri("/api/v1/pedidos/{idPedido}", idPedido)
                .header("Authorization", obtenerToken())
                .retrieve()
                .onStatus(
                        status -> status.value() == 404,
                        response -> Mono.error(new ResourceNotFoundException(
                                "Pedido no encontrado con ID: " + idPedido)))
                .bodyToMono(OrderResponse.class)
                .block();
    }

    private String obtenerToken() {
        return httpServletRequest.getHeader("Authorization");
    }
}
