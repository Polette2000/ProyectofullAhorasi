package cl.duoc.Inventory.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cl.duoc.Inventory.model.Inventory;

// Indica que esta interfaz pertenece a la capa de acceso a datos
@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long> {

    // Busca todos los registros de inventario asociados a un producto
    List<Inventory> findByIdProducto(Long idProducto);

    // Busca todos los registros de inventario asociados a una sucursal
    List<Inventory> findByIdSucursal(Long idSucursal);

    // Verifica si ya existe inventario para el producto y la sucursal indicados
    boolean existsByIdProductoAndIdSucursal(Long idProducto, Long idSucursal);

    // Permite buscar, actualizar o eliminar inventario usando producto y sucursal
    Optional<Inventory> findByIdProductoAndIdSucursal(Long idProducto, Long idSucursal);
}
