package cl.duoc.PerfulandiaOrder.Repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import cl.duoc.PerfulandiaOrder.Model.OrderItem;
import cl.duoc.PerfulandiaOrder.Model.OrderModel;

@DataJpaTest
@ActiveProfiles("test")
class OrderRepositoryTest {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    private OrderModel guardarPedido(Long idUsuario, String estado, LocalDateTime fecha) {
        OrderModel pedido = OrderModel.builder()
                .idUsuario(idUsuario)
                .fechaCreacion(fecha)
                .estado(estado)
                .items(List.of())
                .build();

        OrderItem item = OrderItem.builder()
                .idProducto(10L)
                .cantidad(2)
                .precio(5000)
                .pedido(pedido)
                .build();
        pedido.setItems(List.of(item));
        return orderRepository.save(pedido);
    }

    @Test
    void findByIdUsuario_debeRetornarPedidosDelUsuario() {
        guardarPedido(5L, "CREADO", LocalDateTime.now());
        guardarPedido(5L, "PAGADO", LocalDateTime.now());
        guardarPedido(6L, "CREADO", LocalDateTime.now());

        List<OrderModel> resultado = orderRepository.findByIdUsuario(5L);

        assertThat(resultado).hasSize(2);
        assertThat(resultado).allMatch(pedido -> pedido.getIdUsuario().equals(5L));
    }

    @Test
    void findByEstado_debeRetornarPedidosPorEstado() {
        guardarPedido(5L, "CREADO", LocalDateTime.now());
        guardarPedido(6L, "PAGADO", LocalDateTime.now());

        List<OrderModel> resultado = orderRepository.findByEstado("PAGADO");

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getIdUsuario()).isEqualTo(6L);
    }

    @Test
    void findByFechaCreacionAfter_debeRetornarPedidosPosterioresALaFecha() {
        LocalDateTime fechaBase = LocalDateTime.now();
        guardarPedido(5L, "CREADO", fechaBase.minusDays(2));
        guardarPedido(6L, "CREADO", fechaBase.plusDays(1));

        List<OrderModel> resultado = orderRepository.findByFechaCreacionAfter(fechaBase);

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getIdUsuario()).isEqualTo(6L);
    }

    @Test
    void findByIdUsuarioAndEstado_debeRetornarPedidosFiltrados() {
        guardarPedido(5L, "CREADO", LocalDateTime.now());
        guardarPedido(5L, "PAGADO", LocalDateTime.now());

        List<OrderModel> resultado = orderRepository.findByIdUsuarioAndEstado(5L, "PAGADO");

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getEstado()).isEqualTo("PAGADO");
    }

    @Test
    void orderItemRepository_findByPedidoIdPedido_debeRetornarItemsDelPedido() {
        OrderModel pedido = guardarPedido(5L, "CREADO", LocalDateTime.now());

        List<OrderItem> resultado = orderItemRepository.findByPedidoIdPedido(pedido.getIdPedido());

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getIdProducto()).isEqualTo(10L);
    }

    @Test
    void orderItemRepository_findByIdProducto_debeRetornarItemsDelProducto() {
        guardarPedido(5L, "CREADO", LocalDateTime.now());

        List<OrderItem> resultado = orderItemRepository.findByIdProducto(10L);

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getCantidad()).isEqualTo(2);
    }
}
