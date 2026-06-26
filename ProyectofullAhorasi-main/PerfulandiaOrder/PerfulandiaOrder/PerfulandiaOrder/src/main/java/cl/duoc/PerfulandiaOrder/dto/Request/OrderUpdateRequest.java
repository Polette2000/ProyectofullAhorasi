package cl.duoc.PerfulandiaOrder.dto.Request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderUpdateRequest {

    @NotBlank(message = "El estado es obligatorio")
    private String estado;

}
