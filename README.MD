# Reto Técnico - Parte 1: Oracle Database

## Descripción General

Esta sección corresponde a la **Parte 1 del reto técnico**, enfocada en la creación y gestión de la base de datos Oracle para manejar productos.  
Se implementa:

- La tabla `PRODUCTO` con sus restricciones e índice.
- El paquete `PKG_PRODUCTO` con procedimientos almacenados para operaciones CRUD.
- Scripts de prueba con inserciones y ejecución de procedimientos.

---

## Archivos incluidos

| Archivo | Descripción |
|---------|-------------|
| `01_DDL.sql` | Script de creación de la tabla `PRODUCTO`, restricciones y índice. |
| `02_PKG_PRODUCTO.sql` | Paquete Oracle `PKG_PRODUCTO` con procedimientos para crear, actualizar, eliminar, obtener y listar productos. |
| `03_DML_TEST.sql` | Scripts de prueba: inserción de datos y ejecución de procedimientos del package. |

---

## Estructura de la Tabla PRODUCTO

| Columna | Tipo | Descripción |
|---------|------|-------------|
| `ID_PRODUCTO` | NUMBER(10) | Identificador único, autogenerado. |
| `CODIGO` | VARCHAR2(20) | Código único del producto. |
| `NOMBRE` | VARCHAR2(120) | Nombre del producto. |
| `MARCA` | VARCHAR2(60) | Marca del producto. |
| `MODELO` | VARCHAR2(60) | Modelo del producto. |
| `PRECIO` | NUMBER(10,2) | Precio del producto (>=0). |
| `STOCK` | NUMBER(10) | Stock disponible (>=0). |
| `ESTADO` | CHAR(1) | Estado: `A` activo, `I` inactivo. |
| `FECHA_CREACION` | TIMESTAMP | Fecha de creación del registro (auto). |
| `FECHA_MODIF` | TIMESTAMP | Fecha de última modificación. |

### Restricciones

- **Primary Key:** `ID_PRODUCTO`.
- **Unique Key:** `CODIGO`.
- **Check constraints:** `PRECIO >= 0`, `STOCK >= 0`, `ESTADO IN ('A','I')`.
- **Índice:** `IDX_PRODUCTO_MARCA_MODELO` sobre `MARCA` y `MODELO` para optimizar búsquedas.

---

## Package PKG_PRODUCTO

El paquete contiene los siguientes procedimientos:

| Procedimiento | Función |
|---------------|--------|
| `SP_CREAR_PRODUCTO` | Inserta un producto nuevo validando código único, precio y stock. |
| `SP_ACTUALIZAR_PRODUCTO` | Actualiza los datos de un producto existente validando reglas de negocio. |
| `SP_ELIMINAR_LOGICO_PRODUCTO` | Cambia el estado de un producto a inactivo (`ESTADO='I'`). |
| `SP_OBTENER_PRODUCTO_ID` | Recupera un producto activo por ID. |
| `SP_LISTAR_PRODUCTOS` | Lista productos activos con filtros opcionales por `MARCA` y `MODELO`. |

> Todos los procedimientos retornan un código y mensaje de error (`p_error_cod` y `p_error_msg`) para manejo de errores de negocio y sistema.

---

## Flujo de ejecución

1. **Limpiar la base de datos:** eliminar la tabla si existe (`DROP TABLE PRODUCTO`).
2. **Crear la tabla:** ejecutar `01_DDL.sql`.
3. **Crear el package:** ejecutar `02_PKG_PRODUCTO.sql`.
4. **Insertar datos de prueba y validar procedimientos:** ejecutar `03_DML_TEST.sql`.
5. **Verificar resultados:** consultas a la tabla y ejecución de los procedimientos de prueba.

---

## Reglas de negocio implementadas

- Código único solo para productos activos.
- Precio y stock no pueden ser negativos.
- Solo se permite `ESTADO='A'` o `ESTADO='I'`.
- Fecha de creación automática, fecha de modificación opcional al actualizar.
- Eliminación lógica: `ESTADO` cambia a `I`, nunca se elimina físicamente.

---
## Diagrama

