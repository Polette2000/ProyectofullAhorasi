package cl.duoc.service;

import java.util.List;

import org.springframework.stereotype.Service;

import cl.duoc.dto.request.LoginRequestDto;
import cl.duoc.dto.request.UsuarioCreateDto;
import cl.duoc.dto.request.UsuarioUpdateDto;
import cl.duoc.dto.response.UsuarioResponseDto;
import cl.duoc.dto.response.UsuarioValidacionResponseDto;
import cl.duoc.exception.ResourceNotFoundException;
import cl.duoc.model.Rol;
import cl.duoc.model.Usuario;
import cl.duoc.repository.RolRepository;
import cl.duoc.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;

    public List<UsuarioResponseDto> listarUsuarios() {
        log.info("Listando usuarios registrados");
        List<UsuarioResponseDto> usuarios = usuarioRepository.findAll()
                .stream()
                .map(this::convertirAResponseDto)
                .toList();
        log.info("Cantidad de usuarios encontrados={}", usuarios.size());
        return usuarios;
    }

    public UsuarioResponseDto buscarUsuarioPorId(Integer idUsuario) {
        log.info("Buscando usuario por ID: {}", idUsuario);
        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + idUsuario));

        return convertirAResponseDto(usuario);
    }

    public UsuarioResponseDto crearUsuario(UsuarioCreateDto usuarioCreateDto) {
        log.info("Creando usuario con email={}", usuarioCreateDto.getEmail());

        if (usuarioRepository.existsByEmail(usuarioCreateDto.getEmail())) {
            log.warn("No se pudo crear usuario. Email duplicado={}", usuarioCreateDto.getEmail());
            throw new RuntimeException("Ya existe un usuario registrado con ese email");
        }
        Rol rol = rolRepository.findById(usuarioCreateDto.getIdRol())
                .orElseThrow(()-> new ResourceNotFoundException("Rol no encontrado con ID: " + usuarioCreateDto.getIdRol()));

        Usuario usuario = new Usuario();

        usuario.setNombre(usuarioCreateDto.getNombre());
        usuario.setEmail(usuarioCreateDto.getEmail());
        usuario.setFechaNacimiento(usuarioCreateDto.getFechaNacimiento());
        usuario.setContrasena(usuarioCreateDto.getContrasena());
        usuario.setRol(rol);
        

        Usuario usuarioGuardado = usuarioRepository.save(usuario);
        log.info("Usuario creado correctamente con ID: {}", usuarioGuardado.getIdUsuario());

        return convertirAResponseDto(usuarioGuardado);
    }

    public UsuarioResponseDto actualizarUsuario(Integer idUsuario, UsuarioUpdateDto usuarioUpdateDto) {
        log.info("Actualizando usuario ID: {}", idUsuario);

        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + idUsuario));

        if (usuarioUpdateDto.getNombre() != null) {
            usuario.setNombre(usuarioUpdateDto.getNombre());
        }

        if (usuarioUpdateDto.getEmail() != null) {

            if (!usuario.getEmail().equals(usuarioUpdateDto.getEmail())
                    && usuarioRepository.existsByEmail(usuarioUpdateDto.getEmail())) {
                log.warn("No se pudo actualizar usuario ID: {}. Email duplicado={}", idUsuario, usuarioUpdateDto.getEmail());
                throw new RuntimeException("Ya existe otro usuario registrado con ese email");
            }

            usuario.setEmail(usuarioUpdateDto.getEmail());
        }

        if (usuarioUpdateDto.getFechaNacimiento() != null) {
            usuario.setFechaNacimiento(usuarioUpdateDto.getFechaNacimiento());
        }

        if (usuarioUpdateDto.getContrasena() != null) {
            usuario.setContrasena(usuarioUpdateDto.getContrasena());
        }
        if (usuarioUpdateDto.getIdRol() != null) {
            Rol rol = rolRepository.findById(usuarioUpdateDto.getIdRol())
                    .orElseThrow(()-> new ResourceNotFoundException("Rol no encontrado con ID: " + usuarioUpdateDto.getIdRol()));

            usuario.setRol(rol);
            
        }

        Usuario usuarioActualizado = usuarioRepository.save(usuario);
        log.info("Usuario ID: {} actualizado correctamente", usuarioActualizado.getIdUsuario());

        return convertirAResponseDto(usuarioActualizado);
    }

    public void eliminarUsuario(Integer idUsuario) {
        log.info("Eliminando usuario ID: {}", idUsuario);

        if (!usuarioRepository.existsById(idUsuario)) {
            log.warn("Usuario no encontrado al eliminar. ID: {}", idUsuario);
            throw new ResourceNotFoundException("Usuario no encontrado con ID: " + idUsuario);
        }

        usuarioRepository.deleteById(idUsuario);
        log.info("Usuario ID: {} eliminado correctamente", idUsuario);
    }

    public UsuarioValidacionResponseDto validarLogin(LoginRequestDto request) {
        log.info("Validando login para email={}", request.getEmail());

        Usuario usuario = usuarioRepository.findByEmail(request.getEmail())
                .orElse(null);

        if (usuario == null) {
            log.warn("Login invalido. Usuario no encontrado email={}", request.getEmail());
            return new UsuarioValidacionResponseDto(false, null, null);
        }

        if (!usuario.getContrasena().equals(request.getContrasena())) {
            log.warn("Login invalido. Password incorrecta email={}", request.getEmail());
            return new UsuarioValidacionResponseDto(false, null, null);
        }

        log.info("Login valido para email={}", request.getEmail());
        return new UsuarioValidacionResponseDto(
                true,
                usuario.getEmail(),
                usuario.getRol().getNombreRol()
        );
    }

    private UsuarioResponseDto convertirAResponseDto(Usuario usuario) {

        UsuarioResponseDto responseDto = new UsuarioResponseDto();

        responseDto.setIdUsuario(usuario.getIdUsuario());
        responseDto.setNombre(usuario.getNombre());
        responseDto.setEmail(usuario.getEmail());
        responseDto.setFechaNacimiento(usuario.getFechaNacimiento());
        responseDto.setIdRol(usuario.getRol().getIdRol());
        responseDto.setNombreRol(usuario.getRol().getNombreRol());

        return responseDto;
    }
}
