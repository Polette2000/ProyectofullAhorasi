package cl.duoc.producto.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import lombok.RequiredArgsConstructor;

// Indica que esta clase contiene configuracion de Spring Security
@Configuration

// Genera constructor automaticamente para inyectar dependencias final
@RequiredArgsConstructor
public class SecurityConfig {

    // Filtro personalizado encargado de validar el token JWT en cada request
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    // Registra la configuracion de seguridad como Bean de Spring
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http)
            throws Exception {

        return http
                // Desactiva CSRF porque la API trabaja sin sesiones y usa JWT
                .csrf(csrf -> csrf.disable())
                .formLogin(form -> form.disable())
                .httpBasic(basic -> basic.disable())

                // No crea sesiones HTTP en el servidor
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // Configura reglas de autorizacion
                .authorizeHttpRequests(auth -> auth
                        // Permite errores y documentacion Swagger sin token
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

                        // Los endpoints de productos requieren token JWT
                        .anyRequest().authenticated())

                // Agrega el filtro JWT antes del filtro estandar de Spring Security
                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class)

                .build();
    }
}