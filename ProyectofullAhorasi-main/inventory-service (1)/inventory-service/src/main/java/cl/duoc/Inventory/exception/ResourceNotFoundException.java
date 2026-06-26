package cl.duoc.Inventory.exception;

// Excepcion personalizada para recursos no encontrados
public class ResourceNotFoundException extends RuntimeException {

    // Recibe el mensaje que se mostrara al cliente
    public ResourceNotFoundException(String msg) {
        super(msg);
    }
}
