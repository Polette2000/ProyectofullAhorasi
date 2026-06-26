package cl.duoc.Inventory.dto.response;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

// DTO usado para devolver informacion de inventario al cliente
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Schema(description = "Respuesta con informacion de inventario")
public class InventoryResponse {

    // ID del registro de inventario
    @Schema(description = "ID del registro de inventario", example = "1")
    private Long idInventory;

    // ID del producto asociado
    @Schema(description = "ID del producto asociado", example = "1")
    private Long idProducto;

    // Nombre del producto obtenido desde producto-service
    @Schema(description = "Nombre del producto obtenido desde producto-service", example = "Perfume floral")
    private String nombreProducto;

    // ID de la sucursal asociada
    @Schema(description = "ID de la sucursal asociada", example = "1")
    private Long idSucursal;

    // Stock disponible actualmente
    @Schema(description = "Cantidad de stock disponible", example = "25")
    private Integer stockDisponible;

    // Fecha y hora de la ultima actualizacion
    @Schema(description = "Fecha y hora de la ultima actualizacion", example = "2026-06-07T18:45:32")
    private LocalDateTime fechaActualizacion;
}