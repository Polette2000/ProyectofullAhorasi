package cl.duoc.Perfulandiabilling.Model;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Table(name = "pagos_facturacion")
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class PagoModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idPago;

    @Column(nullable = false)
    private Long idUsuario;

    @Column(nullable = false)
    private Long idOrden;

    @Column(nullable = false)
    private Double monto;

    @Column(nullable = false)
    private String metodo;

    @Column(nullable = false)
    private String estado;

    @Column(nullable = false)
    private LocalDateTime fechaRegistro;
}
