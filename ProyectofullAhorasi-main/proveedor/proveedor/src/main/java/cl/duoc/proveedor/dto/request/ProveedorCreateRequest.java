package cl.duoc.proveedor.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "Datos requeridos para crear un proveedor")
public class ProveedorCreateRequest {

    @NotBlank(message = "El nombre del proveedor es obligatorio")
    @Size(max = 100, message = "El nombre no puede superar los 100 caracteres")
    @Schema(description = "Nombre comercial del proveedor", example = "Aromas Chile SpA", requiredMode = Schema.RequiredMode.REQUIRED)
    private String nombre;

    @NotBlank(message = "El correo del proveedor es obligatorio")
    @Email(message = "El correo debe tener un formato valido")
    @Size(max = 100, message = "El correo no puede superar los 100 caracteres")
    @Schema(description = "Correo de contacto del proveedor", example = "contacto@aromaschile.cl", requiredMode = Schema.RequiredMode.REQUIRED)
    private String correo;

    @NotBlank(message = "El telefono del proveedor es obligatorio")
    @Size(max = 20, message = "El telefono no puede superar los 20 caracteres")
    @Schema(description = "Telefono de contacto del proveedor", example = "+56912345678", requiredMode = Schema.RequiredMode.REQUIRED)
    private String telefono;

    @NotBlank(message = "La direccion del proveedor es obligatoria")
    @Size(max = 255, message = "La direccion no puede superar los 255 caracteres")
    @Schema(description = "Direccion fisica del proveedor", example = "Av. Principal 123, Santiago", requiredMode = Schema.RequiredMode.REQUIRED)
    private String direccion;
}