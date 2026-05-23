package cl.duoc.Perfulandiashipping.dto.Request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class EnvioEstadoRequest {

    @NotBlank(message = "El estado es obligatorio")
    private String estado;
}
