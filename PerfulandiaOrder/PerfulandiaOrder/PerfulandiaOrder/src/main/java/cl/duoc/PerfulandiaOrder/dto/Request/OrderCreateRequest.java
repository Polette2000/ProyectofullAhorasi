package cl.duoc.PerfulandiaOrder.dto.Request;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderCreateRequest {

    @Valid
    @NotEmpty(message = "El pedido debe tener al menos un producto")
    private List<OrderItemCreateRequest> items;

}
