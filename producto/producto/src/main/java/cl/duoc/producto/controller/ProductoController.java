package cl.duoc.producto.controller;

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

import cl.duoc.producto.dto.request.ProductoCreateRequest;
import cl.duoc.producto.dto.request.ProductoUpdateRequest;
import cl.duoc.producto.dto.response.ProductoResponse;
import cl.duoc.producto.service.ProductoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/productos")
@RequiredArgsConstructor
public class ProductoController {

    private final ProductoService productoService;

    /**
     * Lista todos los productos registrados.
     * Devuelve 200 OK con la lista de productos.
     */
    @GetMapping
    public ResponseEntity<List<ProductoResponse>> listarProductos() {
        List<ProductoResponse> productos = productoService.listarProductos();
        return ResponseEntity.ok(productos);
    }

    /**
     * Busca un producto por su ID.
     * Devuelve 200 OK con el producto encontrado.
     * Si el producto no existe, GlobalExceptionHandler devuelve 404.
     */
    @GetMapping("/{idProducto}")
    public ResponseEntity<ProductoResponse> buscarPorId(@PathVariable Long idProducto) {
        ProductoResponse producto = productoService.buscarPorId(idProducto);
        return ResponseEntity.ok(producto);
    }

    /**
     * Crea un nuevo producto.
     * Devuelve 201 CREATED con el producto creado.
     * Si los datos vienen mal, @Valid genera error 400.
     */
    @PostMapping
    public ResponseEntity<ProductoResponse> crearProducto(
            @Valid @RequestBody ProductoCreateRequest request
    ) {
        ProductoResponse productoCreado = productoService.crearProducto(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(productoCreado);
    }

    /**
     * Actualiza un producto existente.
     * Devuelve 200 OK con el producto actualizado.
     * Si el producto no existe, GlobalExceptionHandler devuelve 404.
     * Si los datos vienen mal, @Valid genera error 400.
     */
    @PutMapping("/{idProducto}")
    public ResponseEntity<ProductoResponse> actualizarProducto(
            @PathVariable Long idProducto,
            @Valid @RequestBody ProductoUpdateRequest request
    ) {
        ProductoResponse productoActualizado = productoService.actualizarProducto(idProducto, request);
        return ResponseEntity.ok(productoActualizado);
    }

    /**
     * Elimina un producto de la base de datos.
     * Devuelve 204 NO CONTENT cuando elimina correctamente.
     * Si el producto no existe, GlobalExceptionHandler devuelve 404.
     */
    @DeleteMapping("/{idProducto}")
    public ResponseEntity<Void> eliminarProducto(@PathVariable Long idProducto) {
        productoService.eliminarProducto(idProducto);
        return ResponseEntity.noContent().build();
    }

    /**
     * Busca productos por nombre.
     * Devuelve 200 OK con los productos que coinciden.
     */
    @GetMapping("/buscar")
    public ResponseEntity<List<ProductoResponse>> buscarPorNombre(@RequestParam String nombre) {
        List<ProductoResponse> productos = productoService.buscarPorNombre(nombre);
        return ResponseEntity.ok(productos);
    }
}
