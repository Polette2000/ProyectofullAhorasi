package cl.duoc.PerfulandiaOrder.Model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "items_pedido")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_item")
    private Long idItem;

    @Column(name = "id_producto", nullable = false)
    private Long idProducto;   // ID del producto

    @Column(nullable = false)
    private int cantidad;      // Cantidad pedida

    @Column(nullable = false)
    private double precio;     // Precio unitario

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_pedido", nullable = false)
    private OrderModel pedido; // Relación con el pedido
}
