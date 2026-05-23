package cl.duoc.SucursalesPerfulandia.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "sucursales")
@Data
@NoArgsConstructor
@AllArgsConstructor

public class Sucursal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idSucursal;

    @Column(nullable = false, length = 100)
    private String nombreSucursal;

    @Column(nullable = false, length = 150)
    private String direccion;

    @Column(nullable = false, length = 20)
    private String telefono;

    @Column(nullable = false, length = 100)
    private String horarioAtencion;

    @ManyToOne
    @JoinColumn(name = "id_comuna", nullable = false)
    private Comuna comuna;

}
