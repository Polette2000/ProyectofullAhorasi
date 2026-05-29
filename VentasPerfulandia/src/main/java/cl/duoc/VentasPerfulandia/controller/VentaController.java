package cl.duoc.VentasPerfulandia.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cl.duoc.VentasPerfulandia.dto.request.VentaCreateRequest;
import cl.duoc.VentasPerfulandia.dto.request.VentaEstadoRequest;
import cl.duoc.VentasPerfulandia.dto.response.VentaResponse;
import cl.duoc.VentasPerfulandia.service.VentaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("api/v1/ventas")
@RequiredArgsConstructor
public class VentaController {

    private final VentaService ventaService;

    @GetMapping
    public ResponseEntity<List<VentaResponse>> listarVentas() {
        List<VentaResponse> ventas = ventaService.listarVentas();
        return ResponseEntity.ok(ventas);
    }

    @PostMapping("/confirmar")
    public ResponseEntity<VentaResponse> confirmarVenta(
            @Valid @RequestBody VentaCreateRequest request
    ) {
        VentaResponse ventaCreada = ventaService.confirmarVenta(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ventaCreada);
    }

    @GetMapping("/{idVenta}")
    public ResponseEntity<VentaResponse> obtenerVentaPorId(@PathVariable Long idVenta) {
        VentaResponse venta = ventaService.obtenerVentaPorId(idVenta);
        return ResponseEntity.ok(venta);
    }

    @GetMapping("/pedido/{idPedido}")
    public ResponseEntity<VentaResponse> obtenerVentaPorPedido(@PathVariable Long idPedido) {
        VentaResponse venta = ventaService.obtenerVentaPorPedido(idPedido);
        return ResponseEntity.ok(venta);
    }

    @GetMapping("/usuario/{idUsuario}")
    public ResponseEntity<List<VentaResponse>> buscarPorUsuario(@PathVariable Long idUsuario) {
        List<VentaResponse> ventas = ventaService.buscarPorUsuario(idUsuario);
        return ResponseEntity.ok(ventas);
    }

    @GetMapping("/estado/{estadoVenta}")
    public ResponseEntity<List<VentaResponse>> buscarPorEstado(@PathVariable String estadoVenta) {
        List<VentaResponse> ventas = ventaService.buscarPorEstado(estadoVenta);
        return ResponseEntity.ok(ventas);
    }

    @PatchMapping("/{idVenta}/estado")
    public ResponseEntity<VentaResponse> actualizarEstado(
            @PathVariable Long idVenta,
            @Valid @RequestBody VentaEstadoRequest request
    ) {
        VentaResponse ventaActualizada = ventaService.actualizarEstado(idVenta, request);
        return ResponseEntity.ok(ventaActualizada);
    }

    @PatchMapping("/{idVenta}/anular")
    public ResponseEntity<VentaResponse> anularVenta(@PathVariable Long idVenta) {
        VentaResponse ventaAnulada = ventaService.anularVenta(idVenta);
        return ResponseEntity.ok(ventaAnulada);
    }

    @DeleteMapping("/{idVenta}")
    public ResponseEntity<Void> eliminarVenta(@PathVariable Long idVenta) {
        ventaService.eliminarVenta(idVenta);
        return ResponseEntity.noContent().build();
    }
}
