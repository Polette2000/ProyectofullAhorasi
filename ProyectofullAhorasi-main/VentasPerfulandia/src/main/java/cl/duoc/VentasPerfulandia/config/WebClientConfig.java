package cl.duoc.VentasPerfulandia.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Bean
    public WebClient webClientOrder(@Value("${order.service.url}") String orderUrl) {
        return WebClient.builder()
                .baseUrl(orderUrl)
                .defaultHeader("Content-Type", "application/json")
                .build();
    }

    @Bean
    public WebClient webClientFacturacion(@Value("${facturacion.service.url}") String facturacionUrl) {
        return WebClient.builder()
                .baseUrl(facturacionUrl)
                .defaultHeader("Content-Type", "application/json")
                .build();
    }
}
