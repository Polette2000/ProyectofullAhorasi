package cl.duoc.Perfulandiashipping.Repository;

import cl.duoc.Perfulandiashipping.Model.EnvioModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EnvioRepository extends JpaRepository<EnvioModel, Long> {

    // Buscar por número de seguimiento
    Optional<EnvioModel> findByNumeroSeguimiento(String numeroSeguimiento);

    // Verificar si ya existe un envío para una orden
    boolean existsByIdOrden(Long idOrden);
}
