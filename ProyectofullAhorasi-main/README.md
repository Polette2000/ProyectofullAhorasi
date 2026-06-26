# Proyecto Full Ahorasi - Perfulandia

Proyecto grupal desarrollado con microservicios Spring Boot para gestionar los procesos principales de Perfulandia: usuarios, autenticacion, productos, proveedores, inventario, carrito, pedidos, facturacion, envios, sucursales y ventas.


## Microservicios

| Servicio | Ruta | Puerto dev | Base de datos |
| --- | --- | ---: | --- |
| Inventario | `inventory-service (1)/inventory-service` | 8082 | `db_inventory` |
| Proveedores | `proveedor/proveedor` | 8083 | `db_proveedores` |
| Productos | `producto/producto` | 8084 | `db_productos` |
| Token | `TokenPerfulandia/TokenPerfulandia/TokenPerfulandia` | 8085 | `bd_token_perfulandia` |
| Carrito | `carrito-service/carrito-service` | 8086 | `db_carrito` |
| Pedidos | `PerfulandiaOrder/PerfulandiaOrder/PerfulandiaOrder` | 8087 | `db_pedidos` |
| Facturacion | `facturacion-service/facturacion-service/Perfulandiabilling` | 8088 | `db_facturacion` |
| Envios | `envios-service/envios-service/Perfulandiashipping` | 8089 | `db_envios` |
| Sucursales | `SucursalesPerfulandia/SucursalesPerfulandia/SucursalesPerfulandia` | 8090 | `db_sucursales` |
| Ventas | `VentasPerfulandia` | 8091 | `db_ventas` |
| Usuarios | `UsuarioPerfulandia/UsuarioPerfulandia/UsuarioPerfulandia` | 8801 | `user_service_db` |

## Requisitos

- Java 21
- Maven Wrapper incluido en cada microservicio
- MySQL local

## Integrantes

- Polette Agunanna
- Ruth Honorio
- Darling Pinol

## Funcionalidades implementadas

- Gestion de usuarios y validacion de login.
- Generacion de token de autenticacion.
- Gestion de productos.
- Gestion de proveedores.
- Gestion de inventario por producto y sucursal.
- Gestion de carrito de compras por usuario.
- Creacion y consulta de pedidos.
- Gestion de pagos y facturas.
- Gestion de envios y numeros de seguimiento.
- Gestion de sucursales y comunas.
- Confirmacion y consulta de ventas.
- Persistencia de datos con MySQL.
- Migraciones de base de datos con Flyway.

## Bases de datos

Antes de levantar los servicios, crear las bases de datos MySQL. El archivo `crear_bases_perfulandia.sql` contiene las sentencias base para crearlas.

Cada microservicio usa Flyway y ejecuta sus migraciones desde:

```text
src/main/resources/db/migration
```

## Ejecucion

Cada microservicio se ejecuta por separado desde su propia carpeta, usando el perfil `dev`.

Ejemplo:

```powershell
cd producto/producto
.\mvnw.cmd spring-boot:run -Dspring-boot.run.profiles=dev
```

Para compilar sin ejecutar tests:

```powershell
.\mvnw.cmd -DskipTests package
```

## Pruebas de endpoints

Los endpoints fueron probados con Postman. Para probarlos, primero se debe levantar el microservicio correspondiente y luego enviar las solicitudes HTTP al puerto configurado.

Ejemplos de URLs base:

```text
http://localhost:8082/api/v1/inventory
http://localhost:8083/api/v1/proveedores
http://localhost:8084/api/v1/productos
http://localhost:8085/api/token/v1
http://localhost:8086/api/v1/carrito
http://localhost:8087/api/v1/pedidos
http://localhost:8088/api/v1/billing
http://localhost:8089/api/v1/envios
http://localhost:8090/api/v1/sucursales
http://localhost:8090/api/v1/comunas
http://localhost:8091/api/v1/ventas
http://localhost:8801/api/v1/usuarios
```

## Estructura del Proyecto

El proyecto esta organizado como un conjunto de microservicios Spring Boot. Cada servicio tiene su propio puerto, base de datos, configuracion, controladores, servicios, repositorios, modelos, DTOs y migraciones Flyway.

```text
ProyectofullAhorasi-main/
├── carrito-service/              # Puerto 8086
├── envios-service/               # Puerto 8089
├── facturacion-service/          # Puerto 8088
├── inventory-service (1)/        # Puerto 8082
├── PerfulandiaOrder/             # Puerto 8087
├── producto/                     # Puerto 8084
├── proveedor/                    # Puerto 8083
├── SucursalesPerfulandia/        # Puerto 8090
├── TokenPerfulandia/             # Puerto 8085
├── UsuarioPerfulandia/           # Puerto 8801
├── VentasPerfulandia/            # Puerto 8091
├── crear_bases_perfulandia.sql
└── README.md
```

