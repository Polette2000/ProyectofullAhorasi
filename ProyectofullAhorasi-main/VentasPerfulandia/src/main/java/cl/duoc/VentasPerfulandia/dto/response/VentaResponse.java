package cl.duoc.VentasPerfulandia.dto.response;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class VentaResponse {

    private Long idVenta;
    private Long idPedido;
    private Long idUsuario;
    private Long idPago;
    private Long idFactura;
    private Double totalVenta;
    private String estadoVenta;
    private String canalVenta;
    private LocalDateTime fechaVenta;
    private List<VentaDetalleResponse> detalles;
}
