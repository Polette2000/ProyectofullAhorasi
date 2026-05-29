package cl.duoc.PerfulandiaOrder.dto.Response;

import lombok.Data;

@Data
public class ProductoResponse {

    private Long idProducto;
    private String nombre;
    private String descripcion;
    private Integer precio;

}
