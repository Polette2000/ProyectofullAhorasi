package cl.duoc.PerfulandiaCart.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import cl.duoc.PerfulandiaCart.client.ProductoClient;
import cl.duoc.PerfulandiaCart.dto.request.CarritoCreateRequest;
import cl.duoc.PerfulandiaCart.dto.request.CarritoUpdateRequest;
import cl.duoc.PerfulandiaCart.dto.response.ItemCarritoResponse;
import cl.duoc.PerfulandiaCart.dto.response.ProductoResponse;
import cl.duoc.PerfulandiaCart.exception.ResourceNotFoundException;
import cl.duoc.PerfulandiaCart.model.ItemCarrito;
import cl.duoc.PerfulandiaCart.repository.ItemCarritoRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CarritoService {

    private final ItemCarritoRepository itemCarritoRepository;
    private final ProductoClient productoClient;

    public ItemCarritoResponse agregarItem(Long idUsuario, CarritoCreateRequest request) {
        productoClient.obtenerProductoPorId(request.getIdProducto());

        ItemCarrito item = itemCarritoRepository.findByIdUsuarioAndIdProducto(idUsuario, request.getIdProducto())
                .orElse(ItemCarrito.builder()
                        .idUsuario(idUsuario)
                        .idProducto(request.getIdProducto())
                        .cantidad(0)
                        .build());

        item.setCantidad(item.getCantidad() + request.getCantidad());

        return convertirAResponse(itemCarritoRepository.save(item));
    }

    public List<ItemCarritoResponse> listarCarrito(Long idUsuario) {
        return itemCarritoRepository.findByIdUsuario(idUsuario)
                .stream()
                .map(this::convertirAResponse)
                .collect(Collectors.toList());
    }

    public ItemCarritoResponse actualizarItem(Long idUsuario, Long idItemCarrito, CarritoUpdateRequest request) {
        ItemCarrito item = obtenerItemDelUsuario(idUsuario, idItemCarrito);
        item.setCantidad(request.getCantidad());

        return convertirAResponse(itemCarritoRepository.save(item));
    }

    public void eliminarItem(Long idUsuario, Long idItemCarrito) {
        ItemCarrito item = obtenerItemDelUsuario(idUsuario, idItemCarrito);
        itemCarritoRepository.delete(item);
    }

    private ItemCarrito obtenerItemDelUsuario(Long idUsuario, Long idItemCarrito) {
        return itemCarritoRepository.findByIdItemCarritoAndIdUsuario(idItemCarrito, idUsuario)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Item no encontrado para el usuario indicado"));
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
