package cl.duoc.producto.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductoUpdateRequest {

    @NotBlank(message = "El nombre del producto es obligatorio")
    private String nombre;

    @NotBlank(message = "La descripcion del producto es obligatoria")
    private String descripcion;

    @NotNull(message = "El precio del producto es obligatorio")
    @Min(value = 1, message = "El precio debe ser mayor a cero")
    private Integer precio;
}
