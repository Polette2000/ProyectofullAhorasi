package cl.duoc.Inventory.security;

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

            // Puede lanzar excepciones al construir la configuracion
            throws Exception {

        // Retorna la configuracion final de seguridad
        return http

                // Desactiva CSRF porque la API trabaja sin sesiones y usa JWT
                .csrf(csrf -> csrf.disable())

                // Desactiva formulario de login por defecto de Spring Security
                .formLogin(form -> form.disable())

                // Desactiva autenticacion HTTP Basic
                .httpBasic(basic -> basic.disable())

                // Configura la aplicacion como Stateless
                .sessionManagement(session ->

                        // No crea sesiones HTTP en el servidor
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS))

                // Configura reglas de autorizacion para los endpoints
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

                        // Cualquier otro endpoint requiere autenticacion JWT
                        .anyRequest().authenticated())

                // Agrega el filtro JWT antes del filtro estandar de Spring Security
                .addFilterBefore(

                        // Filtro personalizado que valida el token
                        jwtAuthenticationFilter,

                        // Filtro estandar antes del cual se ejecutara el JWT
                        UsernamePasswordAuthenticationFilter.class)

                // Construye y devuelve la configuracion final
                .build();
    }
}
