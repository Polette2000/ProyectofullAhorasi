package cl.duoc.PerfulandiaCart.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import cl.duoc.PerfulandiaCart.client.ProductoClient;
import cl.duoc.PerfulandiaCart.client.UsuarioClient;
import cl.duoc.PerfulandiaCart.dto.request.CarritoCreateRequest;
import cl.duoc.PerfulandiaCart.dto.request.CarritoUpdateRequest;
import cl.duoc.PerfulandiaCart.dto.response.ItemCarritoResponse;
import cl.duoc.PerfulandiaCart.dto.response.ProductoResponse;
import cl.duoc.PerfulandiaCart.exception.ResourceNotFoundException;
import cl.duoc.PerfulandiaCart.model.ItemCarrito;
import cl.duoc.PerfulandiaCart.repository.ItemCarritoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class CarritoService {

    private final ItemCarritoRepository itemCarritoRepository;
    private final ProductoClient productoClient;
    private final UsuarioClient usuarioClient;

    public ItemCarritoResponse agregarItem(Long idUsuario, CarritoCreateRequest request) {
        log.info("Agregando producto ID: {} al carrito del usuario ID: {}", request.getIdProducto(), idUsuario);
        usuarioClient.obtenerUsuarioPorId(idUsuario);
        productoClient.obtenerProductoPorId(request.getIdProducto());

        ItemCarrito item = itemCarritoRepository.findByIdUsuarioAndIdProducto(idUsuario, request.getIdProducto())
                .orElse(ItemCarrito.builder()
                        .idUsuario(idUsuario)
                        .idProducto(request.getIdProducto())
                        .cantidad(0)
                        .build());

        item.setCantidad(item.getCantidad() + request.getCantidad());

        ItemCarrito saved = itemCarritoRepository.save(item);
        log.info("Item de carrito guardado con ID: {}", saved.getIdItemCarrito());

        return convertirAResponse(saved);
    }

    public List<ItemCarritoResponse> listarCarrito(Long idUsuario) {
        log.info("Buscando carrito del usuario ID: {}", idUsuario);
        usuarioClient.obtenerUsuarioPorId(idUsuario);

        List<ItemCarritoResponse> respuesta = itemCarritoRepository.findByIdUsuario(idUsuario)
                .stream()
                .map(this::convertirAResponse)
                .collect(Collectors.toList());

        log.info("Cantidad de items encontrados en carrito={}", respuesta.size());
        return respuesta;
    }

    public ItemCarritoResponse actualizarItem(Long idUsuario, Long idItemCarrito, CarritoUpdateRequest request) {
        log.info("Actualizando item ID: {} del carrito del usuario ID: {}", idItemCarrito, idUsuario);
        ItemCarrito item = obtenerItemDelUsuario(idUsuario, idItemCarrito);
        item.setCantidad(request.getCantidad());

        ItemCarrito saved = itemCarritoRepository.save(item);
        log.info("Item de carrito ID: {} actualizado correctamente", saved.getIdItemCarrito());

        return convertirAResponse(saved);
    }

    public void eliminarItem(Long idUsuario, Long idItemCarrito) {
        log.info("Eliminando item ID: {} del carrito del usuario ID: {}", idItemCarrito, idUsuario);
        ItemCarrito item = obtenerItemDelUsuario(idUsuario, idItemCarrito);
        itemCarritoRepository.delete(item);
        log.info("Item ID: {} eliminado correctamente del carrito", idItemCarrito);
    }

    private ItemCarrito obtenerItemDelUsuario(Long idUsuario, Long idItemCarrito) {
        return itemCarritoRepository.findByIdItemCarritoAndIdUsuario(idItemCarrito, idUsuario)
                .orElseThrow(() -> {
                    log.warn("Item no encontrado idItemCarrito={} para usuario ID: {}", idItemCarrito, idUsuario);
                    return new ResourceNotFoundException("Item no encontrado para el usuario indicado");
                });
    }

    private ItemCarritoResponse convertirAResponse(ItemCarrito item) {
        ProductoResponse producto = productoClient.obtenerProductoPorId(item.getIdProducto());
        Integer subtotal = producto.getPrecio() * item.getCantidad();

        return ItemCarritoResponse.builder()
                .idItemCarrito(item.getIdItemCarrito())
                .idProducto(item.getIdProducto())
                .nombreProducto(producto.getNombre())
                .precioProducto(producto.getPrecio())
                .cantidad(item.getCantidad())
                .subtotal(subtotal)
                .idUsuario(item.getIdUsuario())
                .build();
    }

}
