package cl.duoc.Perfulandiabilling.dto.Response;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FacturaResponse {

    private Long idFactura;
    private Long idUsuario;
    private Long idSucursal;
    private String nombreSucursal;
    private Long idPago;
    private List<FacturaProductoResponse> productos;
    private Integer cantidadTotal;
    private Double montoTotal;
    private String descripcion;
    private String estado;
    private LocalDateTime fechaEmision;
}
