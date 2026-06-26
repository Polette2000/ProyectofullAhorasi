package cl.duoc.Perfulandiabilling.dto.Response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderItemResponse {

    private Long idItem;
    private Long idProducto;
    private String nombreProducto;
    private Integer cantidad;
    private Double precio;
}
