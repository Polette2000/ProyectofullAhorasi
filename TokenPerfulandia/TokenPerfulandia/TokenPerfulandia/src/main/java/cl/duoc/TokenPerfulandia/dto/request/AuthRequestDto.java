package cl.duoc.TokenPerfulandia.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AuthRequestDto {

    @NotBlank(message = "El email es obligatorio")
    private String email;

    @NotBlank(message = "La contrasena es obligatoria")
    private String contrasena;
}
