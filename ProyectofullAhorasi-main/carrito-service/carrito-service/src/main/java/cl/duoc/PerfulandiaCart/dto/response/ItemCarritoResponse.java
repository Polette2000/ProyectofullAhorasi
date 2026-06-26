package cl.duoc.PerfulandiaCart.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ItemCarritoResponse {

    private Long idItemCarrito;
    private Long idProducto;
    private String nombreProducto;
    private Integer precioProducto;
    private Integer cantidad;
    private Integer subtotal;
    private Long idUsuario;

}
