package cl.duoc.PerfulandiaCart.controller;

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

import cl.duoc.PerfulandiaCart.Security.JwtAuthenticationFilter;
import cl.duoc.PerfulandiaCart.dto.request.CarritoCreateRequest;
import cl.duoc.PerfulandiaCart.dto.request.CarritoUpdateRequest;
import cl.duoc.PerfulandiaCart.dto.response.ItemCarritoResponse;
import cl.duoc.PerfulandiaCart.service.CarritoService;

@WebMvcTest(CarritoController.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class CarritoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JsonMapper jsonMapper;

    @MockitoBean
    private CarritoService carritoService;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    private ItemCarritoResponse crearResponse() {
        return ItemCarritoResponse.builder()
                .idItemCarrito(1L)
                .idProducto(10L)
                .nombreProducto("Perfume test")
                .precioProducto(5000)
                .cantidad(2)
                .subtotal(10000)
                .idUsuario(5L)
                .build();
    }

    @Test
    void agregarItem_debeRetornar200CuandoRequestEsValido() throws Exception {
        CarritoCreateRequest request = new CarritoCreateRequest(10L, 2);
        when(carritoService.agregarItem(eq(5L), any(CarritoCreateRequest.class)))
                .thenReturn(crearResponse());

        mockMvc.perform(post("/api/v1/carrito/5")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idItemCarrito").value(1))
                .andExpect(jsonPath("$.nombreProducto").value("Perfume test"))
                .andExpect(jsonPath("$.subtotal").value(10000));
    }

    @Test
    void agregarItem_debeRetornar400CuandoCantidadEsInvalida() throws Exception {
        CarritoCreateRequest request = new CarritoCreateRequest(10L, 0);

        mockMvc.perform(post("/api/v1/carrito/5")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(carritoService, never()).agregarItem(any(Long.class), any(CarritoCreateRequest.class));
    }

    @Test
    void listarCarrito_debeRetornar200CuandoTieneItems() throws Exception {
        when(carritoService.listarCarrito(5L)).thenReturn(List.of(crearResponse()));

        mockMvc.perform(get("/api/v1/carrito/5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idItemCarrito").value(1))
                .andExpect(jsonPath("$[0].idUsuario").value(5));
    }

    @Test
    void listarCarrito_debeRetornarMensajeCuandoEstaVacio() throws Exception {
        when(carritoService.listarCarrito(5L)).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/carrito/5"))
                .andExpect(status().isOk())
                .andExpect(content().string("El usuario no tiene productos en el carrito"));
    }

    @Test
    void actualizarItem_debeRetornar200CuandoRequestEsValido() throws Exception {
        CarritoUpdateRequest request = new CarritoUpdateRequest(4);
        ItemCarritoResponse response = crearResponse();
        response.setCantidad(4);
        response.setSubtotal(20000);

        when(carritoService.actualizarItem(eq(5L), eq(1L), any(CarritoUpdateRequest.class)))
                .thenReturn(response);

        mockMvc.perform(put("/api/v1/carrito/5/items/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cantidad").value(4))
                .andExpect(jsonPath("$.subtotal").value(20000));
    }

    @Test
    void actualizarItem_debeRetornar400CuandoCantidadEsInvalida() throws Exception {
        CarritoUpdateRequest request = new CarritoUpdateRequest(0);

        mockMvc.perform(put("/api/v1/carrito/5/items/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(carritoService, never())
                .actualizarItem(any(Long.class), any(Long.class), any(CarritoUpdateRequest.class));
    }

    @Test
    void eliminarItem_debeRetornar200CuandoExiste() throws Exception {
        mockMvc.perform(delete("/api/v1/carrito/5/items/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Item eliminado correctamente"));

        verify(carritoService).eliminarItem(5L, 1L);
    }
}
