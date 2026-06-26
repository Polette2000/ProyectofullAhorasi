package cl.duoc.proveedor.service;

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

import cl.duoc.proveedor.dto.request.ProveedorCreateRequest;
import cl.duoc.proveedor.dto.request.ProveedorUpdateRequest;
import cl.duoc.proveedor.dto.response.ProveedorResponse;
import cl.duoc.proveedor.exception.ResourceNotFoundException;
import cl.duoc.proveedor.model.Proveedor;
import cl.duoc.proveedor.repository.ProveedorRepository;

// Permite probar el service con Mockito sin levantar todo Spring Boot.
@ExtendWith(MockitoExtension.class)
class ProveedorServiceTest {

    // Simula el repositorio para no depender de MySQL.
    @Mock
    private ProveedorRepository proveedorRepository;

    // Crea ProveedorService usando el repositorio simulado.
    @InjectMocks
    private ProveedorService proveedorService;

    // Prueba 1: verifica que listarProveedores devuelva todos los proveedores mapeados a DTO.
    @Test
    void listarProveedoresDebeRetornarListaDeProveedores() {
        // Arrange: se simula que el repositorio encuentra un proveedor.
        Proveedor proveedor = crearProveedor(1L, "Aromas Chile SpA", "contacto@aromaschile.cl", "+56912345678", "Av. Principal 123");
        when(proveedorRepository.findAll()).thenReturn(List.of(proveedor));

        // Act: se ejecuta el metodo real del service.
        List<ProveedorResponse> resultado = proveedorService.listarProveedores();

        // Assert: se valida que la respuesta contenga los datos esperados.
        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getIdProveedor()).isEqualTo(1L);
        assertThat(resultado.get(0).getNombre()).isEqualTo("Aromas Chile SpA");
        assertThat(resultado.get(0).getCorreo()).isEqualTo("contacto@aromaschile.cl");
    }

    // Prueba 2: verifica que buscarPorId retorne un proveedor cuando existe.
    @Test
    void buscarPorIdDebeRetornarProveedorCuandoExiste() {
        // Arrange: se simula que existe el proveedor con ID 1.
        Proveedor proveedor = crearProveedor(1L, "Distribuidora Fragancias SPA", "ventas@fraganciasspa.cl", "+56987654321", "Calle Comercio 456");
        when(proveedorRepository.findById(1L)).thenReturn(Optional.of(proveedor));

        // Act: se busca el proveedor desde el service.
        ProveedorResponse resultado = proveedorService.buscarPorId(1L);

        // Assert: se valida que el DTO tenga los datos correctos.
        assertThat(resultado.getIdProveedor()).isEqualTo(1L);
        assertThat(resultado.getNombre()).isEqualTo("Distribuidora Fragancias SPA");
        assertThat(resultado.getTelefono()).isEqualTo("+56987654321");
    }

    // Prueba 3: verifica que crearProveedor guarde correctamente un nuevo proveedor.
    @Test
    void crearProveedorDebeGuardarYRetornarResponse() {
        // Arrange: se prepara el request y se simula el guardado con ID generado.
        ProveedorCreateRequest request = new ProveedorCreateRequest();
        request.setNombre("Importadora Esencias del Sur");
        request.setCorreo("contacto@esenciasdelsur.cl");
        request.setTelefono("+56933333333");
        request.setDireccion("Av. Marina 789");

        when(proveedorRepository.save(any(Proveedor.class))).thenAnswer(invocation -> {
            Proveedor proveedor = invocation.getArgument(0);
            proveedor.setIdProveedor(10L);
            return proveedor;
        });

        // Act: se crea el proveedor desde el service.
        ProveedorResponse resultado = proveedorService.crearProveedor(request);

        // Assert: se valida que se guarde y que retorne el proveedor creado.
        assertThat(resultado.getIdProveedor()).isEqualTo(10L);
        assertThat(resultado.getNombre()).isEqualTo("Importadora Esencias del Sur");
        assertThat(resultado.getCorreo()).isEqualTo("contacto@esenciasdelsur.cl");
        verify(proveedorRepository).save(any(Proveedor.class));
    }

    // Prueba 4: verifica que actualizarProveedor cambie los datos cuando el proveedor existe.
    @Test
    void actualizarProveedorDebeModificarDatosCuandoExiste() {
        // Arrange: se prepara un proveedor existente y un request con nuevos datos.
        Proveedor proveedorExistente = crearProveedor(5L, "Nombre anterior", "antes@proveedor.cl", "+56911111111", "Direccion anterior");
        ProveedorUpdateRequest request = new ProveedorUpdateRequest();
        request.setNombre("Proveedor Actualizado");
        request.setCorreo("actualizado@proveedor.cl");
        request.setTelefono("+56922222222");
        request.setDireccion("Direccion actualizada");

        when(proveedorRepository.findById(5L)).thenReturn(Optional.of(proveedorExistente));
        when(proveedorRepository.save(any(Proveedor.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act: se actualiza el proveedor.
        ProveedorResponse resultado = proveedorService.actualizarProveedor(5L, request);

        // Assert: se comprueba que los datos hayan cambiado y se haya guardado.
        assertThat(resultado.getNombre()).isEqualTo("Proveedor Actualizado");
        assertThat(resultado.getCorreo()).isEqualTo("actualizado@proveedor.cl");
        assertThat(resultado.getDireccion()).isEqualTo("Direccion actualizada");
        verify(proveedorRepository).save(proveedorExistente);
    }

    // Prueba 5: verifica el error al eliminar un proveedor que no existe.
    @Test
    void eliminarProveedorDebeFallarCuandoNoExiste() {
        // Arrange: se simula que no existe proveedor con ID 99.
        when(proveedorRepository.findById(99L)).thenReturn(Optional.empty());

        // Act y Assert: se espera ResourceNotFoundException con el mensaje del service.
        assertThatThrownBy(() -> proveedorService.eliminarProveedor(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Proveedor no encontrado con ID: 99");

        // Verifica que no se elimina nada si no existe.
        verify(proveedorRepository, never()).delete(any(Proveedor.class));
    }

    // Metodo auxiliar para no repetir la construccion de proveedores en cada test.
    private Proveedor crearProveedor(Long idProveedor, String nombre, String correo, String telefono, String direccion) {
        return Proveedor.builder()
                .idProveedor(idProveedor)
                .nombre(nombre)
                .correo(correo)
                .telefono(telefono)
                .direccion(direccion)
                .build();
    }
}