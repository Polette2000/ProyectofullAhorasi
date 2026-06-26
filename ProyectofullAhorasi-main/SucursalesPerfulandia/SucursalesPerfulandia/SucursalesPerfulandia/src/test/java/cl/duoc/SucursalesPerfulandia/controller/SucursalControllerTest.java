package cl.duoc.SucursalesPerfulandia.controller;

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

import cl.duoc.SucursalesPerfulandia.dto.request.SucursalCreateRequest;
import cl.duoc.SucursalesPerfulandia.dto.request.SucursalUpdateRequest;
import cl.duoc.SucursalesPerfulandia.dto.response.ComunaResponse;
import cl.duoc.SucursalesPerfulandia.dto.response.SucursalResponse;
import cl.duoc.SucursalesPerfulandia.security.JwtAuthenticationFilter;
import cl.duoc.SucursalesPerfulandia.service.ComunaService;
import cl.duoc.SucursalesPerfulandia.service.SucursalService;

@WebMvcTest({ SucursalController.class, ComunaController.class })
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class SucursalControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JsonMapper jsonMapper;

    @MockitoBean
    private SucursalService sucursalService;

    @MockitoBean
    private ComunaService comunaService;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    private SucursalResponse crearSucursalResponse() {
        return new SucursalResponse(
                1,
                "Sucursal Centro",
                "Av. Principal 123",
                "+56912345678",
                "09:00 a 18:00",
                1,
                "Santiago",
                "Metropolitana");
    }

    private ComunaResponse crearComunaResponse() {
        ComunaResponse response = new ComunaResponse();
        response.setIdComuna(1);
        response.setNombreComuna("Santiago");
        response.setRegion("Metropolitana");
        return response;
    }

    @Test
    void listarSucursales_debeRetornar200CuandoTieneSucursales() throws Exception {
        when(sucursalService.listarSucursales()).thenReturn(List.of(crearSucursalResponse()));

        mockMvc.perform(get("/api/v1/sucursales"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idSucursal").value(1))
                .andExpect(jsonPath("$[0].nombreSucursal").value("Sucursal Centro"));
    }

    @Test
    void listarSucursales_debeRetornarMensajeCuandoEstaVacio() throws Exception {
        when(sucursalService.listarSucursales()).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/sucursales"))
                .andExpect(status().isOk())
                .andExpect(content().string("No hay sucursales registradas"));
    }

    @Test
    void buscarSucursalPorId_debeRetornar200CuandoExiste() throws Exception {
        when(sucursalService.buscarSucursalPorId(1)).thenReturn(crearSucursalResponse());

        mockMvc.perform(get("/api/v1/sucursales/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idSucursal").value(1))
                .andExpect(jsonPath("$.nombreComuna").value("Santiago"));
    }

    @Test
    void crearSucursal_debeRetornar200CuandoRequestEsValido() throws Exception {
        SucursalCreateRequest request = new SucursalCreateRequest(
                "Sucursal Centro",
                "Av. Principal 123",
                "+56912345678",
                "09:00 a 18:00",
                1);
        when(sucursalService.crearSucursal(any(SucursalCreateRequest.class))).thenReturn(crearSucursalResponse());

        mockMvc.perform(post("/api/v1/sucursales")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idSucursal").value(1));
    }

    @Test
    void crearSucursal_debeRetornar400CuandoNombreEstaVacio() throws Exception {
        SucursalCreateRequest request = new SucursalCreateRequest(
                "",
                "Av. Principal 123",
                "+56912345678",
                "09:00 a 18:00",
                1);

        mockMvc.perform(post("/api/v1/sucursales")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(sucursalService, never()).crearSucursal(any(SucursalCreateRequest.class));
    }

    @Test
    void actualizarSucursal_debeRetornar200CuandoRequestEsValido() throws Exception {
        SucursalUpdateRequest request = new SucursalUpdateRequest(
                "Sucursal Norte",
                "Calle Nueva 456",
                "+56987654321",
                "10:00 a 19:00",
                1);
        SucursalResponse response = crearSucursalResponse();
        response.setNombreSucursal("Sucursal Norte");

        when(sucursalService.actualizarSucursal(eq(1), any(SucursalUpdateRequest.class))).thenReturn(response);

        mockMvc.perform(put("/api/v1/sucursales/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombreSucursal").value("Sucursal Norte"));
    }

    @Test
    void eliminarSucursal_debeRetornar200CuandoExiste() throws Exception {
        mockMvc.perform(delete("/api/v1/sucursales/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Sucursal eliminada correctamente"));

        verify(sucursalService).eliminarSucursal(1);
    }

    @Test
    void listarComunas_debeRetornar200CuandoTieneComunas() throws Exception {
        when(comunaService.listarComunas()).thenReturn(List.of(crearComunaResponse()));

        mockMvc.perform(get("/api/v1/comunas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idComuna").value(1))
                .andExpect(jsonPath("$[0].nombreComuna").value("Santiago"));
    }
}
