# Perfulandia Backend

Plataforma backend para la gestión integral de Perfulandia, desarrollada con
Java y Spring Boot bajo una arquitectura de microservicios.

El sistema separa cada dominio de negocio en un servicio independiente, con su
propia API REST y base de datos. La comunicación se centraliza mediante API
Gateway, mientras Eureka permite registrar y descubrir los servicios disponibles.

## Funcionalidades

- Gestión de usuarios y autenticación mediante JWT.
- Administración de productos y proveedores.
- Control de inventario por producto y sucursal.
- Gestión del carrito de compras.
- Creación y seguimiento de pedidos.
- Registro de pagos y emisión de facturas.
- Gestión de despachos y números de seguimiento.
- Administración de sucursales y comunas.
- Confirmación, consulta y anulación de ventas.

## Arquitectura del Proyecto

El proyecto utiliza una arquitectura de microservicios. Internamente, cada
microservicio está organizado en capas para separar la exposición de endpoints,
la lógica de negocio, el acceso a datos y la persistencia.

### Arquitectura interna de los microservicios

```text
src/main/java/cl/duoc/<microservicio>/
├── client/                 # Comunicación con otros microservicios
├── config/                 # Configuración de Swagger y componentes
├── controller/             # Endpoints de la API REST
├── dto/                    # Objetos de entrada, salida y errores
│   ├── request/            # Datos recibidos por la API
│   └── response/           # Datos entregados al cliente
├── exception/              # Excepciones y manejo global de errores
├── model/                  # Entidades persistidas con JPA
├── repository/             # Acceso a datos con Spring Data JPA
├── security/               # Filtros JWT y reglas de autorización
├── service/                # Lógica y reglas de negocio
└── <Servicio>Application.java
```

La estructura puede variar ligeramente según la responsabilidad de cada
servicio. Por ejemplo, los módulos que no consumen otras APIs no necesitan el
paquete `client`.

### Flujo entre capas

```text
Solicitud HTTP
    │
    ▼
Controller       Recibe la solicitud y valida el DTO
    │
    ▼
Service          Aplica las reglas de negocio
    │
    ▼
Repository       Ejecuta las operaciones de persistencia
    │
    ▼
MySQL            Almacena la información del microservicio
    │
    ▼
DTO Response     Devuelve la respuesta HTTP al cliente
```

### Comunicación entre microservicios

```text
Cliente / Postman / Swagger
└── API Gateway :8080
    ├── Autenticación y Usuarios
    ├── Productos, Proveedores e Inventario
    ├── Carrito y Pedidos
    ├── Facturación y Envíos
    └── Sucursales y Comunas

Eureka Server :8761
└── Registro y descubrimiento de servicios

Cada microservicio
└── Base de datos MySQL independiente
```

### Principios aplicados

- Separación de responsabilidades por capas.
- Separación de dominios mediante microservicios.
- DTOs específicos para solicitudes y respuestas.
- Persistencia independiente por servicio.
- Migraciones de esquema y datos con Flyway.
- Autenticación stateless mediante JWT.
- Registro y descubrimiento con Eureka.
- Enrutamiento centralizado con API Gateway.
- Configuración diferenciada por ambiente.

## Tecnologías

| Categoría | Tecnologías |
| --- | --- |
| Lenguaje y plataforma | Java 21, Spring Boot, Maven |
| APIs | Spring Web MVC, REST, Swagger/OpenAPI |
| Persistencia | Spring Data JPA, Hibernate, MySQL, Flyway |
| Seguridad | Spring Security, JWT |
| Infraestructura | Spring Cloud Gateway, Netflix Eureka, Docker Compose |
| Pruebas | JUnit 5, Mockito, H2 |
| Utilidades | Jakarta Validation, Lombok, Logback |

## Microservicios

| Dominio | Puerto | Ruta base | Base de datos |
| --- | ---: | --- | --- |
| Inventario | 8082 | `/api/v1/inventory` | `db_inventory` |
| Proveedores | 8083 | `/api/v1/proveedores` | `db_proveedores` |
| Productos | 8084 | `/api/v1/productos` | `db_productos` |
| Autenticación | 8085 | `/api/token/v1` | `bd_token_perfulandia` |
| Carrito | 8086 | `/api/v1/carrito` | `db_carrito` |
| Pedidos | 8087 | `/api/v1/pedidos` | `db_pedidos` |
| Facturación | 8088 | `/api/v1/billing` | `db_facturacion` |
| Envíos | 8089 | `/api/v1/envios` | `db_envios` |
| Sucursales | 8090 | `/api/v1/sucursales` | `db_sucursales` |
| Comunas | 8090 | `/api/v1/comunas` | `db_sucursales` |
| Ventas | 8091 | `/api/v1/ventas` | `db_ventas` |
| Usuarios | 8801 | `/api/v1/usuarios` | `user_service_db` |

