package cl.duoc.PerfulandiaOrder.dto.Request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderItemCreateRequest {

    @NotNull(message = "El producto es obligatorio")
    private Long idProducto;

    @Min(value = 1, message = "La cantidad minima es 1")
    private int cantidad;

}
