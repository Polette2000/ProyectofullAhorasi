package cl.duoc.producto.service;

import java.util.List;

import org.springframework.stereotype.Service;

import cl.duoc.producto.dto.request.ProductoCreateRequest;
import cl.duoc.producto.dto.request.ProductoUpdateRequest;
import cl.duoc.producto.dto.response.ProductoResponse;
import cl.duoc.producto.exception.ResourceNotFoundException;
import cl.duoc.producto.model.Producto;
import cl.duoc.producto.repository.ProductoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
// Habilita logger SLF4J automaticamente
@Slf4j
public class ProductoService {

    private final ProductoRepository productoRepository;

    // Lista todos los productos.
    public List<ProductoResponse> listarProductos() {
        log.info("Iniciando busqueda de todos los productos");
        List<ProductoResponse> productos = productoRepository.findAll()
                .stream()
                .map(this::convertirAResponse)
                .toList();
        log.info("Cantidad de productos encontrados={}", productos.size());
        return productos;
    }

    // Busca un producto por su ID.
    public ProductoResponse buscarPorId(Long idProducto) {
        log.info("Buscando producto id={}", idProducto);
        Producto producto = obtenerProductoPorId(idProducto);
        return convertirAResponse(producto);
    }

    // Crea un nuevo producto.
    public ProductoResponse crearProducto(ProductoCreateRequest request) {
        log.info("Creando producto nombre={}", request.getNombre());
        Producto producto = Producto.builder()
                .nombre(request.getNombre())
                .descripcion(request.getDescripcion())
                .precio(request.getPrecio())
                .build();

        Producto productoGuardado = productoRepository.save(producto);
        log.info("Producto creado id={}", productoGuardado.getIdProducto());
        return convertirAResponse(productoGuardado);
    }

    // Actualiza un producto existente.
    public ProductoResponse actualizarProducto(Long idProducto, ProductoUpdateRequest request) {
        log.info("Actualizando producto id={}", idProducto);
        Producto producto = obtenerProductoPorId(idProducto);

        producto.setNombre(request.getNombre());
        producto.setDescripcion(request.getDescripcion());
        producto.setPrecio(request.getPrecio());

        Producto productoActualizado = productoRepository.save(producto);
        log.info("Producto actualizado id={}", productoActualizado.getIdProducto());
        return convertirAResponse(productoActualizado);
    }

    // Elimina un producto de la base de datos.
    public void eliminarProducto(Long idProducto) {
        log.info("Eliminando producto id={}", idProducto);
        Producto producto = obtenerProductoPorId(idProducto);
        productoRepository.delete(producto);
        log.info("Producto eliminado id={}", idProducto);
    }

    // Busca productos por nombre.
    public List<ProductoResponse> buscarPorNombre(String nombre) {
        log.info("Buscando productos por nombre={}", nombre);
        List<ProductoResponse> productos = productoRepository.findByNombreContainingIgnoreCase(nombre)
                .stream()
                .map(this::convertirAResponse)
                .toList();
        log.info("Productos encontrados por nombre={} cantidad={}", nombre, productos.size());
        return productos;
    }

    private Producto obtenerProductoPorId(Long idProducto) {
        return productoRepository.findById(idProducto)
                .orElseThrow(() -> {
                    log.warn("Producto no encontrado id={}", idProducto);
                    return new ResourceNotFoundException("Producto no encontrado con ID: " + idProducto);
                });
    }

    private ProductoResponse convertirAResponse(Producto producto) {
        return ProductoResponse.builder()
                .idProducto(producto.getIdProducto())
                .nombre(producto.getNombre())
                .descripcion(producto.getDescripcion())
                .precio(producto.getPrecio())
                .build();
    }
}