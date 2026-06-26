package cl.duoc.Inventory.security;

import java.io.IOException;
import java.util.List;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;

// Indica que Spring debe crear automáticamente esta clase
@Component
// Clase filtro que se ejecuta una vez por request
public class JwtAuthenticationFilter extends OncePerRequestFilter{
    // Lee la propiedad jwt.secret desde application.properties
    @Value("${jwt.secret}")

    // Variable donde se guarda la clave secreta
    private String secret;

    // Método principal del filtro
    @Override
    protected void doFilterInternal(

            // Request HTTP entrante
            HttpServletRequest request,

            // Response HTTP saliente
            HttpServletResponse response,

            // Cadena de filtros de Spring Security
            FilterChain filterChain)

            // Excepciones posibles
            throws ServletException, IOException {

        // Obtiene el header Authorization
        String header = request.getHeader("Authorization");

        // Si no existe Authorization o no comienza con Bearer
        if (header == null || !header.startsWith("Bearer ")) {

            // Continúa normalmente al siguiente filtro
            filterChain.doFilter(request, response);

            // Termina ejecución del método
            return;
        }

        try {

            // Extrae el token quitando "Bearer "
            String token = header.substring(7);

            // Valida el JWT utilizando la clave secreta
            DecodedJWT jwt = JWT.require(

                    // Usa algoritmo HMAC256 y la clave secreta
                    Algorithm.HMAC256(secret))

                    // Verifica que el emisor sea login-service
                    .withIssuer("login-service")

                    // Construye el verificador
                    .build()

                    // Verifica el token
                    .verify(token);

            // Obtiene el username desde el payload JWT
            String username = jwt.getSubject();

            // Obtiene la lista de roles desde el claim roles
            List<String> roles = jwt.getClaim("roles").asList(String.class);

            // Si el token no trae roles, se deja una lista vacía
            if (roles == null) {
                roles = List.of();
            }

            // Convierte los roles del token en permisos de Spring Security
            List<SimpleGrantedAuthority> authorities = roles.stream()

                    // Convierte cada texto ROLE_USER o ROLE_ADMIN en SimpleGrantedAuthority
                    .map(SimpleGrantedAuthority::new)

                    // Convierte el stream en una lista
                    .toList();

            // Crea objeto Authentication para Spring Security
            UsernamePasswordAuthenticationToken authentication =

                    // Constructor Authentication
                    new UsernamePasswordAuthenticationToken(

                            // Usuario autenticado
                            username,

                            // Password null porque JWT ya autenticó
                            null,

                            // Lista de roles/permisos
                            authorities);

            // Guarda la autenticación del usuario en Spring Security
            SecurityContextHolder.getContext()

                    // Define usuario autenticado actual
                    .setAuthentication(authentication);

        } catch (Exception e) {

            // Si JWT es inválido responde 401 Unauthorized
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

            // Termina ejecución
            return;
        }

        // Continúa al siguiente filtro o controller
        filterChain.doFilter(request, response);
    }
}
