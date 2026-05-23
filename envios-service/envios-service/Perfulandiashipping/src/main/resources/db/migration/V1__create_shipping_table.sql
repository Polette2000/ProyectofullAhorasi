CREATE TABLE envios (
    id_envio BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_orden BIGINT NOT NULL,
    direccion VARCHAR(200) NOT NULL,
    numero_seguimiento VARCHAR(50),
    estado VARCHAR(20) NOT NULL,
    fecha_estimada_inicio DATE NOT NULL,
    fecha_estimada_fin DATE NOT NULL
);
