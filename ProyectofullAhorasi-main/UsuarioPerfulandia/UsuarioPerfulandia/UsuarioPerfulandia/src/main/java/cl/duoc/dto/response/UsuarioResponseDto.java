package cl.duoc.dto.response;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class UsuarioResponseDto {
    private Integer idUsuario;
    private String nombre;
    private String email;
    private LocalDate fechaNacimiento;
    private Integer idRol;
    private String nombreRol;

}