### Diagrama de la tabla PRODUCTO
![Tabla PRODUCTO](docs/img/oracle/tabla.png)


# Reto Técnico - Parte 2: Java 8

API REST construida con Java 8, Spring Boot 2.7.17, Spring Web, Spring Data JPA, Bean Validation, Lombok y Oracle Database.

## Requisitos

- Java 8 o superior para compilar el proyecto configurado con target Java 8.
- Maven.
- Oracle Database disponible en `localhost:1521/FREEPDB1`.
- Usuario Oracle: `MIAPP`.
- Password Oracle: `root123`.

## Configuracion

La aplicacion principal usa Oracle en `src/main/resources/application.yaml`:

```yaml
spring:
  application:
    name: reto
  datasource:
    url: jdbc:oracle:thin:@//localhost:1521/FREEPDB1
    username: MIAPP
    password: root123
    driver-class-name: oracle.jdbc.OracleDriver
  jpa:
    database-platform: org.hibernate.dialect.Oracle12cDialect
    hibernate:
      ddl-auto: update
```

Los tests usan H2 en memoria desde `src/test/resources/application.yaml`, por lo que no dependen de Oracle para ejecutarse.

## Ejecutar

```bash
mvn spring-boot:run
```

La API queda disponible en:

```text
http://localhost:8080
```

## Probar

```bash
mvn test
```

![Tabla PRODUCTO](docs/img/java/tests.png)

Las pruebas cubren:

- Creacion de productos.
- Validacion de codigo unico.
- Busqueda por ID.
- Listado paginado con filtros.
- Actualizacion de productos.
- Eliminacion logica.
- Respuestas HTTP del controlador.
- Validacion de body invalido.
- Manejo de producto no encontrado.

## Endpoints

Base URL:

```text
http://localhost:8080
```

### Crear Producto

```bash
curl --location 'http://localhost:8080/api/productos' \
--header 'Content-Type: application/json' \
--data '{
  "codigo": "PROD001",
  "nombre": "Laptop Lenovo ThinkPad",
  "marca": "Lenovo",
  "modelo": "T14",
  "precio": 3499.90,
  "stock": 10
}'
```

Respuesta esperada: `201 Created`.

![Tabla PRODUCTO](docs/img/java/response01.png)

### Listar Productos

```bash
curl --location 'http://localhost:8080/api/productos'
```

Respuesta esperada: `200 OK`.

![Tabla PRODUCTO](docs/img/java/response02.png)

### Listar Con Paginacion

```bash
curl --location 'http://localhost:8080/api/productos?page=0&size=10'
```

Respuesta esperada: `200 OK`.

![Tabla PRODUCTO](docs/img/java/response03.png)

### Filtrar Por Marca

```bash
curl --location 'http://localhost:8080/api/productos?marca=Lenovo&page=0&size=10'
```

Respuesta esperada: `200 OK`.

![Tabla PRODUCTO](docs/img/java/response04.png)

### Filtrar Por Modelo

```bash
curl --location 'http://localhost:8080/api/productos?modelo=T14&page=0&size=10'
```

Respuesta esperada: `200 OK`.

![Tabla PRODUCTO](docs/img/java/response05.png)

### Filtrar Por Marca Y Modelo

```bash
curl --location 'http://localhost:8080/api/productos?marca=Lenovo&modelo=T14&page=0&size=10'
```

Respuesta esperada: `200 OK`.

![Tabla PRODUCTO](docs/img/java/response06.png)

### Obtener Producto Por ID

```bash
curl --location 'http://localhost:8080/api/productos/1'
```

Respuesta esperada: `200 OK` si existe y esta activo. Si no existe: `404 Not Found`.

![Tabla PRODUCTO](docs/img/java/response07.png)

### Actualizar Producto

```bash
curl --location --request PUT 'http://localhost:8080/api/productos/1' \
--header 'Content-Type: application/json' \
--data '{
  "codigo": "PROD001",
  "nombre": "Laptop Lenovo ThinkPad Actualizada",
  "marca": "Lenovo",
  "modelo": "T14 Gen 2",
  "precio": 3899.90,
  "stock": 15
}'
```

Respuesta esperada: `200 OK`.

![Tabla PRODUCTO](docs/img/java/response08.png)

