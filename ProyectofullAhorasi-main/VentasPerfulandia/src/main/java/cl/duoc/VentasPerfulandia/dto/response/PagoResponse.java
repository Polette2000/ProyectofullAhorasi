package cl.duoc.VentasPerfulandia.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PagoResponse {

    private Long idPago;
    private Long idUsuario;
    private Long idOrden;
    private Double monto;
    private String metodo;
    private String estado;
}
