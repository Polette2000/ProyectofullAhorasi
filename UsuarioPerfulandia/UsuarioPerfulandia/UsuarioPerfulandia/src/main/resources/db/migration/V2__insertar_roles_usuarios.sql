INSERT INTO rol (nombre_rol) VALUES ('ADMIN');
INSERT INTO rol (nombre_rol) VALUES ('CLIENTE');
INSERT INTO rol (nombre_rol) VALUES ('VENDEDOR');

INSERT INTO usuario (nombre, email, fecha_nacimiento, contrasena, rol_id)
VALUES ('Administrador', 'admin@perfulandia.cl', '1990-01-01', 'admin123', 1);

INSERT INTO usuario (nombre, email, fecha_nacimiento, contrasena, rol_id)
VALUES ('Usuario Cliente', 'usuario@perfulandia.cl', '1995-05-10', 'user123', 2);
