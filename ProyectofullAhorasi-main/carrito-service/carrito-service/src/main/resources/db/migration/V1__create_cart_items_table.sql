CREATE TABLE carrito_items (
    id_item BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_producto BIGINT NOT NULL,
    cantidad INT NOT NULL,
    id_usuario BIGINT NOT NULL
);
