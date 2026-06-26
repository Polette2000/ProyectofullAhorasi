package cl.duoc.Perfulandiabilling.dto.Request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FacturaCreateRequest {

    @NotNull(message = "El ID del pago es obligatorio")
    private Long idPago;

    @NotNull(message = "El ID de la sucursal es obligatorio")
    private Long idSucursal;

    @NotBlank(message = "La descripcion de la factura es obligatoria")
    private String descripcion;
}
