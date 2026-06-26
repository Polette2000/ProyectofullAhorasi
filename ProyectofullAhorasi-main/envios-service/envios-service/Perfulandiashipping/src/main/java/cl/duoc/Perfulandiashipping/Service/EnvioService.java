package cl.duoc.Perfulandiashipping.Service;

import cl.duoc.Perfulandiashipping.Client.OrderClient;
import cl.duoc.Perfulandiashipping.Exception.ResourceNotFoundException;
import cl.duoc.Perfulandiashipping.Model.EnvioModel;
import cl.duoc.Perfulandiashipping.Repository.EnvioRepository;
import cl.duoc.Perfulandiashipping.dto.Request.EnvioRequest;
import cl.duoc.Perfulandiashipping.dto.Response.EnvioResponse;
import cl.duoc.Perfulandiashipping.dto.Response.OrderResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class EnvioService {

    private final EnvioRepository envioRepository;
    private final OrderClient orderClient;

    private EnvioResponse mapToEnvioResponse(EnvioModel model) {
        OrderResponse pedido = orderClient.obtenerPedido(model.getIdOrden());

        EnvioResponse response = new EnvioResponse();
        response.setIdEnvio(model.getIdEnvio());
        response.setIdOrden(model.getIdOrden());
        response.setIdUsuario(pedido.getIdUsuario());
        response.setEstadoPedido(pedido.getEstado());
        response.setDireccion(model.getDireccion());
        response.setNumeroSeguimiento(model.getNumeroSeguimiento());
        response.setEstado(model.getEstado());
        response.setFechaEstimadaInicio(model.getFechaEstimadaInicio());
        response.setFechaEstimadaFin(model.getFechaEstimadaFin());
        return response;
    }

    public EnvioResponse obtenerEnvioPorId(Long idEnvio) {
        log.info("Buscando envio ID: {}", idEnvio);
        EnvioModel model = envioRepository.findById(idEnvio)
                .orElseThrow(() -> {
                    log.warn("Envio no encontrado ID: {}", idEnvio);
                    return new ResourceNotFoundException("Envio no encontrado con ID: " + idEnvio);
                });

        return mapToEnvioResponse(model);
    }

    public List<EnvioResponse> listarEnvios() {
        log.info("Listando todos los envios");
        List<EnvioResponse> respuesta = envioRepository.findAll()
                .stream()
                .map(this::mapToEnvioResponse)
                .collect(Collectors.toList());

        log.info("Cantidad de envios encontrados={}", respuesta.size());
        return respuesta;
    }

    public EnvioResponse crearEnvio(EnvioRequest request) {
        log.info("Creando envio para orden ID: {}", request.getIdOrden());
        if (envioRepository.existsByIdOrden(request.getIdOrden())) {
            log.warn("Ya existe un envio para la orden ID: {}", request.getIdOrden());
            throw new RuntimeException("Ya existe un envio para la orden: " + request.getIdOrden());
        }
        orderClient.obtenerPedido(request.getIdOrden());

        EnvioModel model = new EnvioModel();
        model.setIdOrden(request.getIdOrden());
        model.setDireccion(request.getDireccion());
        model.setNumeroSeguimiento(request.getNumeroSeguimiento());
        model.setEstado(request.getEstado() != null ? request.getEstado() : "PENDIENTE");
        model.setFechaEstimadaInicio(request.getFechaEstimadaInicio());
        model.setFechaEstimadaFin(request.getFechaEstimadaFin());

        EnvioModel guardado = envioRepository.save(model);
        log.info("Envio creado con ID: {}", guardado.getIdEnvio());
        return mapToEnvioResponse(guardado);
    }

    public EnvioResponse actualizarEstado(Long idEnvio, String estado) {
        log.info("Actualizando estado del envio ID: {} a '{}'", idEnvio, estado);
        EnvioModel model = envioRepository.findById(idEnvio)
                .orElseThrow(() -> {
                    log.warn("Envio no encontrado ID: {}", idEnvio);
                    return new ResourceNotFoundException("Envio no encontrado con ID: " + idEnvio);
                });

        model.setEstado(estado);
        EnvioModel guardado = envioRepository.save(model);
        log.info("Estado del envio ID: {} actualizado correctamente", guardado.getIdEnvio());
        return mapToEnvioResponse(guardado);
    }

    public void eliminarEnvio(Long idEnvio) {
        log.info("Eliminando envio ID: {}", idEnvio);
        EnvioModel model = envioRepository.findById(idEnvio)
                .orElseThrow(() -> {
                    log.warn("Envio no encontrado ID: {}", idEnvio);
                    return new ResourceNotFoundException("Envio no encontrado con ID: " + idEnvio);
                });

        envioRepository.delete(model);
        log.info("Envio ID: {} eliminado correctamente", idEnvio);
    }

    public EnvioResponse obtenerPorNumeroSeguimiento(String numeroSeguimiento) {
        log.info("Buscando envio por numero de seguimiento: {}", numeroSeguimiento);
        EnvioModel model = envioRepository.findByNumeroSeguimiento(numeroSeguimiento)
                .orElseThrow(() -> {
                    log.warn("Envio no encontrado con numero de seguimiento: {}", numeroSeguimiento);
                    return new ResourceNotFoundException(
                            "Envio no encontrado con numero de seguimiento: " + numeroSeguimiento);
                });

        return mapToEnvioResponse(model);
    }
}
