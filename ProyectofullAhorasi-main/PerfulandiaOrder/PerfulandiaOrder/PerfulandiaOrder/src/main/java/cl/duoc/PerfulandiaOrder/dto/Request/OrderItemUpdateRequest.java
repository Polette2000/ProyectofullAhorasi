package cl.duoc.PerfulandiaOrder.dto.Request;

import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderItemUpdateRequest {

    @Min(value = 1, message = "La cantidad minima es 1")
    private int cantidad;

}
