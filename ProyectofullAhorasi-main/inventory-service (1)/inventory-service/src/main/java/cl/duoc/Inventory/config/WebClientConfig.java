package cl.duoc.Inventory.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    // Cliente HTTP para comunicarse con producto-service.
    @Bean
    public WebClient webClientProducto(@Value("${producto.service.url}") String productoUrl) {
        return WebClient.builder()
                .baseUrl(productoUrl)
                .defaultHeader("Content-Type", "application/json")
                .build();
    }

    // Cliente HTTP para comunicarse con sucursal-service.
    @Bean
    public WebClient webClientSucursal(@Value("${sucursal.service.url}") String sucursalUrl) {
        return WebClient.builder()
                .baseUrl(sucursalUrl)
                .defaultHeader("Content-Type", "application/json")
                .build();
    }
}
