CREATE TABLE IF NOT EXISTS pedidos (
    id_pedido BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_usuario BIGINT NOT NULL,
    fecha_creacion DATETIME NOT NULL,
    estado VARCHAR(50) NOT NULL
);

CREATE TABLE IF NOT EXISTS items_pedido (
    id_item BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_pedido BIGINT NOT NULL,
    id_producto BIGINT NOT NULL,
    cantidad INT NOT NULL,
    precio DOUBLE NOT NULL,

    CONSTRAINT fk_item_pedido
        FOREIGN KEY (id_pedido)
        REFERENCES pedidos(id_pedido)
        ON DELETE CASCADE
);
