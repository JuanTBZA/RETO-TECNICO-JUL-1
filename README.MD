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

### Diagrama de la tabla PRODUCTOS
![Tabla PRODUCTO](docs/img/oracle/tabla.png)


