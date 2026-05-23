package cl.duoc.proveedor.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cl.duoc.proveedor.dto.request.ProveedorCreateRequest;
import cl.duoc.proveedor.dto.request.ProveedorUpdateRequest;
import cl.duoc.proveedor.dto.response.ProveedorResponse;
import cl.duoc.proveedor.service.ProveedorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/proveedores")
@RequiredArgsConstructor
public class ProveedorController {

    private final ProveedorService proveedorService;

    /**
     * Lista todos los proveedores registrados.
     * Devuelve 200 OK con la lista de proveedores.
     */
    @GetMapping
    public ResponseEntity<List<ProveedorResponse>> listarProveedores() {
        List<ProveedorResponse> proveedores = proveedorService.listarProveedores();
        return ResponseEntity.ok(proveedores);
    }

    /**
     * Busca un proveedor por su ID.
     * Devuelve 200 OK con el proveedor encontrado.
     * Si el proveedor no existe, GlobalExceptionHandler devuelve 404.
     */
    @GetMapping("/{idProveedor}")
    public ResponseEntity<ProveedorResponse> buscarPorId(@PathVariable Long idProveedor) {
        ProveedorResponse proveedor = proveedorService.buscarPorId(idProveedor);
        return ResponseEntity.ok(proveedor);
    }

    /**
     * Crea un nuevo proveedor.
     * Devuelve 201 CREATED con el proveedor creado.
     * Si los datos vienen mal, @Valid genera error 400.
     */
    @PostMapping
    public ResponseEntity<ProveedorResponse> crearProveedor(
            @Valid @RequestBody ProveedorCreateRequest request
    ) {
        ProveedorResponse proveedorCreado = proveedorService.crearProveedor(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(proveedorCreado);
    }

    /**
     * Actualiza un proveedor existente.
     * Devuelve 200 OK con el proveedor actualizado.
     * Si el proveedor no existe, GlobalExceptionHandler devuelve 404.
     * Si los datos vienen mal, @Valid genera error 400.
     */
    @PutMapping("/{idProveedor}")
    public ResponseEntity<ProveedorResponse> actualizarProveedor(
            @PathVariable Long idProveedor,
            @Valid @RequestBody ProveedorUpdateRequest request
    ) {
        ProveedorResponse proveedorActualizado = proveedorService.actualizarProveedor(idProveedor, request);
        return ResponseEntity.ok(proveedorActualizado);
    }

    /**
     * Elimina un proveedor de la base de datos.
     * Devuelve 204 NO CONTENT cuando elimina correctamente.
     * Si el proveedor no existe, GlobalExceptionHandler devuelve 404.
     */
    @DeleteMapping("/{idProveedor}")
    public ResponseEntity<Void> eliminarProveedor(@PathVariable Long idProveedor) {
        proveedorService.eliminarProveedor(idProveedor);
        return ResponseEntity.noContent().build();
    }

    /**
     * Busca proveedores por nombre.
     * Devuelve 200 OK con los proveedores que coinciden.
     */
    @GetMapping("/buscar")
    public ResponseEntity<List<ProveedorResponse>> buscarPorNombre(@RequestParam String nombre) {
        List<ProveedorResponse> proveedores = proveedorService.buscarPorNombre(nombre);
        return ResponseEntity.ok(proveedores);
    }
}
