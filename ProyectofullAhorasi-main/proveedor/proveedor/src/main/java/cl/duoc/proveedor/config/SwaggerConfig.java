package cl.duoc.proveedor.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// Indica que esta clase contiene configuracion Spring
@Configuration
public class SwaggerConfig {

    // Nombre usado por Swagger para identificar el esquema JWT
    private static final String SECURITY_SCHEME_NAME = "bearerAuth";

    // Registra la configuracion principal de OpenAPI
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                // Informacion visible en la pantalla principal de Swagger
                .info(new Info()
                        .title("Proveedor Service API")
                        .version("1.0")
                        .description("API para gestionar proveedores de Perfulandia"))

                // Agrega el requisito de seguridad JWT a la documentacion
                .addSecurityItem(new SecurityRequirement().addList(SECURITY_SCHEME_NAME))

                // Configura el boton Authorize para enviar token Bearer
                .components(new Components().addSecuritySchemes(
                        SECURITY_SCHEME_NAME,
                        new SecurityScheme()
                                .name(SECURITY_SCHEME_NAME)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")));
    }
}