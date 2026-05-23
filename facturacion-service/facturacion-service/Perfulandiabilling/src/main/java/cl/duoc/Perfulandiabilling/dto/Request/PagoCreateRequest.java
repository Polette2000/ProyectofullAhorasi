package cl.duoc.Perfulandiabilling.dto.Request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PagoCreateRequest {

    @NotNull(message = "El ID del usuario es obligatorio")
    private Long idUsuario;

    @NotNull(message = "El ID de la orden es obligatorio")
    private Long idOrden;

    @NotNull(message = "El monto del pago es obligatorio")
    @Positive(message = "El monto debe ser mayor a 0")
    private Double monto;

    @NotBlank(message = "El método de pago es obligatorio")
    private String metodo;
}
