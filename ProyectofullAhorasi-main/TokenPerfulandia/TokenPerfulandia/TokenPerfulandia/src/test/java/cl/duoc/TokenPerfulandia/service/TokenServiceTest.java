package cl.duoc.TokenPerfulandia.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;

import cl.duoc.TokenPerfulandia.dto.request.AuthRequestDto;
import cl.duoc.TokenPerfulandia.dto.response.AuthResponseDto;
import cl.duoc.TokenPerfulandia.dto.response.UsuarioValidacionResponseDto;
import reactor.core.publisher.Mono;

@ExtendWith(MockitoExtension.class)
class TokenServiceTest {

    @Mock
    private WebClient webClient;

    @Mock
    private WebClient.RequestBodyUriSpec requestBodyUriSpec;

    @Mock
    private WebClient.RequestBodySpec requestBodySpec;

    @Mock
    private WebClient.RequestHeadersSpec<?> requestHeadersSpec;

    @Mock
    private WebClient.ResponseSpec responseSpec;

    @Mock
    private JwtService jwtService;

    private TokenService tokenService;

    @BeforeEach
    void setUp() {
        tokenService = new TokenService(webClient, jwtService);
        ReflectionTestUtils.setField(tokenService, "usuarioServiceUrl", "http://usuarios-test");
    }

    private AuthRequestDto crearRequest() {
        AuthRequestDto request = new AuthRequestDto();
        request.setEmail("usuario@test.cl");
        request.setContrasena("clave123");
        return request;
    }

    private UsuarioValidacionResponseDto crearUsuarioValido(String rol) {
        UsuarioValidacionResponseDto usuario = new UsuarioValidacionResponseDto();
        usuario.setIdUsuario(1);
        usuario.setNombre("Usuario Test");
        usuario.setEmail("usuario@test.cl");
        usuario.setValido(true);
        usuario.setRol(rol);
        return usuario;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private void simularRespuestaUsuario(Mono<UsuarioValidacionResponseDto> respuesta) {
        when(webClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri("http://usuarios-test/api/v1/usuarios/validar-login"))
                .thenReturn(requestBodySpec);
        when(requestBodySpec.bodyValue(any(AuthRequestDto.class)))
                .thenReturn((WebClient.RequestHeadersSpec) requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(UsuarioValidacionResponseDto.class)).thenReturn(respuesta);
    }

    @Test
    void generarToken_debeRetornarTokenCuandoUsuarioEsValido() {
        AuthRequestDto request = crearRequest();
        UsuarioValidacionResponseDto usuario = crearUsuarioValido("USER");
        simularRespuestaUsuario(Mono.just(usuario));
        when(jwtService.generarToken(usuario)).thenReturn("jwt-user");

        AuthResponseDto response = tokenService.generarToken(request);

        assertThat(response.getToken()).isEqualTo("jwt-user");
        assertThat(response.getEmail()).isEqualTo("usuario@test.cl");
        assertThat(response.getRol()).isEqualTo("USER");
        verify(jwtService).generarToken(usuario);
    }

    @Test
    void generarToken_debeRetornarRolAdminCuandoUsuarioEsAdmin() {
        AuthRequestDto request = crearRequest();
        UsuarioValidacionResponseDto usuario = crearUsuarioValido("ADMIN");
        simularRespuestaUsuario(Mono.just(usuario));
        when(jwtService.generarToken(usuario)).thenReturn("jwt-admin");

        AuthResponseDto response = tokenService.generarToken(request);

        assertThat(response.getToken()).isEqualTo("jwt-admin");
        assertThat(response.getRol()).isEqualTo("ADMIN");
    }

    @Test
    void generarToken_debeLanzarUnauthorizedCuandoUsuarioEsInvalido() {
        AuthRequestDto request = crearRequest();
        UsuarioValidacionResponseDto usuario = crearUsuarioValido("USER");
        usuario.setValido(false);
        simularRespuestaUsuario(Mono.just(usuario));

        assertThatThrownBy(() -> tokenService.generarToken(request))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Usuario o contrasena incorrectos")
                .satisfies(ex -> assertThat(((ResponseStatusException) ex).getStatusCode().value()).isEqualTo(401));
        verify(jwtService, never()).generarToken(any(UsuarioValidacionResponseDto.class));
    }

    @Test
    void generarToken_debeLanzarUnauthorizedCuandoUsuarioServiceRetornaNull() {
        AuthRequestDto request = crearRequest();
        simularRespuestaUsuario(Mono.empty());

        assertThatThrownBy(() -> tokenService.generarToken(request))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Usuario o contrasena incorrectos")
                .satisfies(ex -> assertThat(((ResponseStatusException) ex).getStatusCode().value()).isEqualTo(401));
        verify(jwtService, never()).generarToken(any(UsuarioValidacionResponseDto.class));
    }

    @Test
    void generarToken_debePropagarErrorCuandoFallaUsuarioService() {
        AuthRequestDto request = crearRequest();
        simularRespuestaUsuario(Mono.error(new RuntimeException("servicio usuarios no disponible")));

        assertThatThrownBy(() -> tokenService.generarToken(request))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("servicio usuarios no disponible");
        verify(jwtService, never()).generarToken(any(UsuarioValidacionResponseDto.class));
    }
}
