package cl.duoc.Perfulandiashipping.Service;

import cl.duoc.Perfulandiashipping.Exception.ResourceNotFoundException;
import cl.duoc.Perfulandiashipping.Client.OrderClient;
import cl.duoc.Perfulandiashipping.dto.Request.EnvioRequest;
import cl.duoc.Perfulandiashipping.dto.Response.OrderResponse;
import cl.duoc.Perfulandiashipping.dto.Response.EnvioResponse;
import cl.duoc.Perfulandiashipping.Model.EnvioModel;
import cl.duoc.Perfulandiashipping.Repository.EnvioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

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
        EnvioModel model = envioRepository.findById(idEnvio)
                .orElseThrow(() -> new ResourceNotFoundException("Envío no encontrado con ID: " + idEnvio));

        return mapToEnvioResponse(model);
    }

    public List<EnvioResponse> listarEnvios() {
        return envioRepository.findAll()
                .stream()
                .map(this::mapToEnvioResponse)
                .collect(Collectors.toList());
    }

    public EnvioResponse crearEnvio(EnvioRequest request) {
        if (envioRepository.existsByIdOrden(request.getIdOrden())) {
            throw new RuntimeException("Ya existe un envío para la orden: " + request.getIdOrden());
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
        return mapToEnvioResponse(guardado);
    }

    public EnvioResponse actualizarEstado(Long idEnvio, String estado) {
        EnvioModel model = envioRepository.findById(idEnvio)
                .orElseThrow(() -> new ResourceNotFoundException("Envío no encontrado con ID: " + idEnvio));

        model.setEstado(estado);
        return mapToEnvioResponse(envioRepository.save(model));
    }

    public void eliminarEnvio(Long idEnvio) {
        EnvioModel model = envioRepository.findById(idEnvio)
                .orElseThrow(() -> new ResourceNotFoundException("Envío no encontrado con ID: " + idEnvio));

        envioRepository.delete(model);
    }

    public EnvioResponse obtenerPorNumeroSeguimiento(String numeroSeguimiento) {
        EnvioModel model = envioRepository.findByNumeroSeguimiento(numeroSeguimiento)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Envío no encontrado con número de seguimiento: " + numeroSeguimiento));

        return mapToEnvioResponse(model);
    }
}
