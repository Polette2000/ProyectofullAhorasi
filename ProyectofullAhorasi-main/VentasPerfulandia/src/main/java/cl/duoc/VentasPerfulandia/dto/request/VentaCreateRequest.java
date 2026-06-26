package cl.duoc.VentasPerfulandia.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VentaCreateRequest {

    @NotNull(message = "El ID del pedido es obligatorio")
    private Long idPedido;

    @NotNull(message = "El ID del pago es obligatorio")
    private Long idPago;

    @NotNull(message = "El ID de la factura es obligatorio")
    private Long idFactura;

    @NotBlank(message = "El canal de venta es obligatorio")
    private String canalVenta;
}
