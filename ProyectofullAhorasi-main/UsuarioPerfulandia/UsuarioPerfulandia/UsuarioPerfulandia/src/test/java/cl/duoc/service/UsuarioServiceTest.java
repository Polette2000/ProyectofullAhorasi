package cl.duoc.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import cl.duoc.dto.request.LoginRequestDto;
import cl.duoc.dto.request.UsuarioCreateDto;
import cl.duoc.dto.request.UsuarioUpdateDto;
import cl.duoc.dto.response.UsuarioResponseDto;
import cl.duoc.dto.response.UsuarioValidacionResponseDto;
import cl.duoc.model.Rol;
import cl.duoc.model.Usuario;
import cl.duoc.repository.RolRepository;
import cl.duoc.repository.UsuarioRepository;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private RolRepository rolRepository;

    @InjectMocks
    private UsuarioService usuarioService;

    private Rol rolCliente;
    private Usuario usuario;

    @BeforeEach
    void setUp() {
        rolCliente = new Rol(2, "CLIENTE");
        usuario = new Usuario(
                1,
                "Juan Perez",
                "juan@perfulandia.cl",
                LocalDate.of(1998, 4, 12),
                "secret123",
                rolCliente);
    }

    private UsuarioCreateDto crearRequest() {
        UsuarioCreateDto request = new UsuarioCreateDto();
        request.setNombre("Juan Perez");
        request.setEmail("juan@perfulandia.cl");
        request.setFechaNacimiento(LocalDate.of(1998, 4, 12));
        request.setContrasena("secret123");
        request.setIdRol(2);
        return request;
    }

    @Test
    void listarUsuarios_debeRetornarUsuariosRegistrados() {
        when(usuarioRepository.findAll()).thenReturn(List.of(usuario));

        List<UsuarioResponseDto> resultado = usuarioService.listarUsuarios();

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getEmail()).isEqualTo("juan@perfulandia.cl");
        assertThat(resultado.get(0).getNombreRol()).isEqualTo("CLIENTE");
    }

    @Test
    void crearUsuario_debeGuardarCuandoEmailNoExisteYRolExiste() {
        UsuarioCreateDto request = crearRequest();
        when(usuarioRepository.existsByEmail(request.getEmail())).thenReturn(false);
        when(rolRepository.findById(2)).thenReturn(Optional.of(rolCliente));
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);

        UsuarioResponseDto resultado = usuarioService.crearUsuario(request);

        assertThat(resultado.getIdUsuario()).isEqualTo(1);
        assertThat(resultado.getEmail()).isEqualTo("juan@perfulandia.cl");
        assertThat(resultado.getIdRol()).isEqualTo(2);
        verify(usuarioRepository).save(any(Usuario.class));
    }

    @Test
    void crearUsuario_debeLanzarErrorCuandoEmailYaExiste() {
        UsuarioCreateDto request = crearRequest();
        when(usuarioRepository.existsByEmail(request.getEmail())).thenReturn(true);

        assertThatThrownBy(() -> usuarioService.crearUsuario(request))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Ya existe un usuario");
    }

    @Test
    void actualizarUsuario_debeActualizarDatosCuandoUsuarioExiste() {
        UsuarioUpdateDto request = new UsuarioUpdateDto();
        request.setNombre("Juan Actualizado");
        request.setEmail("juan.nuevo@perfulandia.cl");
        request.setFechaNacimiento(LocalDate.of(1999, 5, 20));
        request.setContrasena("nuevo123");
        request.setIdRol(2);

        Usuario actualizado = new Usuario(
                1,
                "Juan Actualizado",
                "juan.nuevo@perfulandia.cl",
                LocalDate.of(1999, 5, 20),
                "nuevo123",
                rolCliente);

        when(usuarioRepository.findById(1)).thenReturn(Optional.of(usuario));
        when(usuarioRepository.existsByEmail("juan.nuevo@perfulandia.cl")).thenReturn(false);
        when(rolRepository.findById(2)).thenReturn(Optional.of(rolCliente));
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(actualizado);

        UsuarioResponseDto resultado = usuarioService.actualizarUsuario(1, request);

        assertThat(resultado.getNombre()).isEqualTo("Juan Actualizado");
        assertThat(resultado.getEmail()).isEqualTo("juan.nuevo@perfulandia.cl");
        assertThat(resultado.getNombreRol()).isEqualTo("CLIENTE");
    }

    @Test
    void validarLogin_debeRetornarValidoCuandoCredencialesSonCorrectas() {
        LoginRequestDto request = new LoginRequestDto();
        request.setEmail("juan@perfulandia.cl");
        request.setContrasena("secret123");
        when(usuarioRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(usuario));

        UsuarioValidacionResponseDto resultado = usuarioService.validarLogin(request);

        assertThat(resultado.getValido()).isTrue();
        assertThat(resultado.getEmail()).isEqualTo("juan@perfulandia.cl");
        assertThat(resultado.getRol()).isEqualTo("CLIENTE");
    }
}
