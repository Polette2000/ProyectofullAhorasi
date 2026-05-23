package cl.duoc.PerfulandiaCart.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Builder
@Table(name = "carrito_items")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemCarrito {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_item")
    private Long idItemCarrito;

    @Column(name = "id_producto", nullable = false)
    private Long idProducto;

    @Column(nullable = false)
    private Integer cantidad;

    @Column(name = "id_usuario", nullable = false)
    private Long idUsuario;

}
