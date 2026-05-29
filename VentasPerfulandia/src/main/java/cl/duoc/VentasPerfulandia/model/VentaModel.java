package cl.duoc.VentasPerfulandia.model;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Table(name = "ventas")
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class VentaModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_venta")
    private Long idVenta;

    @Column(name = "id_pedido", nullable = false, unique = true)
    private Long idPedido;

    @Column(name = "id_usuario", nullable = false)
    private Long idUsuario;

    @Column(name = "id_pago", nullable = false)
    private Long idPago;

    @Column(name = "id_factura", nullable = false)
    private Long idFactura;

    @Column(name = "total_venta", nullable = false)
    private Double totalVenta;

    @Column(name = "estado_venta", nullable = false, length = 30)
    private String estadoVenta;

    @Column(name = "canal_venta", nullable = false, length = 50)
    private String canalVenta;

    @Column(name = "fecha_venta", nullable = false)
    private LocalDateTime fechaVenta;

    @OneToMany(mappedBy = "venta", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<VentaDetalleModel> detalles;
}
