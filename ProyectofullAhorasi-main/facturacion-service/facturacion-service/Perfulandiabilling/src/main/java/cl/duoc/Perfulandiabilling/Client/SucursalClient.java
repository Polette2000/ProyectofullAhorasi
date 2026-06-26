package cl.duoc.Perfulandiabilling.Client;

import cl.duoc.Perfulandiabilling.Exception.ResourceNotFoundException;
import cl.duoc.Perfulandiabilling.dto.Response.SucursalResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
@RequiredArgsConstructor
public class SucursalClient {

    private final WebClient.Builder webClientBuilder;
    private final HttpServletRequest httpServletRequest;

    @Value("${sucursal.service.url}")
    private String sucursalServiceUrl;

    public SucursalResponse obtenerSucursal(Long idSucursal) {
        String token = httpServletRequest.getHeader("Authorization");

        try {
            return webClientBuilder.build()
                    .get()
                    .uri(sucursalServiceUrl + "/api/v1/sucursales/" + idSucursal)
                    .header("Authorization", token)
                    .retrieve()
                    .bodyToMono(SucursalResponse.class)
                    .block();
        } catch (Exception e) {
            throw new ResourceNotFoundException("Sucursal no encontrada con ID: " + idSucursal);
        }
    }
}
