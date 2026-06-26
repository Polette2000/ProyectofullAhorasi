CREATE TABLE pagos_facturacion (
    id_pago BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_usuario BIGINT NOT NULL,
    id_orden BIGINT NOT NULL,
    monto DECIMAL(10,2) NOT NULL,
    metodo VARCHAR(50) NOT NULL,
    estado VARCHAR(20) NOT NULL,
    fecha_registro DATETIME NOT NULL
);

CREATE TABLE facturas_facturacion (
    id_factura BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_usuario BIGINT NOT NULL,
    id_pago BIGINT NOT NULL,
    monto_total DECIMAL(10,2) NOT NULL,
    descripcion VARCHAR(255) NOT NULL,
    estado VARCHAR(20) NOT NULL,
    fecha_emision DATETIME NOT NULL,

    CONSTRAINT fk_factura_pago FOREIGN KEY (id_pago)
        REFERENCES pagos_facturacion(id_pago)
);