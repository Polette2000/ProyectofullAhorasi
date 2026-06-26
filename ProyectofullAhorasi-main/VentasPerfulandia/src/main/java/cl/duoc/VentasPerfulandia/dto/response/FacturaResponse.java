package cl.duoc.VentasPerfulandia.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FacturaResponse {

    private Long idFactura;
    private Long idUsuario;
    private Long idPago;
    private Double montoTotal;
    private String descripcion;
    private String estado;
}