### Eliminar Producto

```bash
curl --location --request DELETE 'http://localhost:8080/api/productos/1'
```

Respuesta esperada: `204 No Content`.

![Tabla PRODUCTO](docs/img/java/response09.png)

La eliminacion es logica: el producto se marca con estado `I`.

![Tabla PRODUCTO](docs/img/java/response09BD.png)

## Errores

El manejador global devuelve errores con esta estructura:

```json
{
  "timestamp": "2026-05-31T15:00:00",
  "status": 400,
  "error": "Bad Request",
  "mensaje": "Detalle del error"
}
```

Codigos principales:

- `400 Bad Request`: validaciones de entrada o argumentos invalidos.
- `404 Not Found`: producto no encontrado o inactivo.
- `409 Conflict`: codigo duplicado en un producto activo.
- `500 Internal Server Error`: error no controlado.



# Reto Técnico - Parte 3: Angular

Frontend web desarrollado con Angular 17 para la gestion de productos. La aplicacion permite consultar, filtrar, crear, editar, visualizar detalle y eliminar productos consumiendo una API REST.

## Caracteristicas

- Listado de productos en tabla con paginacion.
- Busqueda por marca y modelo.
- Registro de nuevos productos.
- Edicion de productos existentes.
- Visualizacion del detalle de un producto mediante dialogo.
- Eliminacion con dialogo de confirmacion.
- Validaciones de formulario para campos obligatorios, longitudes maximas y valores numericos no negativos.
- Notificaciones de resultado mediante `MatSnackBar`.

## Stack Tecnico

- Angular 17.
- Angular CLI.
- Angular Material.
- TypeScript.
- RxJS.
- SCSS.

## Estructura Principal

```text
src/app/
  components/
    confirm-dialog/
    product-detail/
    product-form/
    product-list/
  models/
    producto.model.ts
  services/
    producto.service.ts
  app.routes.ts
src/environments/
  environment.ts
```

## Funcionalidad

La aplicacion expone las siguientes vistas principales:

- `/productos`: listado de productos con filtros, paginacion y acciones.
- `/productos/nuevo`: formulario para registrar un producto.
- `/productos/editar/:id`: formulario para actualizar un producto existente.

Los campos gestionados para cada producto son:

- `codigo`
- `nombre`
- `marca`
- `modelo`
- `precio`
- `stock`
- `estado`
- `fechaCreacion`
- `fechaModif`

## API Backend

La URL base del backend se configura en `src/environments/environment.ts`:

```ts
export const environment = {
  production: false,
  apiUrl: 'http://localhost:8080/api'
};
```

El servicio `ProductoService` consume el recurso `/productos` y utiliza los siguientes metodos:

- `GET /productos`: lista productos con paginacion y filtros opcionales por `marca` y `modelo`.
- `GET /productos/{id}`: obtiene un producto por identificador.
- `POST /productos`: crea un producto.
- `PUT /productos/{id}`: actualiza un producto.
- `DELETE /productos/{id}`: elimina un producto.

## Requisitos

- Node.js compatible con Angular 17.
- npm.
- Backend disponible en `http://localhost:8080/api` o la URL configurada en `environment.ts`.

## Instalacion

Instala las dependencias del proyecto:

```bash
npm install
```

## Ejecucion en Desarrollo

Inicia el servidor de desarrollo:

```bash
npm start
```

Luego abre la aplicacion en:

```text
http://localhost:4200/
```

La aplicacion se recargara automaticamente cuando se modifiquen archivos fuente.

## Build

Genera la version de produccion:

```bash
npm run build
```

Los artefactos se generaran en:

```text
dist/front-reto/
```

## Desarrollo

Para generar elementos de Angular CLI puedes usar:

```bash
npm run ng -- generate component nombre-componente
```

Para compilar en modo observacion durante desarrollo:

```bash
npm run watch
```

## Notas

- El proyecto usa componentes standalone.
- Las rutas estan definidas en `src/app/app.routes.ts`.
- El cliente HTTP se configura en `src/app/app.config.ts` mediante `provideHttpClient()`.
- Los estilos globales se encuentran en `src/styles.scss`.
