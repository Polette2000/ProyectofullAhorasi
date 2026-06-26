package cl.duoc.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import cl.duoc.model.Rol;
import cl.duoc.model.Usuario;

@DataJpaTest
@ActiveProfiles("test")
class UsuarioRepositoryTest {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private RolRepository rolRepository;

    private Usuario guardarUsuario(String email) {
        Rol rol = rolRepository.save(new Rol(null, "CLIENTE"));
        Usuario usuario = new Usuario(
                null,
                "Juan Perez",
                email,
                LocalDate.of(1998, 4, 12),
                "secret123",
                rol);
        return usuarioRepository.save(usuario);
    }

    @Test
    void save_debePersistirUsuarioConRol() {
        Usuario resultado = guardarUsuario("juan@perfulandia.cl");

        assertThat(resultado.getIdUsuario()).isNotNull();
        assertThat(resultado.getRol().getIdRol()).isNotNull();
        assertThat(resultado.getRol().getNombreRol()).isEqualTo("CLIENTE");
    }

    @Test
    void findByEmail_debeRetornarUsuarioCuandoExiste() {
        guardarUsuario("juan@perfulandia.cl");

        Optional<Usuario> resultado = usuarioRepository.findByEmail("juan@perfulandia.cl");

        assertThat(resultado).isPresent();
        assertThat(resultado.get().getNombre()).isEqualTo("Juan Perez");
    }

    @Test
    void findByEmail_debeRetornarVacioCuandoNoExiste() {
        guardarUsuario("juan@perfulandia.cl");

        Optional<Usuario> resultado = usuarioRepository.findByEmail("noexiste@perfulandia.cl");

        assertThat(resultado).isEmpty();
    }

    @Test
    void existsByEmail_debeRetornarTrueCuandoEmailExiste() {
        guardarUsuario("juan@perfulandia.cl");

        boolean resultado = usuarioRepository.existsByEmail("juan@perfulandia.cl");

        assertThat(resultado).isTrue();
    }

    @Test
    void existsByEmail_debeRetornarFalseCuandoEmailNoExiste() {
        guardarUsuario("juan@perfulandia.cl");

        boolean resultado = usuarioRepository.existsByEmail("otro@perfulandia.cl");

        assertThat(resultado).isFalse();
    }
}
