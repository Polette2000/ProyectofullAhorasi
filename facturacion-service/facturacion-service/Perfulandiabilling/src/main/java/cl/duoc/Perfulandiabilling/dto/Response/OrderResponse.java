package cl.duoc.Perfulandiabilling.dto.Response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderResponse {

    private Long idPedido;
    private Long idUsuario;
    private LocalDateTime fechaCreacion;
    private String estado;
    private List<OrderItemResponse> items;
}
