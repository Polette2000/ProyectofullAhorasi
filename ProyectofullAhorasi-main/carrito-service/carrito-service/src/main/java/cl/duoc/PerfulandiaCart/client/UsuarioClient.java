package cl.duoc.PerfulandiaCart.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.reactive.function.client.WebClient;

import cl.duoc.PerfulandiaCart.dto.response.UsuarioResponse;
import cl.duoc.PerfulandiaCart.exception.ResourceNotFoundException;
import jakarta.servlet.http.HttpServletRequest;

@Component
public class UsuarioClient {

    private final WebClient webClient;

    public UsuarioClient(WebClient.Builder webClientBuilder,
            @Value("${usuario.service.url}") String usuarioServiceUrl) {
        this.webClient = webClientBuilder.baseUrl(usuarioServiceUrl).build();
    }

    public UsuarioResponse obtenerUsuarioPorId(Long idUsuario) {
        try {
            String token = obtenerTokenActual();

            return webClient.get()
                    .uri("/api/v1/usuarios/{idUsuario}", idUsuario)
                    .header(HttpHeaders.AUTHORIZATION, token)
                    .retrieve()
                    .bodyToMono(UsuarioResponse.class)
                    .block();
        } catch (Exception ex) {
            throw new ResourceNotFoundException("Usuario no encontrado con ID: " + idUsuario);
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
