 
CREATE TABLE inventory (
    id_inventory BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_producto BIGINT NOT NULL,
    id_sucursal BIGINT NOT NULL,
    stock_disponible INT NOT NULL,
    fecha_actualizacion DATETIME NOT NULL
);
