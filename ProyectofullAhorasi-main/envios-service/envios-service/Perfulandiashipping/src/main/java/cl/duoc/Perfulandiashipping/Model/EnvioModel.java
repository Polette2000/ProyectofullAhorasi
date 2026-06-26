package cl.duoc.Perfulandiashipping.Model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "envios")
public class EnvioModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_envio")
    private Long idEnvio;

    @Column(name = "id_orden", nullable = false)
    private Long idOrden;

    @Column(name = "direccion", nullable = false, length = 200)
    private String direccion;

    @Column(name = "numero_seguimiento", length = 50)
    private String numeroSeguimiento;

    @Column(name = "estado", nullable = false, length = 20)
    private String estado; // PENDIENTE, EN_TRANSITO, ENTREGADO

    @Column(name = "fecha_estimada_inicio", nullable = false)
    private LocalDate fechaEstimadaInicio;

    @Column(name = "fecha_estimada_fin", nullable = false)
    private LocalDate fechaEstimadaFin;
}
