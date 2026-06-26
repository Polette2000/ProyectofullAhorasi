package cl.duoc.PerfulandiaOrder.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import cl.duoc.PerfulandiaOrder.Model.OrderItem;
import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

  // Buscar ítems por ID de pedido
    List<OrderItem> findByPedidoIdPedido(Long idPedido);

    // Buscar ítems por producto
    List<OrderItem> findByIdProducto(Long idProducto);
}