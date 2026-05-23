package cl.duoc.Perfulandiabilling.dto.Response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FacturaResponse {
    
    private Long idFactura;      // Identificador único de la factura
    private Long idUsuario;      // Usuario al que se emitió la factura
    private Long idPago;         // Pago asociado a la factura
    private Double montoTotal;   // Total de la factura
    private String descripcion;  // Descripción de la factura
    private String estado;       // EMITIDA, CANCELADA
}
