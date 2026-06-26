package cl.duoc.SucursalesPerfulandia.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;

import lombok.*;

@Entity
@Table(name = "comunas")
@Data
@NoArgsConstructor
@AllArgsConstructor


public class Comuna {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idComuna;

    @Column(nullable = false, length = 100)
    private String nombreComuna;

    @Column(nullable = false, length = 100)
    private String region;

    @OneToMany(mappedBy = "comuna", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Sucursal> sucursales;

}
