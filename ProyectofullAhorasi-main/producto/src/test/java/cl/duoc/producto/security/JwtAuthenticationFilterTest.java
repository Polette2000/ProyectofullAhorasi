package cl.duoc.producto.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

import jakarta.servlet.FilterChain;

import static org.mockito.Mockito.mock;

class JwtAuthenticationFilterTest {

    private static final String SECRET = "clave-de-prueba-con-longitud-suficiente-123456";

    private JwtAuthenticationFilter filter;
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private FilterChain chain;

    @BeforeEach
    void setUp() {
        filter = new JwtAuthenticationFilter();
        ReflectionTestUtils.setField(filter, "secret", SECRET);
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        chain = mock(FilterChain.class);
        SecurityContextHolder.clearContext();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void debeContinuarCuandoNoHayToken() throws Exception {
        filter.doFilterInternal(request, response, chain);

        verify(chain).doFilter(request, response);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    void debeAutenticarTokenValidoConRoles() throws Exception {
        request.addHeader("Authorization", "Bearer " + token(List.of("ROLE_ADMIN")));

        filter.doFilterInternal(request, response, chain);

        verify(chain).doFilter(request, response);
        assertThat(SecurityContextHolder.getContext().getAuthentication().getName()).isEqualTo("admin");
        assertThat(SecurityContextHolder.getContext().getAuthentication().getAuthorities())
                .extracting("authority")
                .containsExactly("ROLE_ADMIN");
    }

    @Test
    void debeAceptarTokenValidoSinRoles() throws Exception {
        request.addHeader("Authorization", "Bearer " + token(null));

        filter.doFilterInternal(request, response, chain);

        verify(chain).doFilter(request, response);
        assertThat(SecurityContextHolder.getContext().getAuthentication().getAuthorities()).isEmpty();
    }

    @Test
    void debeResponderUnauthorizedCuandoElTokenEsInvalido() throws Exception {
        request.addHeader("Authorization", "Bearer token-invalido");

        filter.doFilterInternal(request, response, chain);

        assertThat(response.getStatus()).isEqualTo(401);
        verify(chain, never()).doFilter(request, response);
    }

    private String token(List<String> roles) {
        var builder = JWT.create()
                .withIssuer("login-service")
                .withSubject("admin");
        if (roles != null) {
            builder.withClaim("roles", roles);
        }
        return builder.sign(Algorithm.HMAC256(SECRET));
    }
}
