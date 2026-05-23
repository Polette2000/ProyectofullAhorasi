package cl.duoc.Perfulandiabilling.Exception;

// Excepción personalizada para recursos no encontrados en Billing.
// Se usa cuando un Payment o Invoice no existe en la base de datos.
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String msg) {
        super(msg);
    }
}
