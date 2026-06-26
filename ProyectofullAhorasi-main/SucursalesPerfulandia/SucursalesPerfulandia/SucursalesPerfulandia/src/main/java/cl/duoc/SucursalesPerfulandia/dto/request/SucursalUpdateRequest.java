package cl.duoc.SucursalesPerfulandia.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SucursalUpdateRequest {

    @NotBlank(message = "El nombre de la sucursal es obligatorio")
    private String nombreSucursal;

    @NotBlank(message = "La direccion es obligatoria")
    private String direccion;

    @NotBlank(message = "El telefono es obligatorio")
    private String telefono;

    @NotBlank(message = "El horario de atencion es obligatorio")
    private String horarioAtencion;

    @NotNull(message = "El ID de la comuna es obligatorio")
    private Integer idComuna;
}
