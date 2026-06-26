package cl.duoc.Inventory.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

// Indica que esta clase contiene configuracion de Swagger/OpenAPI
@Configuration
public class SwaggerConfig {

    // Registra la configuracion personalizada de la documentacion Swagger
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()

                // Define informacion general del microservicio
                .info(new Info()
                        .title("Inventory Service API")
                        .version("1.0")
                        .description("API para gestionar el inventario de productos en sucursales"))

                // Agrega seguridad JWT a la documentacion
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))

                // Define el esquema de autenticacion Bearer Token
                .components(new Components()
                        .addSecuritySchemes("bearerAuth",
                                new SecurityScheme()
                                        .name("bearerAuth")
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")));
    }
}