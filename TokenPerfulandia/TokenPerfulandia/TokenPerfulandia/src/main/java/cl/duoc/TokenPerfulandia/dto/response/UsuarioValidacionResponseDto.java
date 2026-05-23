package cl.duoc.TokenPerfulandia.dto.response;

import lombok.Data;

@Data
public class UsuarioValidacionResponseDto {
    private Integer idUsuario;
    private String nombre;
    private String email;
    private Boolean valido;
    private String rol;
}
