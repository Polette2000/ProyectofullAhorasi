package cl.duoc.Perfulandiabilling.Controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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

import cl.duoc.Perfulandiabilling.Security.JwtAuthenticationFilter;
import cl.duoc.Perfulandiabilling.Service.FacturacionService;
import cl.duoc.Perfulandiabilling.dto.Request.FacturaCreateRequest;
import cl.duoc.Perfulandiabilling.dto.Request.PagoCreateRequest;
import cl.duoc.Perfulandiabilling.dto.Response.FacturaResponse;
import cl.duoc.Perfulandiabilling.dto.Response.FacturaProductoResponse;
import cl.duoc.Perfulandiabilling.dto.Response.PagoResponse;

@WebMvcTest(FacturacionController.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class FacturacionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JsonMapper jsonMapper;

    @MockitoBean
    private FacturacionService facturacionService;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    private PagoResponse crearPagoResponse() {
        return new PagoResponse(1L, 5L, 10L, 25000.0, "TARJETA", "COMPLETADO");
    }

    private FacturaResponse crearFacturaResponse() {
        return new FacturaResponse(
                1L,
                5L,
                2L,
                "Sucursal Centro",
                1L,
                List.of(new FacturaProductoResponse(100L, "Perfume Floral", 2, 12500.0, 25000.0)),
                2,
                25000.0,
                "Compra de perfumes",
                "EMITIDA",
                LocalDateTime.now()
        );
    }

    @Test
    void listarPagos_debeRetornar200CuandoTienePagos() throws Exception {
        when(facturacionService.listarPagos()).thenReturn(List.of(crearPagoResponse()));

        mockMvc.perform(get("/api/v1/billing/pagos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idPago").value(1))
                .andExpect(jsonPath("$[0].estado").value("COMPLETADO"));
    }

    @Test
    void listarPagos_debeRetornarMensajeCuandoEstaVacio() throws Exception {
        when(facturacionService.listarPagos()).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/billing/pagos"))
                .andExpect(status().isOk())
                .andExpect(content().string("No hay pagos registrados"));
    }

    @Test
    void registrarPago_debeRetornar201CuandoRequestEsValido() throws Exception {
        PagoCreateRequest request = new PagoCreateRequest(5L, 10L, 25000.0, "TARJETA");
        when(facturacionService.registrarPago(any(PagoCreateRequest.class))).thenReturn(crearPagoResponse());

        mockMvc.perform(post("/api/v1/billing/pagos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idPago").value(1))
                .andExpect(jsonPath("$.metodo").value("TARJETA"));
    }

    @Test
    void registrarPago_debeRetornar400CuandoMontoEsInvalido() throws Exception {
        PagoCreateRequest request = new PagoCreateRequest(5L, 10L, 0.0, "TARJETA");

        mockMvc.perform(post("/api/v1/billing/pagos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(facturacionService, never()).registrarPago(any(PagoCreateRequest.class));
    }

    @Test
    void obtenerPago_debeRetornar200CuandoExiste() throws Exception {
        when(facturacionService.obtenerPagoPorId(1L)).thenReturn(crearPagoResponse());

        mockMvc.perform(get("/api/v1/billing/pagos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idPago").value(1))
                .andExpect(jsonPath("$.idOrden").value(10));
    }

    @Test
    void eliminarPago_debeRetornar200CuandoExiste() throws Exception {
        mockMvc.perform(delete("/api/v1/billing/pagos/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Pago eliminado correctamente"));

        verify(facturacionService).eliminarPago(1L);
    }

    @Test
    void listarFacturas_debeRetornar200CuandoTieneFacturas() throws Exception {
        when(facturacionService.listarFacturas()).thenReturn(List.of(crearFacturaResponse()));

        mockMvc.perform(get("/api/v1/billing/facturas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idFactura").value(1))
                .andExpect(jsonPath("$[0].estado").value("EMITIDA"));
    }

    @Test
    void listarFacturas_debeRetornarMensajeCuandoEstaVacio() throws Exception {
        when(facturacionService.listarFacturas()).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/billing/facturas"))
                .andExpect(status().isOk())
                .andExpect(content().string("No hay facturas registradas"));
    }

    @Test
    void generarFactura_debeRetornar201CuandoRequestEsValido() throws Exception {
        FacturaCreateRequest request = new FacturaCreateRequest(1L, 2L, "Compra de perfumes");
        when(facturacionService.generarFactura(any(FacturaCreateRequest.class))).thenReturn(crearFacturaResponse());

        mockMvc.perform(post("/api/v1/billing/facturas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idFactura").value(1))
                .andExpect(jsonPath("$.idUsuario").value(5))
                .andExpect(jsonPath("$.idSucursal").value(2))
                .andExpect(jsonPath("$.productos[0].nombreProducto").value("Perfume Floral"))
                .andExpect(jsonPath("$.cantidadTotal").value(2))
                .andExpect(jsonPath("$.descripcion").value("Compra de perfumes"));
    }

    @Test
    void generarFactura_debeRetornar400CuandoDescripcionEstaVacia() throws Exception {
        FacturaCreateRequest request = new FacturaCreateRequest(1L, 2L, "");

        mockMvc.perform(post("/api/v1/billing/facturas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(facturacionService, never()).generarFactura(any(FacturaCreateRequest.class));
    }

    @Test
    void obtenerFactura_debeRetornar200CuandoExiste() throws Exception {
        when(facturacionService.obtenerFacturaPorId(1L)).thenReturn(crearFacturaResponse());

        mockMvc.perform(get("/api/v1/billing/facturas/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idFactura").value(1))
                .andExpect(jsonPath("$.idPago").value(1));
    }

    @Test
    void eliminarFactura_debeRetornar200CuandoExiste() throws Exception {
        mockMvc.perform(delete("/api/v1/billing/facturas/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Factura eliminada correctamente"));

        verify(facturacionService).eliminarFactura(1L);
    }
}
