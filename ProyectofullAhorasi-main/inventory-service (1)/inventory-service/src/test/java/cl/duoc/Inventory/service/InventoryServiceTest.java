package cl.duoc.Inventory.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import cl.duoc.Inventory.client.ProductoClient;
import cl.duoc.Inventory.client.SucursalClient;
import cl.duoc.Inventory.dto.request.InventoryCreateRequest;
import cl.duoc.Inventory.dto.request.InventoryUpdateRequest;
import cl.duoc.Inventory.dto.response.InventoryResponse;
import cl.duoc.Inventory.dto.response.ProductoResponse;
import cl.duoc.Inventory.dto.response.SucursalResponse;
import cl.duoc.Inventory.exception.ResourceNotFoundException;
import cl.duoc.Inventory.model.Inventory;
import cl.duoc.Inventory.repository.InventoryRepository;

// Permite usar Mockito en las pruebas sin levantar todo el contexto de Spring.
@ExtendWith(MockitoExtension.class)
class InventoryServiceTest {

    // Simula el repositorio para no depender de la base de datos real.
    @Mock
    private InventoryRepository inventoryRepository;

    // Simula el cliente de producto para no levantar el microservicio producto.
    @Mock
    private ProductoClient productoClient;

    // Simula el cliente de sucursal para no levantar el microservicio sucursal.
    @Mock
    private SucursalClient sucursalClient;

    // Crea InventoryService usando los mocks anteriores.
    @InjectMocks
    private InventoryService inventoryService;

    private ProductoResponse producto;
    private SucursalResponse sucursal;

    // Prepara datos comunes antes de cada prueba.
    @BeforeEach
    void setUp() {
        producto = new ProductoResponse();
        producto.setIdProducto(1L);
        producto.setNombre("Perfume Floral Primavera");

        sucursal = new SucursalResponse();
        sucursal.setIdSucursal(1L);
        sucursal.setNombre("Sucursal Centro");
    }

    // Prueba 1: verifica que listar inventario devuelva registros y agregue el nombre del producto.
    @Test
    void listarInventarioDebeRetornarListaConNombreProducto() {
        // Arrange: se prepara un inventario y se define lo que responderan los mocks.
        Inventory inventory = crearInventory(1L, 1L, 1L, 25);

        when(inventoryRepository.findAll()).thenReturn(List.of(inventory));
        when(productoClient.obtenerProductoPorId(1L)).thenReturn(producto);

        // Act: se ejecuta el metodo real del service.
        List<InventoryResponse> resultado = inventoryService.listarInventario();

        // Assert: se comprueba que la respuesta tenga los datos esperados.
        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getIdInventory()).isEqualTo(1L);
        assertThat(resultado.get(0).getNombreProducto()).isEqualTo("Perfume Floral Primavera");
        assertThat(resultado.get(0).getStockDisponible()).isEqualTo(25);
    }

    // Prueba 2: verifica que se pueda crear inventario cuando producto y sucursal existen.
    @Test
    void crearInventarioDebeGuardarCuandoProductoYSucursalExisten() {
        // Arrange: se prepara el request y se simula que no existe inventario duplicado.
        InventoryCreateRequest request = new InventoryCreateRequest(1L, 1L, 30);

        when(productoClient.obtenerProductoPorId(1L)).thenReturn(producto);
        when(sucursalClient.obtenerSucursalPorId(1L)).thenReturn(sucursal);
        when(inventoryRepository.existsByIdProductoAndIdSucursal(1L, 1L)).thenReturn(false);
        when(inventoryRepository.save(any(Inventory.class))).thenAnswer(invocation -> {
            Inventory inventory = invocation.getArgument(0);
            inventory.setIdInventory(10L);
            return inventory;
        });

        // Act: se ejecuta la creacion del inventario.
        InventoryResponse resultado = inventoryService.crearInventario(request);

        // Assert: se valida la respuesta y que realmente se llamo al save del repositorio.
        assertThat(resultado.getIdInventory()).isEqualTo(10L);
        assertThat(resultado.getIdProducto()).isEqualTo(1L);
        assertThat(resultado.getIdSucursal()).isEqualTo(1L);
        assertThat(resultado.getStockDisponible()).isEqualTo(30);
        verify(inventoryRepository).save(any(Inventory.class));
    }

    // Prueba 3: verifica que no se permita crear inventario duplicado para producto y sucursal.
    @Test
    void crearInventarioDebeFallarCuandoYaExisteProductoEnSucursal() {
        // Arrange: se simula que ya existe inventario para el producto y la sucursal.
        InventoryCreateRequest request = new InventoryCreateRequest(1L, 1L, 30);

        when(productoClient.obtenerProductoPorId(1L)).thenReturn(producto);
        when(sucursalClient.obtenerSucursalPorId(1L)).thenReturn(sucursal);
        when(inventoryRepository.existsByIdProductoAndIdSucursal(1L, 1L)).thenReturn(true);

        // Act y Assert: se espera una excepcion de negocio por inventario duplicado.
        assertThatThrownBy(() -> inventoryService.crearInventario(request))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Ya existe inventario para este producto en esta sucursal");

        // Verifica que no se guarde nada cuando existe duplicado.
        verify(inventoryRepository, never()).save(any(Inventory.class));
    }

    // Prueba 4: verifica que actualizar inventario cambie el stock cuando el registro existe.
    @Test
    void actualizarInventarioDebeCambiarStockCuandoExiste() {
        // Arrange: se prepara un inventario existente con stock anterior.
        Inventory inventoryExistente = crearInventory(5L, 1L, 1L, 20);
        InventoryUpdateRequest request = new InventoryUpdateRequest(1L, 1L, 45);

        when(productoClient.obtenerProductoPorId(1L)).thenReturn(producto);
        when(sucursalClient.obtenerSucursalPorId(1L)).thenReturn(sucursal);
        when(inventoryRepository.findByIdProductoAndIdSucursal(1L, 1L))
                .thenReturn(Optional.of(inventoryExistente));
        when(inventoryRepository.save(any(Inventory.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act: se actualiza el stock desde el service.
        InventoryResponse resultado = inventoryService.actualizarInventario(request);

        // Assert: se comprueba que el stock cambio de 20 a 45 y que se guardo.
        assertThat(resultado.getIdInventory()).isEqualTo(5L);
        assertThat(resultado.getStockDisponible()).isEqualTo(45);
        verify(inventoryRepository).save(inventoryExistente);
    }

    // Prueba 5: verifica el escenario de error al eliminar un inventario inexistente.
    @Test
    void eliminarInventarioDebeFallarCuandoNoExisteProductoYSucursal() {
        // Arrange: se simula que no existe inventario para producto 99 y sucursal 1.
        when(inventoryRepository.findByIdProductoAndIdSucursal(99L, 1L))
                .thenReturn(Optional.empty());

        // Act y Assert: se espera ResourceNotFoundException con el mensaje definido en el service.
        assertThatThrownBy(() -> inventoryService.eliminarInventario(99L, 1L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Inventario no encontrado para producto: 99 y sucursal: 1");

        // Verifica que no se elimine nada si el inventario no existe.
        verify(inventoryRepository, never()).delete(any(Inventory.class));
    }

    // Metodo auxiliar para crear inventarios de prueba y evitar repetir codigo en cada test.
    private Inventory crearInventory(Long idInventory, Long idProducto, Long idSucursal, Integer stockDisponible) {
        return Inventory.builder()
                .idInventory(idInventory)
                .idProducto(idProducto)
                .idSucursal(idSucursal)
                .stockDisponible(stockDisponible)
                .fechaActualizacion(LocalDateTime.now())
                .build();
    }
}