### Servicios de infraestructura

| Servicio | Puerto | Responsabilidad |
| --- | ---: | --- |
| API Gateway | 8080 | Punto de entrada y enrutamiento |
| Eureka Server | 8761 | Registro y descubrimiento de servicios |

## Flujo general

1. El cliente solicita un token al servicio de autenticación.
2. El servicio valida las credenciales y emite un JWT.
3. El cliente envía el token en las solicitudes protegidas.
4. API Gateway identifica la ruta y localiza el servicio mediante Eureka.
5. El microservicio valida el token y procesa la operación.
6. La capa Service aplica las reglas de negocio.
7. La capa Repository accede a la base de datos correspondiente.
8. El resultado se devuelve como una respuesta HTTP en formato JSON.

## Autenticación

Endpoint de inicio de sesión:

```http
POST http://localhost:8085/api/token/v1/login
Content-Type: application/json
```

```json
{
  "email": "usuario@perfulandia.cl",
  "contrasena": "user123"
}
```

Credenciales de demostración:

| Perfil | Correo | Contraseña |
| --- | --- | --- |
| Usuario | `usuario@perfulandia.cl` | `user123` |
| Administrador | `admin@perfulandia.cl` | `admin123` |

El token debe enviarse en la cabecera:

```http
Authorization: Bearer <token>
```

## API Gateway

El Gateway se encuentra en `http://localhost:8080` y enruta las solicitudes a
los servicios registrados en Eureka.

Ejemplos:

```text
http://localhost:8080/api/token/v1/login
http://localhost:8080/api/v1/usuarios
http://localhost:8080/api/v1/productos
http://localhost:8080/api/v1/proveedores
http://localhost:8080/api/v1/inventory
http://localhost:8080/api/v1/carrito
http://localhost:8080/api/v1/pedidos
http://localhost:8080/api/v1/billing
http://localhost:8080/api/v1/envios
http://localhost:8080/api/v1/sucursales
http://localhost:8080/api/v1/comunas
http://localhost:8080/api/v1/ventas
```

Todas las rutas anteriores, incluida Ventas, están centralizadas en el Gateway.
Cada destino se resuelve por el nombre con que el servicio se registra en Eureka.

## Documentación de APIs

Los microservicios configurados con Springdoc exponen Swagger UI en:

```text
http://localhost:<puerto>/doc/swagger-ui.html
```

Ejemplos:

| Servicio | Swagger |
| --- | --- |
| Inventario | <http://localhost:8082/doc/swagger-ui.html> |
| Proveedores | <http://localhost:8083/doc/swagger-ui.html> |
| Productos | <http://localhost:8084/doc/swagger-ui.html> |
| Autenticación | <http://localhost:8085/doc/swagger-ui.html> |
| Carrito | <http://localhost:8086/doc/swagger-ui.html> |
| Pedidos | <http://localhost:8087/doc/swagger-ui.html> |
| Facturación | <http://localhost:8088/doc/swagger-ui.html> |
| Envíos | <http://localhost:8089/doc/swagger-ui.html> |
| Sucursales | <http://localhost:8090/doc/swagger-ui.html> |
| Usuarios | <http://localhost:8801/doc/swagger-ui.html> |

Para endpoints protegidos, seleccionar **Authorize** y pegar únicamente el JWT.

## Bases de datos

Cada microservicio utiliza una base independiente. El script
`crear_bases_perfulandia.sql` contiene la creación de todas las bases necesarias
para la ejecución local.

```powershell
Get-Content .\crear_bases_perfulandia.sql | mysql -u root -p
```

Las migraciones se encuentran dentro de cada módulo:

```text
src/main/resources/db/migration
```

Flyway crea las tablas, carga los datos iniciales y registra las versiones
aplicadas en `flyway_schema_history`.

## Ejecución local

### Requisitos

- JDK 21.
- Maven 3.9 o Maven Wrapper.
- MySQL 8.

