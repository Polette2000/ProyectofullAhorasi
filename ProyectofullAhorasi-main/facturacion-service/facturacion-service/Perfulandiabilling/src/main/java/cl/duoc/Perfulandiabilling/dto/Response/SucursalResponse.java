package cl.duoc.Perfulandiabilling.dto.Response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
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
