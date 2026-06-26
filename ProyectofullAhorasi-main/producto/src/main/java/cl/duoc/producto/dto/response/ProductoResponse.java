package cl.duoc.producto.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Respuesta con informacion del producto")
public class ProductoResponse {

    @Schema(description = "Identificador unico del producto", example = "1")
    private Long idProducto;

    @Schema(description = "Nombre del producto", example = "Perfume Floral Primavera")
    private String nombre;

    @Schema(description = "Descripcion del producto", example = "Perfume floral de 100 ml")
    private String descripcion;

    @Schema(description = "Precio del producto", example = "24990")
    private Integer precio;
}