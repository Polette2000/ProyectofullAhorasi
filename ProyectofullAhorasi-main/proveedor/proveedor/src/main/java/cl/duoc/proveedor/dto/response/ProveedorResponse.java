package cl.duoc.proveedor.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Respuesta con informacion del proveedor")
public class ProveedorResponse {

    @Schema(description = "Identificador unico del proveedor", example = "1")
    private Long idProveedor;

    @Schema(description = "Nombre comercial del proveedor", example = "Aromas Chile SpA")
    private String nombre;

    @Schema(description = "Correo de contacto del proveedor", example = "contacto@aromaschile.cl")
    private String correo;

    @Schema(description = "Telefono de contacto del proveedor", example = "+56912345678")
    private String telefono;

    @Schema(description = "Direccion fisica del proveedor", example = "Av. Principal 123, Santiago")
    private String direccion;
}