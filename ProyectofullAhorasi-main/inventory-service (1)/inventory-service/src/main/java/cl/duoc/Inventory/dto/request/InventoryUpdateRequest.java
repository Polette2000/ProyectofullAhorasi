package cl.duoc.Inventory.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// DTO usado para recibir datos al actualizar stock de inventario
@AllArgsConstructor
@NoArgsConstructor
@Data
@Schema(description = "Datos requeridos para actualizar stock de inventario")
public class InventoryUpdateRequest {

    // ID del producto cuyo stock sera actualizado
    @Schema(description = "ID del producto existente", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "El Id del producto es obligatorio")
    private Long idProducto;

    // ID de la sucursal donde se actualizara el stock
    @Schema(description = "ID de la sucursal existente", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "El Id de la sucursal es obligatorio")
    private Long idSucursal;

    // Nueva cantidad disponible; no puede ser nula ni negativa
    @Schema(description = "Nueva cantidad de unidades disponibles", example = "40", requiredMode = Schema.RequiredMode.REQUIRED)
    @Min(value = 0, message = "El stock disponible no puede ser negativo")
    @NotNull(message = "El stock disponible es obligatorio")
    private Integer stockDisponible;
}