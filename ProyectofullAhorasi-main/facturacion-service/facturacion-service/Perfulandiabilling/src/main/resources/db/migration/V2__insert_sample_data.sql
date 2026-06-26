-- Script de datos de prueba para Billing

-- Insertar un pago de ejemplo
INSERT INTO pagos_facturacion 
(id_usuario, id_orden, monto, metodo, estado, fecha_registro)
VALUES 
(1, 1001, 29990.00, 'TARJETA_CREDITO', 'COMPLETADO', NOW());

-- Insertar una factura asociada al pago anterior
INSERT INTO facturas_facturacion 
(id_usuario, id_pago, monto_total, descripcion, estado, fecha_emision)
VALUES 
(1, 1, 29990.00, 'Compra de perfumes en Perfulandia', 'EMITIDA', NOW());