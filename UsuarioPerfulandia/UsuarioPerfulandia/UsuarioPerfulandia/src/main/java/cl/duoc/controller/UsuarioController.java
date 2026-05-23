package cl.duoc.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import cl.duoc.dto.request.LoginRequestDto;
import cl.duoc.dto.request.UsuarioCreateDto;
import cl.duoc.dto.request.UsuarioUpdateDto;
import cl.duoc.dto.response.UsuarioResponseDto;
import cl.duoc.dto.response.UsuarioValidacionResponseDto;
import cl.duoc.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;

    @GetMapping
    public ResponseEntity<List<UsuarioResponseDto>> listarUsuarios() {
        return ResponseEntity.ok(usuarioService.listarUsuarios());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponseDto> buscarUsuarioPorId(
            @PathVariable("id") Integer idUsuario) {
        return ResponseEntity.ok(usuarioService.buscarUsuarioPorId(idUsuario));
    }

    @PostMapping
    public ResponseEntity<UsuarioResponseDto> crearUsuario(
            @Valid @RequestBody UsuarioCreateDto usuarioCreateDto) {
        UsuarioResponseDto usuarioCreado = usuarioService.crearUsuario(usuarioCreateDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioCreado);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<UsuarioResponseDto> actualizarUsuario(
            @PathVariable("id") Integer idUsuario,
            @Valid @RequestBody UsuarioUpdateDto usuarioUpdateDto) {
        UsuarioResponseDto usuarioActualizado = usuarioService.actualizarUsuario(idUsuario, usuarioUpdateDto);
        return ResponseEntity.ok(usuarioActualizado);
    }

    @PostMapping("/validar-login")
    public ResponseEntity<UsuarioValidacionResponseDto> validarLogin(@RequestBody LoginRequestDto request) {
        return ResponseEntity.ok(usuarioService.validarLogin(request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminarUsuario(
            @PathVariable("id") Integer idUsuario) {
        usuarioService.eliminarUsuario(idUsuario);
        return ResponseEntity.ok("Usuario eliminado correctamente");
    }
}