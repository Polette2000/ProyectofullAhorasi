package cl.duoc.PerfulandiaCart.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cl.duoc.PerfulandiaCart.dto.request.CarritoCreateRequest;
import cl.duoc.PerfulandiaCart.dto.request.CarritoUpdateRequest;
import cl.duoc.PerfulandiaCart.dto.response.ItemCarritoResponse;
import cl.duoc.PerfulandiaCart.service.CarritoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/carrito")
@RequiredArgsConstructor
public class CarritoController {

    private final CarritoService carritoService;

    // Agrega un producto al carrito del usuario y devuelve el item guardado.
    @PostMapping("/{idUsuario}")
    public ItemCarritoResponse agregarItem(
            @PathVariable Long idUsuario,
            @Valid @RequestBody CarritoCreateRequest request) {
        return carritoService.agregarItem(idUsuario, request);
    }

    // Lista los items del carrito de un usuario.
    @GetMapping("/{idUsuario}")
    public List<ItemCarritoResponse> listarCarrito(@PathVariable Long idUsuario) {
        return carritoService.listarCarrito(idUsuario);
    }

    // Actualiza la cantidad de un item del carrito y devuelve el item actualizado.
    @PutMapping("/{idUsuario}/items/{idItemCarrito}")
    public ItemCarritoResponse actualizarItem(
            @PathVariable Long idUsuario,
            @PathVariable Long idItemCarrito,
            @Valid @RequestBody CarritoUpdateRequest request) {
        return carritoService.actualizarItem(idUsuario, idItemCarrito, request);
    }

    // Elimina un item del carrito y devuelve un mensaje de confirmacion.
    @DeleteMapping("/{idUsuario}/items/{idItemCarrito}")
    public String eliminarItem(
            @PathVariable Long idUsuario,
            @PathVariable Long idItemCarrito) {
        carritoService.eliminarItem(idUsuario, idItemCarrito);
        return "Item eliminado correctamente";
    }

}
