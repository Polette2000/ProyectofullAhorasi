package cl.duoc.proveedor.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import lombok.RequiredArgsConstructor;

// Indica que esta clase contiene configuracion Spring
@Configuration
// Genera constructor automaticamente
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    // Registra configuracion de seguridad como Bean
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http)
            throws Exception {

        return http
                // Desactiva proteccion CSRF
                .csrf(csrf -> csrf.disable())
                .formLogin(form -> form.disable())
                .httpBasic(basic -> basic.disable())

                // No crear sesiones HTTP
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // Configura reglas de autorizacion
                .authorizeHttpRequests(auth -> auth
                        // Permite que Spring muestre errores correctamente
                        .requestMatchers("/error").permitAll()
                        // Los endpoints de proveedores requieren token
                        .anyRequest().authenticated())

                // Agrega el filtro JWT antes del filtro estandar de Spring
                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class)

                .build();
    }
}
