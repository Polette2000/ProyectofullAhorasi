package cl.duoc.Inventory.model;

import java.time.LocalDateTime;

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

// Define la tabla de base de datos asociada a esta entidad
@Table(name = "inventory")

// Indica que esta clase es una entidad JPA
@Entity

// Permite construir objetos Inventory usando el patron Builder
@Builder

// Genera constructor vacio requerido por JPA
@NoArgsConstructor

// Genera constructor con todos los atributos
@AllArgsConstructor

// Genera getters, setters, equals, hashCode y toString
@Data
public class Inventory {

    // Identificador unico del registro de inventario
    @Id

    // Genera el ID automaticamente en la base de datos
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idInventory;

    // ID del producto asociado al inventario
    @Column(nullable = false)
    private Long idProducto;

    // ID de la sucursal donde se encuentra el producto
    @Column(nullable = false)
    private Long idSucursal;

    // Cantidad de stock disponible para el producto en la sucursal
    @Column(nullable = false)
    private Integer stockDisponible;

    // Fecha y hora de la ultima actualizacion del stock
    @Column(nullable = false)
    private LocalDateTime fechaActualizacion;
}
