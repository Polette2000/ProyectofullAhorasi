package cl.duoc.Perfulandiabilling.Controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import cl.duoc.Perfulandiabilling.dto.Request.PagoCreateRequest;
import cl.duoc.Perfulandiabilling.dto.Request.FacturaCreateRequest;
import cl.duoc.Perfulandiabilling.dto.Response.PagoResponse;
import cl.duoc.Perfulandiabilling.dto.Response.FacturaResponse;
import cl.duoc.Perfulandiabilling.Service.FacturacionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/billing")
@RequiredArgsConstructor
public class FacturacionController {

    private final FacturacionService facturacionService;

    // Lista todos los pagos registrados
    @GetMapping("/pagos")
    public ResponseEntity<List<PagoResponse>> listarPagos() {
        List<PagoResponse> pagos = facturacionService.listarPagos();
        return ResponseEntity.ok(pagos);
    }

    // Registra un nuevo pago
    @PostMapping("/pagos")
    public ResponseEntity<PagoResponse> registrarPago(
            @Valid @RequestBody PagoCreateRequest request) {

        PagoResponse pagoCreado = facturacionService.registrarPago(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(pagoCreado);
    }

    // Obtiene un pago específico por ID
    @GetMapping("/pagos/{id}")
    public ResponseEntity<PagoResponse> obtenerPago(@PathVariable Long id) {
        PagoResponse pago = facturacionService.obtenerPagoPorId(id);
        return ResponseEntity.ok(pago);
    }

    // Elimina un pago por ID
    @DeleteMapping("/pagos/{id}")
    public ResponseEntity<Void> eliminarPago(@PathVariable Long id) {
        facturacionService.eliminarPago(id);
        return ResponseEntity.noContent().build();
    }

    // Lista todas las facturas
    @GetMapping("/facturas")
    public ResponseEntity<List<FacturaResponse>> listarFacturas() {
        List<FacturaResponse> facturas = facturacionService.listarFacturas();
        return ResponseEntity.ok(facturas);
    }

    // Genera una nueva factura
    @PostMapping("/facturas")
    public ResponseEntity<FacturaResponse> generarFactura(
            @Valid @RequestBody FacturaCreateRequest request) {

        FacturaResponse facturaCreada = facturacionService.generarFactura(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(facturaCreada);
    }

    // Obtiene una factura específica por ID
    @GetMapping("/facturas/{id}")
    public ResponseEntity<FacturaResponse> obtenerFactura(@PathVariable Long id) {
        FacturaResponse factura = facturacionService.obtenerFacturaPorId(id);
        return ResponseEntity.ok(factura);
    }

    // Elimina una factura por ID
    @DeleteMapping("/facturas/{id}")
    public ResponseEntity<Void> eliminarFactura(@PathVariable Long id) {
        facturacionService.eliminarFactura(id);
        return ResponseEntity.noContent().build();
    }
}
