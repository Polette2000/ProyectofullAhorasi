package cl.duoc.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UsuarioValidacionResponseDto {

    private Boolean valido;
    private String email;
    private String rol;
}
