package cl.duoc.PerfulandiaOrder.dto.Response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderItemResponse {

    private Long idItem;
    private Long idProducto;
    private String nombreProducto;
    private int cantidad;
    private double precio;

}
