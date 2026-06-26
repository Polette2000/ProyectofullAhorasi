package cl.duoc.PerfulandiaOrder.Controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import tools.jackson.databind.json.JsonMapper;

import cl.duoc.PerfulandiaOrder.Security.JwtAuthenticationFilter;
import cl.duoc.PerfulandiaOrder.Service.OrderService;
import cl.duoc.PerfulandiaOrder.dto.Request.OrderCreateRequest;
import cl.duoc.PerfulandiaOrder.dto.Request.OrderItemCreateRequest;
import cl.duoc.PerfulandiaOrder.dto.Request.OrderItemUpdateRequest;
import cl.duoc.PerfulandiaOrder.dto.Request.OrderUpdateRequest;
import cl.duoc.PerfulandiaOrder.dto.Response.OrderItemResponse;
import cl.duoc.PerfulandiaOrder.dto.Response.OrderResponse;

@WebMvcTest(OrderController.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JsonMapper jsonMapper;

    @MockitoBean
    private OrderService orderService;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    private OrderResponse crearResponse() {
        return OrderResponse.builder()
                .idPedido(1L)
                .idUsuario(5L)
                .fechaCreacion(LocalDateTime.now())
                .estado("CREADO")
                .items(List.of(OrderItemResponse.builder()
                        .idItem(2L)
                        .idProducto(10L)
                        .nombreProducto("Perfume test")
                        .cantidad(2)
                        .precio(5000)
                        .build()))
                .build();
    }

    @Test
    void crearPedido_debeRetornar200CuandoRequestEsValido() throws Exception {
        OrderCreateRequest request = new OrderCreateRequest(List.of(new OrderItemCreateRequest(10L, 2)));
        when(orderService.crearPedido(eq(5L), any(OrderCreateRequest.class))).thenReturn(crearResponse());

        mockMvc.perform(post("/api/v1/pedidos/usuario/5")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idPedido").value(1))
                .andExpect(jsonPath("$.items[0].nombreProducto").value("Perfume test"));
    }

    @Test
    void crearPedido_debeRetornar400CuandoNoTieneItems() throws Exception {
        OrderCreateRequest request = new OrderCreateRequest(List.of());

        mockMvc.perform(post("/api/v1/pedidos/usuario/5")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(orderService, never()).crearPedido(any(Long.class), any(OrderCreateRequest.class));
    }

    @Test
    void obtenerPedidosPorUsuario_debeRetornar200CuandoTienePedidos() throws Exception {
        when(orderService.obtenerPedidosPorUsuario(5L)).thenReturn(List.of(crearResponse()));

        mockMvc.perform(get("/api/v1/pedidos/usuario/5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idPedido").value(1))
                .andExpect(jsonPath("$[0].idUsuario").value(5));
    }

    @Test
    void obtenerPedidosPorUsuario_debeRetornarMensajeCuandoEstaVacio() throws Exception {
        when(orderService.obtenerPedidosPorUsuario(5L)).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/pedidos/usuario/5"))
                .andExpect(status().isOk())
                .andExpect(content().string("El usuario no tiene pedidos creados"));
    }

    @Test
    void obtenerPedidoPorId_debeRetornar200CuandoExiste() throws Exception {
        when(orderService.obtenerPedidoPorId(1L)).thenReturn(crearResponse());

        mockMvc.perform(get("/api/v1/pedidos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idPedido").value(1))
                .andExpect(jsonPath("$.estado").value("CREADO"));
    }

    @Test
    void actualizarEstadoPedido_debeRetornar200CuandoRequestEsValido() throws Exception {
        OrderUpdateRequest request = new OrderUpdateRequest("PAGADO");
        OrderResponse response = crearResponse();
        response.setEstado("PAGADO");

        when(orderService.actualizarEstadoPedido(eq(1L), any(OrderUpdateRequest.class))).thenReturn(response);

        mockMvc.perform(put("/api/v1/pedidos/1/estado")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("PAGADO"));
    }

    @Test
    void actualizarEstadoPedido_debeRetornar400CuandoEstadoEstaVacio() throws Exception {
        OrderUpdateRequest request = new OrderUpdateRequest("");

        mockMvc.perform(put("/api/v1/pedidos/1/estado")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(orderService, never()).actualizarEstadoPedido(any(Long.class), any(OrderUpdateRequest.class));
    }

    @Test
    void actualizarItemPedido_debeRetornar200CuandoRequestEsValido() throws Exception {
        OrderItemUpdateRequest request = new OrderItemUpdateRequest(4);
        OrderResponse response = crearResponse();
        response.getItems().get(0).setCantidad(4);

        when(orderService.actualizarItemPedido(eq(1L), eq(2L), any(OrderItemUpdateRequest.class)))
                .thenReturn(response);

        mockMvc.perform(put("/api/v1/pedidos/1/items/2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items[0].cantidad").value(4));
    }

    @Test
    void actualizarItemPedido_debeRetornar400CuandoCantidadEsInvalida() throws Exception {
        OrderItemUpdateRequest request = new OrderItemUpdateRequest(0);

        mockMvc.perform(put("/api/v1/pedidos/1/items/2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(orderService, never())
                .actualizarItemPedido(any(Long.class), any(Long.class), any(OrderItemUpdateRequest.class));
    }

    @Test
    void eliminarPedido_debeRetornar200CuandoExiste() throws Exception {
        mockMvc.perform(delete("/api/v1/pedidos/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Pedido eliminado correctamente"));

        verify(orderService).eliminarPedido(1L);
    }
}
