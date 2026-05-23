package cl.duoc.Perfulandiashipping.dto.Response;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EnvioResponse {
    private Long idEnvio;

    private Long idOrden;

    private Long idUsuario;

    private String estadoPedido;

    private String direccion;

    private String numeroSeguimiento;

    private String estado; // PENDIENTE, EN_TRANSITO, ENTREGADO

    private LocalDate fechaEstimadaInicio;

    private LocalDate fechaEstimadaFin;
}
