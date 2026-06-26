package cl.duoc.Inventory.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import cl.duoc.Inventory.client.ProductoClient;
import cl.duoc.Inventory.client.SucursalClient;
import cl.duoc.Inventory.dto.request.InventoryCreateRequest;
import cl.duoc.Inventory.dto.request.InventoryUpdateRequest;
import cl.duoc.Inventory.dto.response.InventoryResponse;
import cl.duoc.Inventory.dto.response.ProductoResponse;
import cl.duoc.Inventory.exception.ResourceNotFoundException;
import cl.duoc.Inventory.model.Inventory;
import cl.duoc.Inventory.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
// Habilita logger SLF4J automaticamente
@Slf4j
public class InventoryService {

    private final InventoryRepository inventoryRepository;
    private final ProductoClient productoClient;
    private final SucursalClient sucursalClient;

    // Lista todos los inventarios.
    public List<InventoryResponse> listarInventario() {
        log.info("Iniciando busqueda de todos los inventarios");
        List<InventoryResponse> inventarios = inventoryRepository.findAll()
                .stream()
                .map(this::convertirAResponse)
                .toList();
        log.info("Cantidad de inventarios encontrados={}", inventarios.size());
        return inventarios;
    }

    // Crea un nuevo inventario.
    public InventoryResponse crearInventario(InventoryCreateRequest request) {
        log.info("Creando inventario para producto={} sucursal={}", request.getIdProducto(), request.getIdSucursal());
        productoClient.obtenerProductoPorId(request.getIdProducto());
        sucursalClient.obtenerSucursalPorId(request.getIdSucursal());

        if (inventoryRepository.existsByIdProductoAndIdSucursal(
                request.getIdProducto(),
                request.getIdSucursal())) {
            log.warn("Inventario duplicado para producto={} sucursal={}", request.getIdProducto(), request.getIdSucursal());
            throw new RuntimeException("Ya existe inventario para este producto en esta sucursal");
        }

        Inventory inventory = Inventory.builder()
                .idProducto(request.getIdProducto())
                .idSucursal(request.getIdSucursal())
                .stockDisponible(request.getStockDisponible())
                .fechaActualizacion(LocalDateTime.now())
                .build();

        Inventory inventoryGuardado = inventoryRepository.save(inventory);
        log.info("Inventario creado id={}", inventoryGuardado.getIdInventory());
        return convertirAResponse(inventoryGuardado);
    }

    // Actualiza el stock de un producto en una sucursal.
    public InventoryResponse actualizarInventario(InventoryUpdateRequest request) {
        log.info("Actualizando inventario producto={} sucursal={} stock={}",
                request.getIdProducto(), request.getIdSucursal(), request.getStockDisponible());
        productoClient.obtenerProductoPorId(request.getIdProducto());
        sucursalClient.obtenerSucursalPorId(request.getIdSucursal());

        Inventory inventory = obtenerInventoryPorProductoYSucursal(request.getIdProducto(), request.getIdSucursal());

        inventory.setStockDisponible(request.getStockDisponible());
        inventory.setFechaActualizacion(LocalDateTime.now());

        Inventory inventoryActualizado = inventoryRepository.save(inventory);
        log.info("Inventario actualizado id={}", inventoryActualizado.getIdInventory());
        return convertirAResponse(inventoryActualizado);
    }

    // Elimina un inventario de la base de datos.
    public void eliminarInventario(Long idProducto, Long idSucursal) {
        log.info("Eliminando inventario producto={} sucursal={}", idProducto, idSucursal);
        Inventory inventory = obtenerInventoryPorProductoYSucursal(idProducto, idSucursal);
        inventoryRepository.delete(inventory);
        log.info("Inventario eliminado id={}", inventory.getIdInventory());
    }

    // Busca inventarios por producto.
    public List<InventoryResponse> buscarPorProducto(Long idProducto) {
        log.info("Buscando inventarios por producto={}", idProducto);
        productoClient.obtenerProductoPorId(idProducto);

        List<InventoryResponse> inventarios = inventoryRepository.findByIdProducto(idProducto)
                .stream()
                .map(this::convertirAResponse)
                .toList();
        log.info("Inventarios encontrados para producto={} cantidad={}", idProducto, inventarios.size());
        return inventarios;
    }

    // Busca inventarios por sucursal.
    public List<InventoryResponse> buscarPorSucursal(Long idSucursal) {
        log.info("Buscando inventarios por sucursal={}", idSucursal);
        sucursalClient.obtenerSucursalPorId(idSucursal);

        List<InventoryResponse> inventarios = inventoryRepository.findByIdSucursal(idSucursal)
                .stream()
                .map(this::convertirAResponse)
                .toList();
        log.info("Inventarios encontrados para sucursal={} cantidad={}", idSucursal, inventarios.size());
        return inventarios;
    }

    // Busca un inventario por producto y sucursal.
    private Inventory obtenerInventoryPorProductoYSucursal(Long idProducto, Long idSucursal) {
        return inventoryRepository.findByIdProductoAndIdSucursal(idProducto, idSucursal)
                .orElseThrow(() -> {
                    log.warn("Inventario no encontrado producto={} sucursal={}", idProducto, idSucursal);
                    return new ResourceNotFoundException(
                            "Inventario no encontrado para producto: " + idProducto + " y sucursal: " + idSucursal);
                });
    }

    // Convierte la entidad Inventory en InventoryResponse.
    private InventoryResponse convertirAResponse(Inventory inventory) {
        String nombreProducto;
        try {
            ProductoResponse producto = productoClient.obtenerProductoPorId(inventory.getIdProducto());
            nombreProducto = producto.getNombre();
        } catch (RuntimeException ex) {
            log.warn("No se pudo obtener nombre del producto id={}", inventory.getIdProducto());
            nombreProducto = "Producto no encontrado";
        }

        return InventoryResponse.builder()
                .idInventory(inventory.getIdInventory())
                .idProducto(inventory.getIdProducto())
                .nombreProducto(nombreProducto)
                .idSucursal(inventory.getIdSucursal())
                .stockDisponible(inventory.getStockDisponible())
                .fechaActualizacion(inventory.getFechaActualizacion())
                .build();
    }
}