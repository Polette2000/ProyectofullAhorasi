package cl.duoc.Perfulandiashipping.Exception;

public class ResourceNotFoundException extends RuntimeException {

    // Constructor con mensaje
    public ResourceNotFoundException(String mensaje) {
        super(mensaje);
    }
}