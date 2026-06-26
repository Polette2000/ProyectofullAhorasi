package cl.duoc.PerfulandiaOrder.Repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cl.duoc.PerfulandiaOrder.Model.OrderModel;

@Repository
public interface OrderRepository extends JpaRepository<OrderModel, Long> {
    // Buscar pedidos por usuario
// Buscar pedidos por usuario
    List<OrderModel> findByIdUsuario(Long idUsuario);

    // Buscar pedidos por estado
    List<OrderModel> findByEstado(String estado);

    // Buscar pedidos creados después de cierta fecha
    List<OrderModel> findByFechaCreacionAfter(LocalDateTime fecha);

    // Buscar pedidos por estado y usuario
    List<OrderModel> findByIdUsuarioAndEstado(Long idUsuario, String estado);
}