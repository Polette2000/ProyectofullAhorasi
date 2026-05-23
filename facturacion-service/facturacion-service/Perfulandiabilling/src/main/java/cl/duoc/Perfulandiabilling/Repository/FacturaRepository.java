package cl.duoc.Perfulandiabilling.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cl.duoc.Perfulandiabilling.Model.FacturaModel;

@Repository
public interface FacturaRepository extends JpaRepository<FacturaModel, Long> {
    // Buscar facturas por usuario
    List<FacturaModel> findByIdUsuario(Long idUsuario);

    // Buscar facturas por pago
    List<FacturaModel> findByIdPago(Long idPago);

    // Verificar si existe una factura por usuario y pago
    boolean existsByIdUsuarioAndIdPago(Long idUsuario, Long idPago);

    // Buscar una factura específica por usuario y pago
    Optional<FacturaModel> findByIdUsuarioAndIdPago(Long idUsuario, Long idPago);
}