Antes de iniciar los servicios, definir las credenciales mediante variables de
entorno. No se almacenan contraseñas ni claves JWT en el código fuente:

```powershell
$env:SPRING_DATASOURCE_PASSWORD="<password-mysql>"
$env:JWT_SECRET="<clave-jwt-de-al-menos-32-caracteres>"
```

1. Crear las bases de datos con el script incluido.
2. Iniciar `eureka-server`.
3. Iniciar los microservicios requeridos con el perfil `dev`.
4. Iniciar `api-gateway` para consumirlos desde el puerto 8080.

Ejemplo de ejecución de un microservicio:

```powershell
cd proveedor/proveedor
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

Eureka permite comprobar los servicios registrados en:

<http://localhost:8761>

## Pruebas automatizadas

El proyecto contiene pruebas unitarias y de integración distribuidas entre los
microservicios. Según el módulo, se validan:

- lógica de negocio con JUnit y Mockito;
- controladores y respuestas HTTP;
- repositorios y persistencia con H2;
- filtros de seguridad y validación de JWT;
- operaciones CRUD y escenarios de error.

Para ejecutar las pruebas de un servicio:

```powershell
cd <directorio-del-microservicio>
mvn test
```

Los resultados se generan en `target/surefire-reports` dentro de cada módulo.
Antes de la entrega se debe comprobar que las pruebas permanezcan exitosas y que
la cobertura sea al menos 80%, como exige la rúbrica.

## Alcance de la defensa

Para la demostración solicitada en la evaluación se seleccionó el microservicio
**Producto**. Sobre este servicio se preparó el despliegue Docker y se verificaron
pruebas unitarias y de integración, sin convertirlo en el único foco del sistema.

El entorno de defensa definido en `docker-compose.yml` levanta:

- API Gateway;
- Eureka Server;
- Producto Service;
- MySQL con `db_productos`.

```powershell
docker compose up --build -d
docker compose ps
```

Docker Compose lee `MYSQL_ROOT_PASSWORD` y `JWT_SECRET` desde el archivo `.env`,
que no se versiona. Para preparar el entorno por primera vez:

```powershell
Copy-Item .env.example .env
# Editar .env y reemplazar ambos valores antes de iniciar Docker.
```

Comprobaciones principales:

| Recurso | URL |
| --- | --- |
| Gateway | <http://localhost:8080/actuator/health> |
| Eureka | <http://localhost:8761> |
| Swagger de Producto | <http://localhost:8084/doc/swagger-ui.html> |
| Producto por Gateway | <http://localhost:8080/api/v1/productos> |

La suite seleccionada contiene 27 pruebas de Producto: 24 unitarias y 3 de
integración con JPA, H2 y Flyway. Las pruebas cubren Service, Controller,
seguridad JWT, manejo de excepciones, configuración Swagger y Repository.

```powershell
cd producto
mvn test
```

Resultado verificado:

```text
Tests run: 27, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

### Ejecución remota

El mismo entorno puede desplegarse en una máquina remota que disponga de Docker
Compose. Se debe clonar el repositorio, crear `.env` a partir de `.env.example`,
configurar las variables seguras y ejecutar `docker compose up --build -d`.
Los puertos `8080`, `8084` y `8761` deben habilitarse en el firewall de la
plataforma. Esta sección describe el procedimiento; la URL y evidencia del
proveedor remoto deben agregarse cuando el equipo efectüe ese despliegue.

## Estructura del repositorio

```text
.
|-- api-gateway/                 # Enrutamiento centralizado
|-- eureka-server/               # Registro de servicios
|-- UsuarioPerfulandia/          # Usuarios
|-- TokenPerfulandia/            # Autenticación
|-- producto/                    # Productos
|-- proveedor/                   # Proveedores
|-- inventory-service (1)/       # Inventario
|-- carrito-service/             # Carrito
|-- PerfulandiaOrder/            # Pedidos
|-- facturacion-service/         # Facturación
|-- envios-service/              # Envíos
|-- SucursalesPerfulandia/       # Sucursales y comunas
|-- VentasPerfulandia/           # Ventas
|-- mysql/init/                  # Inicialización para Docker
|-- crear_bases_perfulandia.sql  # Bases para ejecución local
`-- docker-compose.yml           # Entorno de la defensa
```

## Equipo

- Polette Agunanna
- Ruth Honorio
- Darling Pinol
