package cl.duoc.TokenPerfulandia.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cl.duoc.TokenPerfulandia.dto.request.AuthRequestDto;
import cl.duoc.TokenPerfulandia.dto.response.AuthResponseDto;
import cl.duoc.TokenPerfulandia.service.TokenService;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/api/token/v1")

public class TokenController {
    private final TokenService tokenService;

    public TokenController(TokenService tokenService){
        this.tokenService = tokenService;

    }
    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> login (@Valid @RequestBody AuthRequestDto request ) {
        
        
        return  ResponseEntity.ok(tokenService.generarToken(request));
    }
    

}
