package cl.duoc.producto.config;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityScheme;

class SwaggerConfigTest {

    @Test
    void customOpenApiDebeConfigurarInformacionYJwt() {
        OpenAPI openAPI = new SwaggerConfig().customOpenAPI();

        assertThat(openAPI.getInfo().getTitle()).isEqualTo("Producto Service API");
        assertThat(openAPI.getInfo().getVersion()).isEqualTo("1.0");
        assertThat(openAPI.getSecurity()).hasSize(1);
        SecurityScheme bearer = openAPI.getComponents().getSecuritySchemes().get("bearerAuth");
        assertThat(bearer.getType()).isEqualTo(SecurityScheme.Type.HTTP);
        assertThat(bearer.getScheme()).isEqualTo("bearer");
        assertThat(bearer.getBearerFormat()).isEqualTo("JWT");
    }
}
