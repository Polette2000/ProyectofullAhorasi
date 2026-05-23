package cl.duoc.SucursalesPerfulandia.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cl.duoc.SucursalesPerfulandia.dto.response.ComunaResponse;
import cl.duoc.SucursalesPerfulandia.service.ComunaService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/comunas")
@RequiredArgsConstructor
public class ComunaController {

    private final ComunaService comunaService;

    // Lista las comunas disponibles para saber que idComuna usar en sucursales.
    @GetMapping
    public List<ComunaResponse> listarComunas() {
        return comunaService.listarComunas();
    }

}
