package cl.duoc.Inventory.dto.response;

import lombok.Data;

@Data
public class SucursalResponse {

    private Long idSucursal;
    private String nombre;
    private String direccion;
    private String comuna;
    private String telefono;
}
