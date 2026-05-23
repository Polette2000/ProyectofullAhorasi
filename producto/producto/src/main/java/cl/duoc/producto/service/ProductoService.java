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

@Service
@RequiredArgsConstructor
public class ProductoService {

    private final ProductoRepository productoRepository;

    // Lista todos los productos.
    public List<ProductoResponse> listarProductos() {
        return productoRepository.findAll()
                .stream()
                .map(this::convertirAResponse)
                .toList();
    }

    // Busca un producto por su ID.
    public ProductoResponse buscarPorId(Long idProducto) {
        Producto producto = obtenerProductoPorId(idProducto);
        return convertirAResponse(producto);
    }

    // Crea un nuevo producto.
    public ProductoResponse crearProducto(ProductoCreateRequest request) {
        Producto producto = Producto.builder()
                .nombre(request.getNombre())
                .descripcion(request.getDescripcion())
                .precio(request.getPrecio())
                .build();

        Producto productoGuardado = productoRepository.save(producto);
        return convertirAResponse(productoGuardado);
    }

    // Actualiza un producto existente.
    public ProductoResponse actualizarProducto(Long idProducto, ProductoUpdateRequest request) {
        Producto producto = obtenerProductoPorId(idProducto);

        producto.setNombre(request.getNombre());
        producto.setDescripcion(request.getDescripcion());
        producto.setPrecio(request.getPrecio());

        Producto productoActualizado = productoRepository.save(producto);
        return convertirAResponse(productoActualizado);
    }

    // Elimina un producto de la base de datos.
    public void eliminarProducto(Long idProducto) {
        Producto producto = obtenerProductoPorId(idProducto);
        productoRepository.delete(producto);
    }

    // Busca productos por nombre.
    public List<ProductoResponse> buscarPorNombre(String nombre) {
        return productoRepository.findByNombreContainingIgnoreCase(nombre)
                .stream()
                .map(this::convertirAResponse)
                .toList();
    }

    private Producto obtenerProductoPorId(Long idProducto) {
        return productoRepository.findById(idProducto)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con ID: " + idProducto));
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
