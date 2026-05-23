package cl.duoc.proveedor.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProveedorResponse {

    private Long idProveedor;
    private String nombre;
    private String correo;
    private String telefono;
    private String direccion;
}
