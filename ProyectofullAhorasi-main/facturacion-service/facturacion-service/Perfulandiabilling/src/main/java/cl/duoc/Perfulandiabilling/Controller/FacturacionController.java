package cl.duoc.Perfulandiabilling.Controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cl.duoc.Perfulandiabilling.Service.FacturacionService;
import cl.duoc.Perfulandiabilling.dto.Request.FacturaCreateRequest;
import cl.duoc.Perfulandiabilling.dto.Request.PagoCreateRequest;
import cl.duoc.Perfulandiabilling.dto.Response.FacturaResponse;
import cl.duoc.Perfulandiabilling.dto.Response.PagoResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/billing")
@RequiredArgsConstructor
@Tag(name = "Facturacion", description = "Gestion de pagos y facturas")
public class FacturacionController {

    private final FacturacionService facturacionService;

    private static final String PAGO_REQUEST_EXAMPLE = """
            {
              "idUsuario": 5,
              "idOrden": 1,
              "monto": 25980.0,
              "metodo": "TARJETA"
            }
            """;

    private static final String PAGO_RESPONSE_EXAMPLE = """
            {
              "idPago": 1,
              "idUsuario": 5,
              "idOrden": 1,
              "monto": 25980.0,
              "metodo": "TARJETA",
              "estado": "COMPLETADO"
            }
            """;

    private static final String PAGOS_LIST_RESPONSE_EXAMPLE = """
            [
              {
                "idPago": 1,
                "idUsuario": 5,
                "idOrden": 1,
                "monto": 25980.0,
                "metodo": "TARJETA",
                "estado": "COMPLETADO"
              }
            ]
            """;

    private static final String FACTURA_REQUEST_EXAMPLE = """
            {
              "idPago": 1,
              "idSucursal": 2,
              "descripcion": "Compra de perfumes"
            }
            """;

    private static final String FACTURA_RESPONSE_EXAMPLE = """
            {
              "idFactura": 1,
              "idUsuario": 5,
              "idSucursal": 2,
              "nombreSucursal": "Sucursal Centro",
              "idPago": 1,
              "productos": [
                {
                  "idProducto": 10,
                  "nombreProducto": "Perfume Floral",
                  "cantidad": 2,
                  "precioUnitario": 12990.0,
                  "subtotal": 25980.0
                }
              ],
              "cantidadTotal": 2,
              "montoTotal": 25980.0,
              "descripcion": "Compra de perfumes",
              "estado": "EMITIDA",
              "fechaEmision": "2026-06-25T19:30:00"
            }
            """;

    private static final String FACTURAS_LIST_RESPONSE_EXAMPLE = """
            [
              {
                "idFactura": 1,
                "idUsuario": 5,
                "idSucursal": 2,
                "nombreSucursal": "Sucursal Centro",
                "idPago": 1,
                "productos": [
                  {
                    "idProducto": 10,
                    "nombreProducto": "Perfume Floral",
                    "cantidad": 2,
                    "precioUnitario": 12990.0,
                    "subtotal": 25980.0
                  }
                ],
                "cantidadTotal": 2,
                "montoTotal": 25980.0,
                "descripcion": "Compra de perfumes",
                "estado": "EMITIDA",
                "fechaEmision": "2026-06-25T19:30:00"
              }
            ]
            """;

    @GetMapping("/pagos")
    @Operation(summary = "Listar pagos", description = "Obtiene todos los pagos registrados.")
    @ApiResponse(responseCode = "200", description = "Pagos encontrados",
            content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(value = PAGOS_LIST_RESPONSE_EXAMPLE)))
    public ResponseEntity<?> listarPagos() {
        List<PagoResponse> pagos = facturacionService.listarPagos();
        if (pagos.isEmpty()) {
            return ResponseEntity.ok("No hay pagos registrados");
        }
        return ResponseEntity.ok(pagos);
    }

    @PostMapping("/pagos")
    @Operation(summary = "Registrar pago", description = "Registra un pago asociado a una orden.")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Datos del pago",
            content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(value = PAGO_REQUEST_EXAMPLE)))
    @ApiResponse(responseCode = "201", description = "Pago creado",
            content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(value = PAGO_RESPONSE_EXAMPLE)))
    public ResponseEntity<PagoResponse> registrarPago(
            @Valid @RequestBody PagoCreateRequest request) {

        PagoResponse pagoCreado = facturacionService.registrarPago(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(pagoCreado);
    }

    @GetMapping("/pagos/{id}")
    @Operation(summary = "Buscar pago por ID", description = "Obtiene un pago especifico por su ID.")
    @ApiResponse(responseCode = "200", description = "Pago encontrado",
            content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(value = PAGO_RESPONSE_EXAMPLE)))
    public ResponseEntity<PagoResponse> obtenerPago(@PathVariable Long id) {
        PagoResponse pago = facturacionService.obtenerPagoPorId(id);
        return ResponseEntity.ok(pago);
    }

    @DeleteMapping("/pagos/{id}")
    @Operation(summary = "Eliminar pago", description = "Elimina un pago por ID.")
    @ApiResponse(responseCode = "200", description = "Pago eliminado",
            content = @Content(mediaType = "text/plain",
                    examples = @ExampleObject(value = "Pago eliminado correctamente")))
    public ResponseEntity<String> eliminarPago(@PathVariable Long id) {
        facturacionService.eliminarPago(id);
        return ResponseEntity.ok("Pago eliminado correctamente");
    }

    @GetMapping("/facturas")
    @Operation(summary = "Listar facturas", description = "Obtiene todas las facturas registradas.")
    @ApiResponse(responseCode = "200", description = "Facturas encontradas",
            content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(value = FACTURAS_LIST_RESPONSE_EXAMPLE)))
    public ResponseEntity<?> listarFacturas() {
        List<FacturaResponse> facturas = facturacionService.listarFacturas();
        if (facturas.isEmpty()) {
            return ResponseEntity.ok("No hay facturas registradas");
        }
        return ResponseEntity.ok(facturas);
    }

    @PostMapping("/facturas")
    @Operation(summary = "Generar factura", description = "Genera una factura desde un pago y una sucursal.")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Pago, sucursal y descripcion para emitir la factura",
            content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(value = FACTURA_REQUEST_EXAMPLE)))
    @ApiResponse(responseCode = "201", description = "Factura generada",
            content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(value = FACTURA_RESPONSE_EXAMPLE)))
    public ResponseEntity<FacturaResponse> generarFactura(
            @Valid @RequestBody FacturaCreateRequest request) {

        FacturaResponse facturaCreada = facturacionService.generarFactura(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(facturaCreada);
    }

    @GetMapping("/facturas/{id}")
    @Operation(summary = "Buscar factura por ID", description = "Obtiene una factura especifica por su ID.")
    @ApiResponse(responseCode = "200", description = "Factura encontrada",
            content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(value = FACTURA_RESPONSE_EXAMPLE)))
    public ResponseEntity<FacturaResponse> obtenerFactura(@PathVariable Long id) {
        FacturaResponse factura = facturacionService.obtenerFacturaPorId(id);
        return ResponseEntity.ok(factura);
    }

    @DeleteMapping("/facturas/{id}")
    @Operation(summary = "Eliminar factura", description = "Elimina una factura por ID.")
    @ApiResponse(responseCode = "200", description = "Factura eliminada",
            content = @Content(mediaType = "text/plain",
                    examples = @ExampleObject(value = "Factura eliminada correctamente")))
    public ResponseEntity<String> eliminarFactura(@PathVariable Long id) {
        facturacionService.eliminarFactura(id);
        return ResponseEntity.ok("Factura eliminada correctamente");
    }
}
