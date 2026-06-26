package cl.duoc.TokenPerfulandia.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cl.duoc.TokenPerfulandia.dto.request.AuthRequestDto;
import cl.duoc.TokenPerfulandia.dto.response.AuthResponseDto;
import cl.duoc.TokenPerfulandia.service.TokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/api/token/v1")
@Tag(name = "Auth", description = "Autenticacion y generacion de JWT")

public class TokenController {
    private final TokenService tokenService;

    private static final String LOGIN_REQUEST_EXAMPLE = """
            {
              "email": "usuario@perfulandia.cl",
              "contrasena": "user123"
            }
            """;

    private static final String LOGIN_RESPONSE_EXAMPLE = """
            {
              "token": "eyJhbGciOiJIUzI1NiJ9.ejemplo-token-jwt",
              "email": "usuario@perfulandia.cl",
              "rol": "CLIENTE"
            }
            """;

    public TokenController(TokenService tokenService){
        this.tokenService = tokenService;

    }
    @PostMapping("/login")
    @Operation(summary = "Iniciar sesion", description = "Valida credenciales y devuelve un token JWT.")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Credenciales del usuario",
            content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(value = LOGIN_REQUEST_EXAMPLE)))
    @ApiResponse(responseCode = "200", description = "Login exitoso",
            content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(value = LOGIN_RESPONSE_EXAMPLE)))
    public ResponseEntity<AuthResponseDto> login (@Valid @RequestBody AuthRequestDto request ) {
        
        
        return  ResponseEntity.ok(tokenService.generarToken(request));
    }
    

}
