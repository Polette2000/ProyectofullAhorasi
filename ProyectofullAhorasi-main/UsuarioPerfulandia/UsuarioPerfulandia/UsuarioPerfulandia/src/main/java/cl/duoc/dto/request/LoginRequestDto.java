package cl.duoc.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequestDto {

    @NotBlank(message = "El email es obligatorio")
    private String email;

    @NotBlank(message = "La contrasena es obligatoria")
    private String contrasena;
}


