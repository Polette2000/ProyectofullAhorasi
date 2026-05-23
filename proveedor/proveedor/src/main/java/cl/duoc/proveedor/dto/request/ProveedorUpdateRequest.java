package cl.duoc.proveedor.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ProveedorUpdateRequest {

    @NotBlank(message = "El nombre del proveedor es obligatorio")
    @Size(max = 100, message = "El nombre no puede superar los 100 caracteres")
    private String nombre;

    @NotBlank(message = "El correo del proveedor es obligatorio")
    @Email(message = "El correo debe tener un formato valido")
    @Size(max = 100, message = "El correo no puede superar los 100 caracteres")
    private String correo;

    @NotBlank(message = "El telefono del proveedor es obligatorio")
    @Size(max = 20, message = "El telefono no puede superar los 20 caracteres")
    private String telefono;

    @NotBlank(message = "La direccion del proveedor es obligatoria")
    @Size(max = 255, message = "La direccion no puede superar los 255 caracteres")
    private String direccion;
}
