package cl.duoc.PerfulandiaCart.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import cl.duoc.PerfulandiaCart.model.ItemCarrito;

@DataJpaTest
@ActiveProfiles("test")
class ItemCarritoRepositoryTest {

    @Autowired
    private ItemCarritoRepository itemCarritoRepository;

    private ItemCarrito guardarItem(Long idUsuario, Long idProducto, Integer cantidad) {
        ItemCarrito item = ItemCarrito.builder()
                .idUsuario(idUsuario)
                .idProducto(idProducto)
                .cantidad(cantidad)
                .build();
        return itemCarritoRepository.save(item);
    }

    @Test
    void findByIdUsuario_debeRetornarItemsDelUsuario() {
        guardarItem(7L, 20L, 3);
        guardarItem(7L, 21L, 1);
        guardarItem(8L, 30L, 5);

        List<ItemCarrito> resultado = itemCarritoRepository.findByIdUsuario(7L);

        assertThat(resultado).hasSize(2);
        assertThat(resultado).allMatch(item -> item.getIdUsuario().equals(7L));
    }

    @Test
    void findByIdUsuario_debeRetornarListaVaciaCuandoUsuarioNoTieneItems() {
        guardarItem(7L, 20L, 3);

        List<ItemCarrito> resultado = itemCarritoRepository.findByIdUsuario(99L);

        assertThat(resultado).isEmpty();
    }

    @Test
    void findByIdUsuarioAndIdProducto_debeRetornarItemCuandoExiste() {
        guardarItem(7L, 20L, 3);
        guardarItem(7L, 21L, 1);

        Optional<ItemCarrito> resultado = itemCarritoRepository.findByIdUsuarioAndIdProducto(7L, 20L);

        assertThat(resultado).isPresent();
        assertThat(resultado.get().getCantidad()).isEqualTo(3);
        assertThat(resultado.get().getIdProducto()).isEqualTo(20L);
    }

    @Test
    void findByIdUsuarioAndIdProducto_debeRetornarVacioCuandoNoExiste() {
        guardarItem(7L, 20L, 3);

        Optional<ItemCarrito> resultado = itemCarritoRepository.findByIdUsuarioAndIdProducto(7L, 99L);

        assertThat(resultado).isEmpty();
    }

    @Test
    void findByIdItemCarritoAndIdUsuario_debeRetornarItemDelUsuario() {
        ItemCarrito itemGuardado = guardarItem(7L, 20L, 3);

        Optional<ItemCarrito> resultado = itemCarritoRepository.findByIdItemCarritoAndIdUsuario(
                itemGuardado.getIdItemCarrito(),
                7L);

        assertThat(resultado).isPresent();
        assertThat(resultado.get().getIdItemCarrito()).isEqualTo(itemGuardado.getIdItemCarrito());
        assertThat(resultado.get().getIdUsuario()).isEqualTo(7L);
    }

    @Test
    void findByIdItemCarritoAndIdUsuario_debeRetornarVacioCuandoItemEsDeOtroUsuario() {
        ItemCarrito itemGuardado = guardarItem(7L, 20L, 3);

        Optional<ItemCarrito> resultado = itemCarritoRepository.findByIdItemCarritoAndIdUsuario(
                itemGuardado.getIdItemCarrito(),
                99L);

        assertThat(resultado).isEmpty();
    }

    @Test
    void save_debePersistirItemCarrito() {
        ItemCarrito resultado = guardarItem(7L, 20L, 3);

        assertThat(resultado.getIdItemCarrito()).isNotNull();
        assertThat(resultado.getIdProducto()).isEqualTo(20L);
        assertThat(resultado.getCantidad()).isEqualTo(3);
        assertThat(resultado.getIdUsuario()).isEqualTo(7L);
    }
}
