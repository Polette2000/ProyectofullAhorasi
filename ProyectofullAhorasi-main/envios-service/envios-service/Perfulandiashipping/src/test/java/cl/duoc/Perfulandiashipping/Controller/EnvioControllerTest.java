package cl.duoc.Perfulandiashipping.Controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
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

import cl.duoc.Perfulandiashipping.Security.JwtAuthenticationFilter;
import cl.duoc.Perfulandiashipping.Service.EnvioService;
import cl.duoc.Perfulandiashipping.dto.Request.EnvioEstadoRequest;
import cl.duoc.Perfulandiashipping.dto.Request.EnvioRequest;
import cl.duoc.Perfulandiashipping.dto.Response.EnvioResponse;

@WebMvcTest(EnvioController.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class EnvioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JsonMapper jsonMapper;

    @MockitoBean
    private EnvioService envioService;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    private EnvioResponse crearResponse() {
        return new EnvioResponse(
                1L,
                10L,
                5L,
                "PAGADO",
                "Av. Principal 123",
                "SEG-001",
                "PENDIENTE",
                LocalDate.now().plusDays(1),
                LocalDate.now().plusDays(3));
    }

    private EnvioRequest crearRequest() {
        EnvioRequest request = new EnvioRequest();
        request.setIdOrden(10L);
        request.setDireccion("Av. Principal 123");
        request.setNumeroSeguimiento("SEG-001");
        request.setEstado("PENDIENTE");
        request.setFechaEstimadaInicio(LocalDate.now().plusDays(1));
        request.setFechaEstimadaFin(LocalDate.now().plusDays(3));
        return request;
    }

    @Test
    void obtenerEnvio_debeRetornar200CuandoExiste() throws Exception {
        when(envioService.obtenerEnvioPorId(1L)).thenReturn(crearResponse());

        mockMvc.perform(get("/api/v1/envios/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idEnvio").value(1))
                .andExpect(jsonPath("$.numeroSeguimiento").value("SEG-001"));
    }

    @Test
    void listarEnvios_debeRetornar200CuandoTieneEnvios() throws Exception {
        when(envioService.listarEnvios()).thenReturn(List.of(crearResponse()));

        mockMvc.perform(get("/api/v1/envios"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idEnvio").value(1))
                .andExpect(jsonPath("$[0].idOrden").value(10));
    }

    @Test
    void listarEnvios_debeRetornarMensajeCuandoEstaVacio() throws Exception {
        when(envioService.listarEnvios()).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/envios"))
                .andExpect(status().isOk())
                .andExpect(content().string("No hay envios registrados"));
    }

    @Test
    void crearEnvio_debeRetornar201CuandoRequestEsValido() throws Exception {
        EnvioRequest request = crearRequest();
        when(envioService.crearEnvio(any(EnvioRequest.class))).thenReturn(crearResponse());

        mockMvc.perform(post("/api/v1/envios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idEnvio").value(1))
                .andExpect(jsonPath("$.estado").value("PENDIENTE"));
    }

    @Test
    void crearEnvio_debeRetornar400CuandoDireccionEstaVacia() throws Exception {
        EnvioRequest request = crearRequest();
        request.setDireccion("");

        mockMvc.perform(post("/api/v1/envios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(envioService, never()).crearEnvio(any(EnvioRequest.class));
    }

    @Test
    void actualizarEstado_debeRetornar200CuandoRequestEsValido() throws Exception {
        EnvioEstadoRequest request = new EnvioEstadoRequest();
        request.setEstado("EN_TRANSITO");
        EnvioResponse response = crearResponse();
        response.setEstado("EN_TRANSITO");

        when(envioService.actualizarEstado(eq(1L), eq("EN_TRANSITO"))).thenReturn(response);

        mockMvc.perform(patch("/api/v1/envios/1/estado")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("EN_TRANSITO"));
    }

    @Test
    void actualizarEstado_debeRetornar400CuandoEstadoEstaVacio() throws Exception {
        EnvioEstadoRequest request = new EnvioEstadoRequest();
        request.setEstado("");

        mockMvc.perform(patch("/api/v1/envios/1/estado")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(envioService, never()).actualizarEstado(any(Long.class), any(String.class));
    }

    @Test
    void eliminarEnvio_debeRetornar200CuandoExiste() throws Exception {
        mockMvc.perform(delete("/api/v1/envios/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Envio eliminado correctamente"));

        verify(envioService).eliminarEnvio(1L);
    }

    @Test
    void buscarPorNumeroSeguimiento_debeRetornar200CuandoExiste() throws Exception {
        when(envioService.obtenerPorNumeroSeguimiento("SEG-001")).thenReturn(crearResponse());

        mockMvc.perform(get("/api/v1/envios/seguimiento/SEG-001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.numeroSeguimiento").value("SEG-001"));
    }
}
