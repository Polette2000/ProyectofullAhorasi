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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/usuarios")
@RequiredArgsConstructor
@Tag(name = "Usuarios", description = "Gestion y validacion de usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    private static final String USUARIO_RESPONSE_EXAMPLE = """
            {
              "idUsuario": 2,
              "nombre": "Usuario Cliente",
              "email": "usuario@perfulandia.cl",
              "fechaNacimiento": "1995-05-10",
              "idRol": 2,
              "nombreRol": "CLIENTE"
            }
            """;

    private static final String USUARIOS_LIST_RESPONSE_EXAMPLE = """
            [
              {
                "idUsuario": 2,
                "nombre": "Usuario Cliente",
                "email": "usuario@perfulandia.cl",
                "fechaNacimiento": "1995-05-10",
                "idRol": 2,
                "nombreRol": "CLIENTE"
              }
            ]
            """;

    private static final String USUARIO_CREATE_REQUEST_EXAMPLE = """
            {
              "nombre": "Juan Perez",
              "email": "juan@perfulandia.cl",
              "fechaNacimiento": "1998-04-12",
              "contrasena": "secret123",
              "idRol": 2
            }
            """;

    private static final String USUARIO_UPDATE_REQUEST_EXAMPLE = """
            {
              "nombre": "Juan Perez Actualizado",
              "email": "juan.actualizado@perfulandia.cl",
              "fechaNacimiento": "1998-04-12",
              "contrasena": "nuevo123",
              "idRol": 2
            }
            """;

    private static final String LOGIN_REQUEST_EXAMPLE = """
            {
              "email": "usuario@perfulandia.cl",
              "contrasena": "user123"
            }
            """;

    private static final String LOGIN_RESPONSE_EXAMPLE = """
            {
              "valido": true,
              "email": "usuario@perfulandia.cl",
              "rol": "CLIENTE"
            }
            """;

    @GetMapping
    @Operation(summary = "Listar usuarios", description = "Obtiene todos los usuarios registrados.")
    @ApiResponse(responseCode = "200", description = "Operacion exitosa",
            content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(value = USUARIOS_LIST_RESPONSE_EXAMPLE)))
    public ResponseEntity<?> listarUsuarios() {
        List<UsuarioResponseDto> usuarios = usuarioService.listarUsuarios();
        if (usuarios.isEmpty()) {
            return ResponseEntity.ok("No hay usuarios registrados");
        }
        return ResponseEntity.ok(usuarios);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar usuario por ID", description = "Obtiene los datos de un usuario especifico.")
    @ApiResponse(responseCode = "200", description = "Usuario encontrado",
            content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(value = USUARIO_RESPONSE_EXAMPLE)))
    public ResponseEntity<UsuarioResponseDto> buscarUsuarioPorId(
            @PathVariable("id") Integer idUsuario) {
        return ResponseEntity.ok(usuarioService.buscarUsuarioPorId(idUsuario));
    }

    @PostMapping
    @Operation(summary = "Crear usuario", description = "Registra un nuevo usuario con su rol.")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Datos necesarios para crear un usuario",
            content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(value = USUARIO_CREATE_REQUEST_EXAMPLE)))
    @ApiResponse(responseCode = "201", description = "Usuario creado",
            content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(value = USUARIO_RESPONSE_EXAMPLE)))
    public ResponseEntity<UsuarioResponseDto> crearUsuario(
            @Valid @RequestBody UsuarioCreateDto usuarioCreateDto) {
        UsuarioResponseDto usuarioCreado = usuarioService.crearUsuario(usuarioCreateDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioCreado);
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Actualizar usuario", description = "Actualiza datos editables de un usuario existente.")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Datos que se actualizaran del usuario",
            content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(value = USUARIO_UPDATE_REQUEST_EXAMPLE)))
    @ApiResponse(responseCode = "200", description = "Usuario actualizado",
            content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(value = USUARIO_RESPONSE_EXAMPLE)))
    public ResponseEntity<UsuarioResponseDto> actualizarUsuario(
            @PathVariable("id") Integer idUsuario,
            @Valid @RequestBody UsuarioUpdateDto usuarioUpdateDto) {
        UsuarioResponseDto usuarioActualizado = usuarioService.actualizarUsuario(idUsuario, usuarioUpdateDto);
        return ResponseEntity.ok(usuarioActualizado);
    }

    @PostMapping("/validar-login")
    @Operation(summary = "Validar login", description = "Verifica email y contrasena para que Auth pueda generar JWT.")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Credenciales del usuario",
            content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(value = LOGIN_REQUEST_EXAMPLE)))
    @ApiResponse(responseCode = "200", description = "Resultado de validacion",
            content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(value = LOGIN_RESPONSE_EXAMPLE)))
    public ResponseEntity<UsuarioValidacionResponseDto> validarLogin(@Valid @RequestBody LoginRequestDto request) {
        return ResponseEntity.ok(usuarioService.validarLogin(request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar usuario", description = "Elimina un usuario por ID.")
    @ApiResponse(responseCode = "200", description = "Usuario eliminado",
            content = @Content(mediaType = "text/plain",
                    examples = @ExampleObject(value = "Usuario eliminado correctamente")))
    public ResponseEntity<String> eliminarUsuario(
            @PathVariable("id") Integer idUsuario) {
        usuarioService.eliminarUsuario(idUsuario);
        return ResponseEntity.ok("Usuario eliminado correctamente");
    }
}
