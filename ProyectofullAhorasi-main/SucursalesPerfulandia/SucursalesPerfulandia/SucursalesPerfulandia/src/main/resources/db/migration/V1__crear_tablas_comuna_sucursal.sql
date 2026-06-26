CREATE TABLE IF NOT EXISTS comunas (
    id_comuna INT AUTO_INCREMENT PRIMARY KEY,
    nombre_comuna VARCHAR(100) NOT NULL,
    region VARCHAR(100) NOT NULL
);

CREATE TABLE IF NOT EXISTS sucursales (
    id_sucursal INT AUTO_INCREMENT PRIMARY KEY,
    nombre_sucursal VARCHAR(100) NOT NULL,
    direccion VARCHAR(150) NOT NULL,
    telefono VARCHAR(20) NOT NULL,
    horario_atencion VARCHAR(100) NOT NULL,
    id_comuna INT NOT NULL,

    CONSTRAINT fk_sucursal_comuna
        FOREIGN KEY (id_comuna)
        REFERENCES comunas(id_comuna)
);
