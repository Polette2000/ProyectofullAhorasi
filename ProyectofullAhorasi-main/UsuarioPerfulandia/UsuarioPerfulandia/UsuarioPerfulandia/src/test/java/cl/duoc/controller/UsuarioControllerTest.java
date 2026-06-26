package cl.duoc.controller;

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

import cl.duoc.dto.request.LoginRequestDto;
import cl.duoc.dto.request.UsuarioCreateDto;
import cl.duoc.dto.request.UsuarioUpdateDto;
import cl.duoc.dto.response.UsuarioResponseDto;
import cl.duoc.dto.response.UsuarioValidacionResponseDto;
import cl.duoc.security.JwtAuthenticationFilter;
import cl.duoc.service.UsuarioService;

@WebMvcTest(UsuarioController.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class UsuarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JsonMapper jsonMapper;

    @MockitoBean
    private UsuarioService usuarioService;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    private UsuarioResponseDto crearResponse() {
        return new UsuarioResponseDto(
                1,
                "Juan Perez",
                "juan@perfulandia.cl",
                LocalDate.of(1998, 4, 12),
                2,
                "CLIENTE");
    }

    private UsuarioCreateDto crearRequestValido() {
        UsuarioCreateDto request = new UsuarioCreateDto();
        request.setNombre("Juan Perez");
        request.setEmail("juan@perfulandia.cl");
        request.setFechaNacimiento(LocalDate.of(1998, 4, 12));
        request.setContrasena("secret123");
        request.setIdRol(2);
        return request;
    }

    @Test
    void listarUsuarios_debeRetornarUsuariosCuandoExisten() throws Exception {
        when(usuarioService.listarUsuarios()).thenReturn(List.of(crearResponse()));

        mockMvc.perform(get("/api/v1/usuarios"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idUsuario").value(1))
                .andExpect(jsonPath("$[0].email").value("juan@perfulandia.cl"))
                .andExpect(jsonPath("$[0].nombreRol").value("CLIENTE"));
    }

    @Test
    void listarUsuarios_debeRetornarMensajeCuandoNoExistenUsuarios() throws Exception {
        when(usuarioService.listarUsuarios()).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/usuarios"))
                .andExpect(status().isOk())
                .andExpect(content().string("No hay usuarios registrados"));
    }

    @Test
    void crearUsuario_debeRetornar201CuandoRequestEsValido() throws Exception {
        UsuarioCreateDto request = crearRequestValido();
        when(usuarioService.crearUsuario(any(UsuarioCreateDto.class))).thenReturn(crearResponse());

        mockMvc.perform(post("/api/v1/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idUsuario").value(1))
                .andExpect(jsonPath("$.email").value("juan@perfulandia.cl"));
    }

    @Test
    void crearUsuario_debeRetornar400CuandoEmailEsInvalido() throws Exception {
        UsuarioCreateDto request = crearRequestValido();
        request.setEmail("correo-invalido");

        mockMvc.perform(post("/api/v1/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(usuarioService, never()).crearUsuario(any(UsuarioCreateDto.class));
    }

    @Test
    void actualizarUsuario_debeRetornar200CuandoRequestEsValido() throws Exception {
        UsuarioUpdateDto request = new UsuarioUpdateDto();
        request.setNombre("Juan Actualizado");
        request.setEmail("juan.nuevo@perfulandia.cl");
        request.setFechaNacimiento(LocalDate.of(1999, 5, 20));
        request.setContrasena("nuevo123");
        request.setIdRol(2);

        UsuarioResponseDto response = crearResponse();
        response.setNombre("Juan Actualizado");
        response.setEmail("juan.nuevo@perfulandia.cl");

        when(usuarioService.actualizarUsuario(eq(1), any(UsuarioUpdateDto.class))).thenReturn(response);

        mockMvc.perform(patch("/api/v1/usuarios/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Juan Actualizado"))
                .andExpect(jsonPath("$.email").value("juan.nuevo@perfulandia.cl"));
    }

    @Test
    void validarLogin_debeRetornarResultadoDeValidacion() throws Exception {
        LoginRequestDto request = new LoginRequestDto();
        request.setEmail("juan@perfulandia.cl");
        request.setContrasena("secret123");

        when(usuarioService.validarLogin(any(LoginRequestDto.class)))
                .thenReturn(new UsuarioValidacionResponseDto(true, "juan@perfulandia.cl", "CLIENTE"));

        mockMvc.perform(post("/api/v1/usuarios/validar-login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valido").value(true))
                .andExpect(jsonPath("$.rol").value("CLIENTE"));
    }

    @Test
    void eliminarUsuario_debeRetornar200CuandoExiste() throws Exception {
        mockMvc.perform(delete("/api/v1/usuarios/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Usuario eliminado correctamente"));

        verify(usuarioService).eliminarUsuario(1);
    }
}
