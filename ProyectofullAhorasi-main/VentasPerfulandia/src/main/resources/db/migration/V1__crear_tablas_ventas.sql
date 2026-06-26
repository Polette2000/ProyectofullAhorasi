CREATE TABLE ventas (
    id_venta BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_pedido BIGINT NOT NULL,
    id_usuario BIGINT NOT NULL,
    id_pago BIGINT NOT NULL,
    id_factura BIGINT NOT NULL,
    total_venta DOUBLE NOT NULL,
    estado_venta VARCHAR(30) NOT NULL,
    canal_venta VARCHAR(50) NOT NULL,
    fecha_venta DATETIME NOT NULL,
    CONSTRAINT uk_ventas_id_pedido UNIQUE (id_pedido)
);

CREATE TABLE detalles_venta (
    id_detalle_venta BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_venta BIGINT NOT NULL,
    id_producto BIGINT NOT NULL,
    nombre_producto VARCHAR(150) NOT NULL,
    cantidad INT NOT NULL,
    precio_unitario DOUBLE NOT NULL,
    subtotal DOUBLE NOT NULL,
    CONSTRAINT fk_detalles_venta_venta
        FOREIGN KEY (id_venta)
        REFERENCES ventas(id_venta)
        ON DELETE CASCADE
);
