package cl.duoc.Inventory.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// DTO usado para recibir datos al crear un inventario
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Datos requeridos para crear un registro de inventario")
public class InventoryCreateRequest {

    // ID del producto que se registrara en inventario
    @Schema(description = "ID del producto existente", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "El Id del producto es obligatorio")
    private Long idProducto;

    // ID de la sucursal donde se registrara el stock
    @Schema(description = "ID de la sucursal existente", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "El Id de la sucursal es obligatorio")
    private Long idSucursal;

    // Cantidad inicial disponible; no puede ser nula ni negativa
    @Schema(description = "Cantidad de unidades disponibles", example = "25", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "El stock disponible es obligatorio")
    @Min(value = 0, message = "El stock disponible no puede ser negativo")
    private Integer stockDisponible;
}