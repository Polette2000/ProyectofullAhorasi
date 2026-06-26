package cl.duoc.Perfulandiabilling.dto.Response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PagoResponse {


    private Long idPago;      // Identificador único del pago
    private Long idUsuario;   // Usuario que realizó el pago
    private Long idOrden;     // Orden asociada al pago
    private Double monto;     // Monto del pago
    private String metodo;    // Método de pago utilizado
    private String estado;    // COMPLETADO, PENDIENTE
}
