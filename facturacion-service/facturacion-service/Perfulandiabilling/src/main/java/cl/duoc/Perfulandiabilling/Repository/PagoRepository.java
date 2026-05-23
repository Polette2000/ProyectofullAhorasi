package cl.duoc.Perfulandiabilling.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cl.duoc.Perfulandiabilling.Model.PagoModel;

@Repository
public interface PagoRepository extends JpaRepository<PagoModel, Long> {

    // Buscar pagos por usuario
    List<PagoModel> findByIdUsuario(Long idUsuario);

    // Buscar pagos por orden
    List<PagoModel> findByIdOrden(Long idOrden);

    // Verificar si existe un pago por usuario y orden
    boolean existsByIdUsuarioAndIdOrden(Long idUsuario, Long idOrden);

    // Buscar un pago específico por usuario y orden
    Optional<PagoModel> findByIdUsuarioAndIdOrden(Long idUsuario, Long idOrden);
}
