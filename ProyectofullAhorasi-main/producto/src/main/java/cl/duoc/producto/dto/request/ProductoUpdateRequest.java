package cl.duoc.producto.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Datos requeridos para actualizar un producto")
public class ProductoUpdateRequest {

    @NotBlank(message = "El nombre del producto es obligatorio")
    @Schema(description = "Nombre del producto", example = "Perfume Citrico Verano", requiredMode = Schema.RequiredMode.REQUIRED)
    private String nombre;

    @NotBlank(message = "La descripcion del producto es obligatoria")
    @Schema(description = "Descripcion del producto", example = "Perfume citrico de 100 ml", requiredMode = Schema.RequiredMode.REQUIRED)
    private String descripcion;

    @NotNull(message = "El precio del producto es obligatorio")
    @Min(value = 1, message = "El precio debe ser mayor a cero")
    @Schema(description = "Precio del producto", example = "29990", minimum = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer precio;
}