package cl.duoc.PerfulandiaCart.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cl.duoc.PerfulandiaCart.model.ItemCarrito;

@Repository
public interface ItemCarritoRepository extends JpaRepository<ItemCarrito, Long> {

    List<ItemCarrito> findByIdUsuario(Long idUsuario);

    Optional<ItemCarrito> findByIdItemCarritoAndIdUsuario(Long idItemCarrito, Long idUsuario);

    Optional<ItemCarrito> findByIdUsuarioAndIdProducto(Long idUsuario, Long idProducto);

}
