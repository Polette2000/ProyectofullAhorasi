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
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
// Habilita logger SLF4J automaticamente
@Slf4j
public class ProveedorService {

    private final ProveedorRepository proveedorRepository;

    // Lista todos los proveedores.
    public List<ProveedorResponse> listarProveedores() {
        log.info("Iniciando busqueda de todos los proveedores");
        List<ProveedorResponse> proveedores = proveedorRepository.findAll()
                .stream()
                .map(this::convertirAResponse)
                .toList();
        log.info("Cantidad de proveedores encontrados={}", proveedores.size());
        return proveedores;
    }

    // Busca un proveedor por su ID.
    public ProveedorResponse buscarPorId(Long idProveedor) {
        log.info("Buscando proveedor id={}", idProveedor);
        Proveedor proveedor = obtenerProveedorPorId(idProveedor);
        return convertirAResponse(proveedor);
    }

    // Crea un nuevo proveedor.
    public ProveedorResponse crearProveedor(ProveedorCreateRequest request) {
        log.info("Creando proveedor nombre={}", request.getNombre());
        Proveedor proveedor = Proveedor.builder()
                .nombre(request.getNombre())
                .correo(request.getCorreo())
                .telefono(request.getTelefono())
                .direccion(request.getDireccion())
                .build();

        Proveedor proveedorGuardado = proveedorRepository.save(proveedor);
        log.info("Proveedor creado id={}", proveedorGuardado.getIdProveedor());
        return convertirAResponse(proveedorGuardado);
    }

    // Actualiza un proveedor existente.
    public ProveedorResponse actualizarProveedor(Long idProveedor, ProveedorUpdateRequest request) {
        log.info("Actualizando proveedor id={}", idProveedor);
        Proveedor proveedor = obtenerProveedorPorId(idProveedor);

        proveedor.setNombre(request.getNombre());
        proveedor.setCorreo(request.getCorreo());
        proveedor.setTelefono(request.getTelefono());
        proveedor.setDireccion(request.getDireccion());

        Proveedor proveedorActualizado = proveedorRepository.save(proveedor);
        log.info("Proveedor actualizado id={}", proveedorActualizado.getIdProveedor());
        return convertirAResponse(proveedorActualizado);
    }

    // Elimina un proveedor de la base de datos.
    public void eliminarProveedor(Long idProveedor) {
        log.info("Eliminando proveedor id={}", idProveedor);
        Proveedor proveedor = obtenerProveedorPorId(idProveedor);
        proveedorRepository.delete(proveedor);
        log.info("Proveedor eliminado id={}", idProveedor);
    }

    // Busca proveedores por nombre.
    public List<ProveedorResponse> buscarPorNombre(String nombre) {
        log.info("Buscando proveedores por nombre={}", nombre);
        List<ProveedorResponse> proveedores = proveedorRepository.findByNombreContainingIgnoreCase(nombre)
                .stream()
                .map(this::convertirAResponse)
                .toList();
        log.info("Proveedores encontrados por nombre={} cantidad={}", nombre, proveedores.size());
        return proveedores;
    }

    private Proveedor obtenerProveedorPorId(Long idProveedor) {
        return proveedorRepository.findById(idProveedor)
                .orElseThrow(() -> {
                    log.warn("Proveedor no encontrado id={}", idProveedor);
                    return new ResourceNotFoundException("Proveedor no encontrado con ID: " + idProveedor);
                });
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