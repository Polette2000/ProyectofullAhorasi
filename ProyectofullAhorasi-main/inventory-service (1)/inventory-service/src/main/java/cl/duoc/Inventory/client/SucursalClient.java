package cl.duoc.Inventory.client;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import cl.duoc.Inventory.dto.response.SucursalResponse;
import cl.duoc.Inventory.exception.ResourceNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class SucursalClient {

    @Qualifier("webClientSucursal")
    private final WebClient webClientSucursal;

    private final HttpServletRequest httpServletRequest;

    // Busca una sucursal en sucursal-service usando su ID.
    public SucursalResponse obtenerSucursalPorId(Long idSucursal) {
        return webClientSucursal.get()
                .uri("/api/v1/sucursales/{idSucursal}", idSucursal)
                .header("Authorization", obtenerToken())
                .retrieve()
                .onStatus(
                        status -> status.value() == 404,
                        response -> Mono.error(new ResourceNotFoundException(
                                "Sucursal no encontrada con ID: " + idSucursal)))
                .bodyToMono(SucursalResponse.class)
                .block();
    }

    // Reutiliza el token recibido por inventory-service.
    private String obtenerToken() {
        return httpServletRequest.getHeader("Authorization");
    }
}
