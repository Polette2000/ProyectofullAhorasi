package cl.duoc.VentasPerfulandia.dto.response;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderResponse {

    private Long idPedido;
    private Long idUsuario;
    private LocalDateTime fechaCreacion;
    private String estado;
    private List<OrderItemResponse> items;
}
