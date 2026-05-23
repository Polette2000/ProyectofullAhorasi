package cl.duoc.Perfulandiabilling.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import cl.duoc.Perfulandiabilling.Repository.PagoRepository;
import cl.duoc.Perfulandiabilling.Repository.FacturaRepository;
import cl.duoc.Perfulandiabilling.Client.OrderClient;
import cl.duoc.Perfulandiabilling.Model.PagoModel;
import cl.duoc.Perfulandiabilling.Model.FacturaModel;
import cl.duoc.Perfulandiabilling.Exception.ResourceNotFoundException;
import cl.duoc.Perfulandiabilling.dto.Request.PagoCreateRequest;
import cl.duoc.Perfulandiabilling.dto.Request.FacturaCreateRequest;
import cl.duoc.Perfulandiabilling.dto.Response.PagoResponse;
import cl.duoc.Perfulandiabilling.dto.Response.FacturaResponse;
import cl.duoc.Perfulandiabilling.dto.Response.OrderResponse;

@Slf4j
@Service
@RequiredArgsConstructor
public class FacturacionService {

    private final PagoRepository paymentRepository;
    private final FacturaRepository invoiceRepository;
    private final OrderClient orderClient;

    private PagoResponse mapToPaymentDTO(PagoModel payment) {
        return new PagoResponse(
                payment.getIdPago(),
                payment.getIdUsuario(),
                payment.getIdOrden(),
                payment.getMonto(),
                payment.getMetodo(),
                payment.getEstado()
        );
    }

    private FacturaResponse mapToInvoiceDTO(FacturaModel invoice) {
        return new FacturaResponse(
                invoice.getIdFactura(),
                invoice.getIdUsuario(),
                invoice.getIdPago(),
                invoice.getMontoTotal(),
                invoice.getDescripcion(),
                invoice.getEstado()
        );
    }

    public PagoResponse registrarPago(PagoCreateRequest request) {
        log.info("Registrando pago para usuario ID: {}", request.getIdUsuario());
        OrderResponse pedido = orderClient.obtenerPedido(request.getIdOrden());

        PagoModel pago = PagoModel.builder()
                .idUsuario(pedido.getIdUsuario())
                .idOrden(request.getIdOrden())
                .monto(request.getMonto())
                .metodo(request.getMetodo())
                .estado("COMPLETADO")
                .fechaRegistro(LocalDateTime.now())
                .build();

        PagoModel saved = paymentRepository.save(pago);
        log.info("Pago registrado con ID: {}", saved.getIdPago());
        return mapToPaymentDTO(saved);
    }

    public List<PagoResponse> listarPagos() {
        return paymentRepository.findAll()
                .stream()
                .map(this::mapToPaymentDTO)
                .collect(Collectors.toList());
    }

    public PagoResponse obtenerPagoPorId(Long id) {
        return paymentRepository.findById(id)
                .map(this::mapToPaymentDTO)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Pago no encontrado con ID: " + id));
    }

    public void eliminarPago(Long id) {
        if (!paymentRepository.existsById(id)) {
            throw new ResourceNotFoundException("Pago no encontrado con ID: " + id);
        }
        paymentRepository.deleteById(id);
    }

    public FacturaResponse generarFactura(FacturaCreateRequest request) {
        log.info("Generando factura para usuario ID: {}", request.getIdUsuario());

        FacturaModel factura = FacturaModel.builder()
                .idUsuario(request.getIdUsuario())
                .idPago(request.getIdPago())
                .montoTotal(request.getMontoTotal())
                .descripcion(request.getDescripcion())
                .estado("EMITIDA")
                .fechaEmision(LocalDateTime.now())
                .build();

        FacturaModel saved = invoiceRepository.save(factura);
        log.info("Factura generada con ID: {}", saved.getIdFactura());
        return mapToInvoiceDTO(saved);
    }

    public List<FacturaResponse> listarFacturas() {
        return invoiceRepository.findAll()
                .stream()
                .map(this::mapToInvoiceDTO)
                .collect(Collectors.toList());
    }

    public FacturaResponse obtenerFacturaPorId(Long id) {
        return invoiceRepository.findById(id)
                .map(this::mapToInvoiceDTO)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Factura no encontrada con ID: " + id));
    }

    public void eliminarFactura(Long id) {
        if (!invoiceRepository.existsById(id)) {
            throw new ResourceNotFoundException("Factura no encontrada con ID: " + id);
        }
        invoiceRepository.deleteById(id);
    }
}
