package cl.duoc.Inventory.dto.response;

import lombok.Data;

@Data
public class ProductoResponse {

    private Long idProducto;
    private String nombre;
    private String descripcion;
    private Integer precio;
}
