package cl.duoc.proveedor.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

// Indica que esta clase contiene configuracion Spring
@Configuration
// Genera constructor automaticamente
@RequiredArgsConstructor
public class SecurityConfig {

    // Inyecta automaticamente JwtAuthenticationFilter
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    // Registra configuracion de seguridad como Bean
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http)
            throws Exception {

        // Retorna configuracion final de seguridad
        return http
                // Desactiva proteccion CSRF
                .csrf(csrf -> csrf.disable())

                // Desactiva login por formulario
                .formLogin(form -> form.disable())

                // Desactiva autenticacion basica HTTP
                .httpBasic(basic -> basic.disable())

                // Configura aplicacion Stateless
                .sessionManagement(session ->
                        // No crear sesiones HTTP
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // Configura reglas de autorizacion
                .authorizeHttpRequests(auth -> auth
                        // Permite acceso publico a errores y documentacion Swagger
                        .requestMatchers(
                                "/error",
                                "/doc/swagger-ui.html",
                                "/doc/swagger-ui/index.html",
                                "/doc/swagger-ui/**",
                                "/doc/api-docs",
                                "/doc/api-docs/**",
                                "/swagger-ui.html",
                                "/swagger-ui/**",
                                "/v3/api-docs",
                                "/v3/api-docs/**"
                        ).permitAll()

                        // Cualquier otro endpoint requiere autenticacion
                        .anyRequest().authenticated())

                // Agrega JwtAuthenticationFilter antes del filtro estandar
                .addFilterBefore(
                        // Nuestro filtro JWT personalizado
                        jwtAuthenticationFilter,

                        // Antes del filtro default Spring
                        UsernamePasswordAuthenticationFilter.class)

                // Construye configuracion final
                .build();
    }
}