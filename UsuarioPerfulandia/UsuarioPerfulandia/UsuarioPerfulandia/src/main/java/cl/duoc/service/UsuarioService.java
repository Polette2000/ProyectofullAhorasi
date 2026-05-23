package cl.duoc.service;

import java.util.List;

import org.springframework.stereotype.Service;

import cl.duoc.dto.request.LoginRequestDto;
import cl.duoc.dto.request.UsuarioCreateDto;
import cl.duoc.dto.request.UsuarioUpdateDto;
import cl.duoc.dto.response.UsuarioResponseDto;
import cl.duoc.dto.response.UsuarioValidacionResponseDto;
import cl.duoc.model.Rol;
import cl.duoc.model.Usuario;
import cl.duoc.repository.RolRepository;
import cl.duoc.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;

    public List<UsuarioResponseDto> listarUsuarios() {
        return usuarioRepository.findAll()
                .stream()
                .map(this::convertirAResponseDto)
                .toList();
    }

    public UsuarioResponseDto buscarUsuarioPorId(Integer idUsuario) {
        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new RuntimeException("No existe un usuario con el ID: " + idUsuario));

        return convertirAResponseDto(usuario);
    }

    public UsuarioResponseDto crearUsuario(UsuarioCreateDto usuarioCreateDto) {

        if (usuarioRepository.existsByEmail(usuarioCreateDto.getEmail())) {
            throw new RuntimeException("Ya existe un usuario registrado con ese email");
        }
        Rol rol = rolRepository.findById(usuarioCreateDto.getIdRol())
                .orElseThrow(()-> new RuntimeException("No existe un rol con el ID: " + usuarioCreateDto.getIdRol()));

        Usuario usuario = new Usuario();

        usuario.setNombre(usuarioCreateDto.getNombre());
        usuario.setEmail(usuarioCreateDto.getEmail());
        usuario.setFechaNacimiento(usuarioCreateDto.getFechaNacimiento());
        usuario.setContrasena(usuarioCreateDto.getContrasena());
        usuario.setRol(rol);
        

        Usuario usuarioGuardado = usuarioRepository.save(usuario);

        return convertirAResponseDto(usuarioGuardado);
    }

    public UsuarioResponseDto actualizarUsuario(Integer idUsuario, UsuarioUpdateDto usuarioUpdateDto) {

        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new RuntimeException("No existe un usuario con el ID: " + idUsuario));

        if (usuarioUpdateDto.getNombre() != null) {
            usuario.setNombre(usuarioUpdateDto.getNombre());
        }

        if (usuarioUpdateDto.getEmail() != null) {

            if (!usuario.getEmail().equals(usuarioUpdateDto.getEmail())
                    && usuarioRepository.existsByEmail(usuarioUpdateDto.getEmail())) {
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
                    .orElseThrow(()-> new RuntimeException("No existe un rol con el ID:"+ usuarioUpdateDto.getIdRol()));

            usuario.setRol(rol);
            
        }

        Usuario usuarioActualizado = usuarioRepository.save(usuario);

        return convertirAResponseDto(usuarioActualizado);
    }

    public void eliminarUsuario(Integer idUsuario) {

        if (!usuarioRepository.existsById(idUsuario)) {
            throw new RuntimeException("No existe un usuario con el ID: " + idUsuario);
        }

        usuarioRepository.deleteById(idUsuario);
    }

    public UsuarioValidacionResponseDto validarLogin(LoginRequestDto request) {

    Usuario usuario = usuarioRepository.findByEmail(request.getEmail())
            .orElse(null);

    if (usuario == null) {
        return new UsuarioValidacionResponseDto(false, null, null);
    }

    if (!usuario.getContrasena().equals(request.getContrasena())) {
        return new UsuarioValidacionResponseDto(false, null, null);
    }

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