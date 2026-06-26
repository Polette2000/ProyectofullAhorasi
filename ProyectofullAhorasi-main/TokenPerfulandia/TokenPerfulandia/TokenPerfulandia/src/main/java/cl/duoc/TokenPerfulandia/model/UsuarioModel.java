package cl.duoc.TokenPerfulandia.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;

import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name ="usuario")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UsuarioModel {
   
    @Id
    @Column(name = "id_usuario")
    private Integer idUsuario;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "contrasena", nullable = false)
    private String contrasena;

    @Column(name = "rol", nullable = false)
    private String rol;

    @Column(name = "enabled", nullable = false)
    private Boolean enabled;
}


