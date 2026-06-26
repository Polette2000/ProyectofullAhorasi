package cl.duoc.Perfulandiashipping.Service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import cl.duoc.Perfulandiashipping.Client.OrderClient;
import cl.duoc.Perfulandiashipping.Exception.ResourceNotFoundException;
import cl.duoc.Perfulandiashipping.Model.EnvioModel;
import cl.duoc.Perfulandiashipping.Repository.EnvioRepository;
import cl.duoc.Perfulandiashipping.dto.Request.EnvioRequest;
import cl.duoc.Perfulandiashipping.dto.Response.EnvioResponse;
import cl.duoc.Perfulandiashipping.dto.Response.OrderResponse;

@ExtendWith(MockitoExtension.class)
class EnvioServiceTest {

    @Mock
    private EnvioRepository envioRepository;

    @Mock
    private OrderClient orderClient;

    @InjectMocks
    private EnvioService envioService;

    private OrderResponse crearPedido() {
        return new OrderResponse(10L, 5L, LocalDateTime.now(), "PAGADO", List.of());
    }

    private EnvioModel crearEnvio() {
        return new EnvioModel(
                1L,
                10L,
                "Av. Principal 123",
                "SEG-001",
                "PENDIENTE",
                LocalDate.now().plusDays(1),
                LocalDate.now().plusDays(3));
    }

    private EnvioRequest crearRequest() {
        EnvioRequest request = new EnvioRequest();
        request.setIdOrden(10L);
        request.setDireccion("Av. Principal 123");
        request.setNumeroSeguimiento("SEG-001");
        request.setEstado("PENDIENTE");
        request.setFechaEstimadaInicio(LocalDate.now().plusDays(1));
        request.setFechaEstimadaFin(LocalDate.now().plusDays(3));
        return request;
    }

    @Test
    void crearEnvio_debeCrearEnvioCuandoOrdenExisteYNoTieneEnvio() {
        EnvioRequest request = crearRequest();
        when(envioRepository.existsByIdOrden(10L)).thenReturn(false);
        when(orderClient.obtenerPedido(10L)).thenReturn(crearPedido());
        when(envioRepository.save(any(EnvioModel.class))).thenReturn(crearEnvio());

        EnvioResponse response = envioService.crearEnvio(request);

        assertThat(response.getIdEnvio()).isEqualTo(1L);
        assertThat(response.getIdUsuario()).isEqualTo(5L);
        assertThat(response.getEstadoPedido()).isEqualTo("PAGADO");
        assertThat(response.getNumeroSeguimiento()).isEqualTo("SEG-001");
        verify(envioRepository).save(any(EnvioModel.class));
    }

    @Test
    void crearEnvio_debeLanzarExcepcionCuandoOrdenYaTieneEnvio() {
        EnvioRequest request = crearRequest();
        when(envioRepository.existsByIdOrden(10L)).thenReturn(true);

        assertThatThrownBy(() -> envioService.crearEnvio(request))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Ya existe un envio para la orden: 10");
        verify(orderClient, never()).obtenerPedido(10L);
        verify(envioRepository, never()).save(any(EnvioModel.class));
    }

    @Test
    void obtenerEnvioPorId_debeRetornarEnvioCuandoExiste() {
        when(envioRepository.findById(1L)).thenReturn(Optional.of(crearEnvio()));
        when(orderClient.obtenerPedido(10L)).thenReturn(crearPedido());

        EnvioResponse response = envioService.obtenerEnvioPorId(1L);

        assertThat(response.getIdEnvio()).isEqualTo(1L);
        assertThat(response.getDireccion()).isEqualTo("Av. Principal 123");
    }

    @Test
    void actualizarEstado_debeActualizarCuandoEnvioExiste() {
        EnvioModel envio = crearEnvio();
        EnvioModel actualizado = crearEnvio();
        actualizado.setEstado("EN_TRANSITO");

        when(envioRepository.findById(1L)).thenReturn(Optional.of(envio));
        when(envioRepository.save(envio)).thenReturn(actualizado);
        when(orderClient.obtenerPedido(10L)).thenReturn(crearPedido());

        EnvioResponse response = envioService.actualizarEstado(1L, "EN_TRANSITO");

        assertThat(response.getEstado()).isEqualTo("EN_TRANSITO");
        verify(envioRepository).save(envio);
    }

    @Test
    void obtenerPorNumeroSeguimiento_debeRetornarEnvioCuandoExiste() {
        when(envioRepository.findByNumeroSeguimiento("SEG-001")).thenReturn(Optional.of(crearEnvio()));
        when(orderClient.obtenerPedido(10L)).thenReturn(crearPedido());

        EnvioResponse response = envioService.obtenerPorNumeroSeguimiento("SEG-001");

        assertThat(response.getNumeroSeguimiento()).isEqualTo("SEG-001");
    }
}
