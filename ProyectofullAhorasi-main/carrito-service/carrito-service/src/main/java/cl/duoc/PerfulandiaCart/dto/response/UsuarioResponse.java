package cl.duoc.PerfulandiaCart.dto.response;

import java.time.LocalDate;

import lombok.Data;

@Data
public class UsuarioResponse {

    private Long idUsuario;
    private String nombre;
    private String email;
    private LocalDate fechaNacimiento;
    private Long idRol;
    private String nombreRol;
}
