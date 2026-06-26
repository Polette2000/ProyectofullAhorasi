package cl.duoc.Perfulandiabilling.Model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Table(name = "facturas_facturacion")
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class FacturaModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idFactura;

    @Column(nullable = false)
    private Long idUsuario;

    @Column(nullable = false)
    private Long idPago;

    @Column(nullable = false)
    private Long idSucursal;

    @Column(nullable = false)
    private String nombreSucursal;

    @ElementCollection
    @CollectionTable(
            name = "factura_productos_facturacion",
            joinColumns = @JoinColumn(name = "id_factura")
    )
    @Builder.Default
    private List<FacturaProductoModel> productos = new ArrayList<>();

    @Column(nullable = false)
    private Integer cantidadTotal;

    @Column(nullable = false)
    private Double montoTotal;

    @Column(nullable = false)
    private String descripcion;

    @Column(nullable = false)
    private String estado;

    @Column(nullable = false)
    private LocalDateTime fechaEmision;
}
