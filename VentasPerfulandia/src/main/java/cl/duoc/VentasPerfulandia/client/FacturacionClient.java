package cl.duoc.VentasPerfulandia.client;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import cl.duoc.VentasPerfulandia.dto.response.FacturaResponse;
import cl.duoc.VentasPerfulandia.dto.response.PagoResponse;
import cl.duoc.VentasPerfulandia.exception.ResourceNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class FacturacionClient {

    @Qualifier("webClientFacturacion")
    private final WebClient webClientFacturacion;

    private final HttpServletRequest httpServletRequest;

    public PagoResponse obtenerPagoPorId(Long idPago) {
        return webClientFacturacion.get()
                .uri("/api/v1/billing/pagos/{idPago}", idPago)
                .header("Authorization", obtenerToken())
                .retrieve()
                .onStatus(
                        status -> status.value() == 404,
                        response -> Mono.error(new ResourceNotFoundException(
                                "Pago no encontrado con ID: " + idPago)))
                .bodyToMono(PagoResponse.class)
                .block();
    }

    public FacturaResponse obtenerFacturaPorId(Long idFactura) {
        return webClientFacturacion.get()
                .uri("/api/v1/billing/facturas/{idFactura}", idFactura)
                .header("Authorization", obtenerToken())
                .retrieve()
                .onStatus(
                        status -> status.value() == 404,
                        response -> Mono.error(new ResourceNotFoundException(
                                "Factura no encontrada con ID: " + idFactura)))
                .bodyToMono(FacturaResponse.class)
                .block();
    }

    private String obtenerToken() {
        return httpServletRequest.getHeader("Authorization");
    }
}
