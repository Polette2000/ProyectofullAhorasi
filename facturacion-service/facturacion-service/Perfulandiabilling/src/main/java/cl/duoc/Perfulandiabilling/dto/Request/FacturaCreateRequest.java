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
public class FacturaCreateRequest {

    @NotNull(message = "El ID del usuario es obligatorio")
    private Long idUsuario;

    @NotNull(message = "El ID del pago es obligatorio")
    private Long idPago;

    @NotNull(message = "El total de la factura es obligatorio")
    @Positive(message = "El total debe ser mayor a 0")
    private Double montoTotal;

    @NotBlank(message = "La descripción de la factura es obligatoria")
    private String descripcion;
}
