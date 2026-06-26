package cl.duoc.SucursalesPerfulandia.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import cl.duoc.SucursalesPerfulandia.dto.request.SucursalCreateRequest;
import cl.duoc.SucursalesPerfulandia.dto.request.SucursalUpdateRequest;
import cl.duoc.SucursalesPerfulandia.dto.response.SucursalResponse;
import cl.duoc.SucursalesPerfulandia.exception.ResourceNotFoundException;
import cl.duoc.SucursalesPerfulandia.model.Comuna;
import cl.duoc.SucursalesPerfulandia.model.Sucursal;
import cl.duoc.SucursalesPerfulandia.repository.ComunaRepository;
import cl.duoc.SucursalesPerfulandia.repository.SucursalRepository;

@ExtendWith(MockitoExtension.class)
class SucursalServiceTest {

    @Mock
    private SucursalRepository sucursalRepository;

    @Mock
    private ComunaRepository comunaRepository;

    @InjectMocks
    private SucursalService sucursalService;

    private Comuna crearComuna() {
        return new Comuna(1, "Santiago", "Metropolitana", List.of());
    }

    private Sucursal crearSucursal() {
        return new Sucursal(
                1,
                "Sucursal Centro",
                "Av. Principal 123",
                "+56912345678",
                "09:00 a 18:00",
                crearComuna());
    }

    @Test
    void listarSucursales_debeRetornarSucursales() {
        when(sucursalRepository.findAll()).thenReturn(List.of(crearSucursal()));

        List<SucursalResponse> response = sucursalService.listarSucursales();

        assertThat(response).hasSize(1);
        assertThat(response.get(0).getNombreSucursal()).isEqualTo("Sucursal Centro");
        assertThat(response.get(0).getNombreComuna()).isEqualTo("Santiago");
    }

    @Test
    void buscarSucursalPorId_debeRetornarSucursalCuandoExiste() {
        when(sucursalRepository.findById(1)).thenReturn(Optional.of(crearSucursal()));

        SucursalResponse response = sucursalService.buscarSucursalPorId(1);

        assertThat(response.getIdSucursal()).isEqualTo(1);
        assertThat(response.getRegion()).isEqualTo("Metropolitana");
    }

    @Test
    void crearSucursal_debeCrearSucursalCuandoComunaExiste() {
        SucursalCreateRequest request = new SucursalCreateRequest(
                "Sucursal Centro",
                "Av. Principal 123",
                "+56912345678",
                "09:00 a 18:00",
                1);
        when(comunaRepository.findById(1)).thenReturn(Optional.of(crearComuna()));
        when(sucursalRepository.save(any(Sucursal.class))).thenReturn(crearSucursal());

        SucursalResponse response = sucursalService.crearSucursal(request);

        assertThat(response.getIdSucursal()).isEqualTo(1);
        assertThat(response.getDireccion()).isEqualTo("Av. Principal 123");
        verify(sucursalRepository).save(any(Sucursal.class));
    }

    @Test
    void crearSucursal_debeLanzarExcepcionCuandoComunaNoExiste() {
        SucursalCreateRequest request = new SucursalCreateRequest(
                "Sucursal Centro",
                "Av. Principal 123",
                "+56912345678",
                "09:00 a 18:00",
                99);
        when(comunaRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> sucursalService.crearSucursal(request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Comuna no encontrada con ID: 99");
        verify(sucursalRepository, never()).save(any(Sucursal.class));
    }

    @Test
    void actualizarSucursal_debeActualizarCuandoSucursalYComunaExisten() {
        Sucursal sucursal = crearSucursal();
        SucursalUpdateRequest request = new SucursalUpdateRequest(
                "Sucursal Norte",
                "Calle Nueva 456",
                "+56987654321",
                "10:00 a 19:00",
                1);
        Sucursal actualizada = new Sucursal(
                1,
                "Sucursal Norte",
                "Calle Nueva 456",
                "+56987654321",
                "10:00 a 19:00",
                crearComuna());

        when(sucursalRepository.findById(1)).thenReturn(Optional.of(sucursal));
        when(comunaRepository.findById(1)).thenReturn(Optional.of(crearComuna()));
        when(sucursalRepository.save(sucursal)).thenReturn(actualizada);

        SucursalResponse response = sucursalService.actualizarSucursal(1, request);

        assertThat(response.getNombreSucursal()).isEqualTo("Sucursal Norte");
        assertThat(response.getDireccion()).isEqualTo("Calle Nueva 456");
        verify(sucursalRepository).save(sucursal);
    }
}
