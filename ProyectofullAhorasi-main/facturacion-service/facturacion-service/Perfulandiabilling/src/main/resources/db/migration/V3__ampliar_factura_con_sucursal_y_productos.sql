ALTER TABLE facturas_facturacion
    ADD COLUMN id_sucursal BIGINT NOT NULL DEFAULT 1;

ALTER TABLE facturas_facturacion
    ADD COLUMN nombre_sucursal VARCHAR(100) NOT NULL DEFAULT 'Sucursal no especificada';

ALTER TABLE facturas_facturacion
    ADD COLUMN cantidad_total INT NOT NULL DEFAULT 0;

CREATE TABLE factura_productos_facturacion (
    id_factura BIGINT NOT NULL,
    id_producto BIGINT NOT NULL,
    nombre_producto VARCHAR(255) NOT NULL,
    cantidad INT NOT NULL,
    precio_unitario DECIMAL(10,2) NOT NULL,
    subtotal DECIMAL(10,2) NOT NULL,

    CONSTRAINT fk_factura_producto_factura FOREIGN KEY (id_factura)
        REFERENCES facturas_facturacion(id_factura)
);
