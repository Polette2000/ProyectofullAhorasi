package cl.duoc.SucursalesPerfulandia.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cl.duoc.SucursalesPerfulandia.dto.request.SucursalCreateRequest;
import cl.duoc.SucursalesPerfulandia.dto.request.SucursalUpdateRequest;
import cl.duoc.SucursalesPerfulandia.dto.response.SucursalResponse;
import cl.duoc.SucursalesPerfulandia.service.SucursalService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/sucursales")
@RequiredArgsConstructor
public class SucursalController {

    private final SucursalService sucursalService;

    @GetMapping
    public List<SucursalResponse> listarSucursales() {
        return sucursalService.listarSucursales();
    }

    @GetMapping("/comuna/{idComuna}")
    public List<SucursalResponse> listarSucursalesPorComuna(@PathVariable Integer idComuna) {
        return sucursalService.listarSucursalesPorComuna(idComuna);
    }

    @GetMapping("/{idSucursal}")
    public SucursalResponse buscarSucursalPorId(@PathVariable Integer idSucursal) {
        return sucursalService.buscarSucursalPorId(idSucursal);
    }

    @PostMapping
    public SucursalResponse crearSucursal(@Valid @RequestBody SucursalCreateRequest request) {
        return sucursalService.crearSucursal(request);
    }

    @PutMapping("/{idSucursal}")
    public SucursalResponse actualizarSucursal(
            @PathVariable Integer idSucursal,
            @Valid @RequestBody SucursalUpdateRequest request) {
        return sucursalService.actualizarSucursal(idSucursal, request);
    }

    @DeleteMapping("/{idSucursal}")
    public String eliminarSucursal(@PathVariable Integer idSucursal) {
        sucursalService.eliminarSucursal(idSucursal);
        return "Sucursal eliminada correctamente";
    }

}
