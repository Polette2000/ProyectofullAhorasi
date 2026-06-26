package cl.duoc.VentasPerfulandia.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cl.duoc.VentasPerfulandia.model.VentaModel;

@Repository
public interface VentaRepository extends JpaRepository<VentaModel, Long> {

    List<VentaModel> findByIdUsuario(Long idUsuario);

    List<VentaModel> findByEstadoVenta(String estadoVenta);

    Optional<VentaModel> findByIdPedido(Long idPedido);

    boolean existsByIdPedido(Long idPedido);
}
