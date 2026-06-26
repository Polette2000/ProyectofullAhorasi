package cl.duoc.SucursalesPerfulandia.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SucursalResponse {

    private Integer idSucursal;
    private String nombreSucursal;
    private String direccion;
    private String telefono;
    private String horarioAtencion;
    private Integer idComuna;
    private String nombreComuna;
    private String region;
}
