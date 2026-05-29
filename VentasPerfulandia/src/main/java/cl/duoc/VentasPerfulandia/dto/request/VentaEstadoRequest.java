package cl.duoc.VentasPerfulandia.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VentaEstadoRequest {

    @NotBlank(message = "El estado de la venta es obligatorio")
    private String estadoVenta;
}
