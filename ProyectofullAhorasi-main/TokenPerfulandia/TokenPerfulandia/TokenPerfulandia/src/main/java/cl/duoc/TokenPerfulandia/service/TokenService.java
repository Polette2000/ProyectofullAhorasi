package cl.duoc.TokenPerfulandia.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;

import cl.duoc.TokenPerfulandia.dto.request.AuthRequestDto;
import cl.duoc.TokenPerfulandia.dto.response.AuthResponseDto;
import cl.duoc.TokenPerfulandia.dto.response.UsuarioValidacionResponseDto;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class TokenService {

    private final WebClient webClient;
    private final JwtService jwtService;

    @Value("${usuario.service.url}")
    private String usuarioServiceUrl;

    public TokenService(WebClient webClient, JwtService jwtService) {
        this.webClient = webClient;
        this.jwtService = jwtService;
    }

    public AuthResponseDto generarToken(AuthRequestDto request) {
        log.info("Validando credenciales para email={}", request.getEmail());

        UsuarioValidacionResponseDto usuario = webClient.post()
                .uri(usuarioServiceUrl + "/api/v1/usuarios/validar-login")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(UsuarioValidacionResponseDto.class)
                .block();

        if (usuario == null || !Boolean.TRUE.equals(usuario.getValido())) {
            log.warn("Credenciales invalidas para email={}", request.getEmail());
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Usuario o contrasena incorrectos");
        }

        String token = jwtService.generarToken(usuario);
        log.info("Token generado correctamente para email={} rol={}", usuario.getEmail(), usuario.getRol());

        return new AuthResponseDto(
                token,
                usuario.getEmail(),
                usuario.getRol());
    }
}
