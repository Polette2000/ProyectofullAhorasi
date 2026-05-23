package cl.duoc.proveedor.service;

import java.util.List;

import org.springframework.stereotype.Service;

import cl.duoc.proveedor.dto.request.ProveedorCreateRequest;
import cl.duoc.proveedor.dto.request.ProveedorUpdateRequest;
import cl.duoc.proveedor.dto.response.ProveedorResponse;
import cl.duoc.proveedor.exception.ResourceNotFoundException;
import cl.duoc.proveedor.model.Proveedor;
import cl.duoc.proveedor.repository.ProveedorRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProveedorService {

    private final ProveedorRepository proveedorRepository;

    // Lista todos los proveedores.
    public List<ProveedorResponse> listarProveedores() {
        return proveedorRepository.findAll()
                .stream()
                .map(this::convertirAResponse)
                .toList();
    }

    // Busca un proveedor por su ID.
    public ProveedorResponse buscarPorId(Long idProveedor) {
        Proveedor proveedor = obtenerProveedorPorId(idProveedor);
        return convertirAResponse(proveedor);
    }

    // Crea un nuevo proveedor.
    public ProveedorResponse crearProveedor(ProveedorCreateRequest request) {
        Proveedor proveedor = Proveedor.builder()
                .nombre(request.getNombre())
                .correo(request.getCorreo())
                .telefono(request.getTelefono())
                .direccion(request.getDireccion())
                .build();

        Proveedor proveedorGuardado = proveedorRepository.save(proveedor);
        return convertirAResponse(proveedorGuardado);
    }

    // Actualiza un proveedor existente.
    public ProveedorResponse actualizarProveedor(Long idProveedor, ProveedorUpdateRequest request) {
        Proveedor proveedor = obtenerProveedorPorId(idProveedor);

        proveedor.setNombre(request.getNombre());
        proveedor.setCorreo(request.getCorreo());
        proveedor.setTelefono(request.getTelefono());
        proveedor.setDireccion(request.getDireccion());

        Proveedor proveedorActualizado = proveedorRepository.save(proveedor);
        return convertirAResponse(proveedorActualizado);
    }

    // Elimina un proveedor de la base de datos.
    public void eliminarProveedor(Long idProveedor) {
        Proveedor proveedor = obtenerProveedorPorId(idProveedor);
        proveedorRepository.delete(proveedor);
    }

    // Busca proveedores por nombre.
    public List<ProveedorResponse> buscarPorNombre(String nombre) {
        return proveedorRepository.findByNombreContainingIgnoreCase(nombre)
                .stream()
                .map(this::convertirAResponse)
                .toList();
    }

    private Proveedor obtenerProveedorPorId(Long idProveedor) {
        return proveedorRepository.findById(idProveedor)
                .orElseThrow(() -> new ResourceNotFoundException("Proveedor no encontrado con ID: " + idProveedor));
    }

    private ProveedorResponse convertirAResponse(Proveedor proveedor) {
        return ProveedorResponse.builder()
                .idProveedor(proveedor.getIdProveedor())
                .nombre(proveedor.getNombre())
                .correo(proveedor.getCorreo())
                .telefono(proveedor.getTelefono())
                .direccion(proveedor.getDireccion())
                .build();
    }
}
