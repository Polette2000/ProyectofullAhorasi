package cl.duoc.Perfulandiashipping.dto.Request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import java.time.LocalDate;

@Data
public class EnvioRequest {

   @NotNull(message = "El ID de la orden es obligatorio")
    private Long idOrden;

    @NotBlank(message = "La dirección es obligatoria")
    @Size(max = 200, message = "La dirección no puede superar 200 caracteres")
    private String direccion;

    @Size(max = 50, message = "El número de seguimiento no puede superar 50 caracteres")
    private String numeroSeguimiento;

    private String estado; // PENDIENTE, EN_TRANSITO, ENTREGADO

    @NotNull(message = "La fecha estimada de inicio es obligatoria")
    @Future(message = "La fecha de inicio debe ser una fecha futura")
    private LocalDate fechaEstimadaInicio;

    @NotNull(message = "La fecha estimada de fin es obligatoria")
    @Future(message = "La fecha de fin debe ser una fecha futura")
    private LocalDate fechaEstimadaFin;
}
