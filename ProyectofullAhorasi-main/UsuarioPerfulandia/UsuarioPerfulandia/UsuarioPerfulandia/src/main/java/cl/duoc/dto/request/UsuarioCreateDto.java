package cl.duoc.dto.request;

import java.time.LocalDate;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UsuarioCreateDto {
    @NotBlank (message = "El nombre es obligatorio")
    @Size(min = 3, max = 100 , message = "El nombre debe tener entre 3 y 100 caracteres")
    private String nombre;

    @NotBlank(message = "El email es obligatorio ")
    @Email(message = "El email debe tener un formato valido")
    private String email;

    @NotNull(message ="La fecha de nacimiento es obligatoria")
    private LocalDate fechaNacimiento;

    @NotBlank(message = "La contraseña es obligatoria ")
    @Size (min = 6 , max = 15, message = " La contraseña debe tener entre 6 y 15 caracteres")
    private String contrasena;

    @NotNull(message = "El rol es obligatorio")
    private Integer idRol;

    
}
