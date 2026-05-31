-- PACKAGE SPEC

CREATE OR REPLACE PACKAGE PKG_PRODUCTO AS

    TYPE T_CURSOR IS REF CURSOR;

    PROCEDURE SP_CREAR_PRODUCTO (
        p_codigo    IN  PRODUCTO.CODIGO%TYPE,
        p_nombre    IN  PRODUCTO.NOMBRE%TYPE,
        p_marca     IN  PRODUCTO.MARCA%TYPE,
        p_modelo    IN  PRODUCTO.MODELO%TYPE,
        p_precio    IN  PRODUCTO.PRECIO%TYPE,
        p_stock     IN  PRODUCTO.STOCK%TYPE,
        p_id_nuevo  OUT PRODUCTO.ID_PRODUCTO%TYPE,
        p_error_cod OUT NUMBER,
        p_error_msg OUT VARCHAR2
    );

    PROCEDURE SP_ACTUALIZAR_PRODUCTO (
        p_id_producto IN  PRODUCTO.ID_PRODUCTO%TYPE,
        p_codigo      IN  PRODUCTO.CODIGO%TYPE,
        p_nombre      IN  PRODUCTO.NOMBRE%TYPE,
        p_marca       IN  PRODUCTO.MARCA%TYPE,
        p_modelo      IN  PRODUCTO.MODELO%TYPE,
        p_precio      IN  PRODUCTO.PRECIO%TYPE,
        p_stock       IN  PRODUCTO.STOCK%TYPE,
        p_error_cod   OUT NUMBER,
        p_error_msg   OUT VARCHAR2
    );

    PROCEDURE SP_ELIMINAR_LOGICO_PRODUCTO (
        p_id_producto IN  PRODUCTO.ID_PRODUCTO%TYPE,
        p_error_cod   OUT NUMBER,
        p_error_msg   OUT VARCHAR2
    );

    PROCEDURE SP_OBTENER_PRODUCTO_ID (
        p_id_producto IN  PRODUCTO.ID_PRODUCTO%TYPE,
        p_cursor      OUT T_CURSOR,
        p_error_cod   OUT NUMBER,
        p_error_msg   OUT VARCHAR2
    );

    PROCEDURE SP_LISTAR_PRODUCTOS (
        p_marca     IN  PRODUCTO.MARCA%TYPE  DEFAULT NULL,
        p_modelo    IN  PRODUCTO.MODELO%TYPE DEFAULT NULL,
        p_cursor    OUT T_CURSOR,
        p_error_cod OUT NUMBER,
        p_error_msg OUT VARCHAR2
    );

END PKG_PRODUCTO;



-- PACKAGE BODY

CREATE OR REPLACE PACKAGE BODY PKG_PRODUCTO AS

    C_OK          CONSTANT NUMBER := 0;
    C_ERR_NEGOCIO CONSTANT NUMBER := -1;
    C_ERR_SISTEMA CONSTANT NUMBER := -99;

    -- SP_CREAR_PRODUCTO

    PROCEDURE SP_CREAR_PRODUCTO (
        p_codigo    IN  PRODUCTO.CODIGO%TYPE,
        p_nombre    IN  PRODUCTO.NOMBRE%TYPE,
        p_marca     IN  PRODUCTO.MARCA%TYPE,
        p_modelo    IN  PRODUCTO.MODELO%TYPE,
        p_precio    IN  PRODUCTO.PRECIO%TYPE,
        p_stock     IN  PRODUCTO.STOCK%TYPE,
        p_id_nuevo  OUT PRODUCTO.ID_PRODUCTO%TYPE,
        p_error_cod OUT NUMBER,
        p_error_msg OUT VARCHAR2
    ) AS
        v_existe NUMBER;
    BEGIN
        p_error_cod := C_OK;
        p_error_msg := NULL;

        IF p_precio < 0 THEN
            p_error_cod := C_ERR_NEGOCIO;
            p_error_msg := 'El precio no puede ser negativo.';
            RETURN;
        END IF;

        IF p_stock < 0 THEN
            p_error_cod := C_ERR_NEGOCIO;
            p_error_msg := 'El stock no puede ser negativo.';
            RETURN;
        END IF;

        -- Codigo unico solo entre productos activos
        SELECT COUNT(1) INTO v_existe
          FROM PRODUCTO
         WHERE CODIGO = p_codigo
           AND ESTADO = 'A';

        IF v_existe > 0 THEN
            p_error_cod := C_ERR_NEGOCIO;
            p_error_msg := 'Ya existe un producto activo con el código: ' || p_codigo;
            RETURN;
        END IF;

        INSERT INTO PRODUCTO (CODIGO, NOMBRE, MARCA, MODELO, PRECIO, STOCK)
        VALUES (p_codigo, p_nombre, p_marca, p_modelo, p_precio, p_stock)
        RETURNING ID_PRODUCTO INTO p_id_nuevo;

        COMMIT;
    EXCEPTION
        WHEN OTHERS THEN
            ROLLBACK;
            p_error_cod := C_ERR_SISTEMA;
            p_error_msg := 'Error al crear producto: ' || SQLERRM;
    END SP_CREAR_PRODUCTO;

    -- SP_ACTUALIZAR_PRODUCTO

    PROCEDURE SP_ACTUALIZAR_PRODUCTO (
        p_id_producto IN  PRODUCTO.ID_PRODUCTO%TYPE,
        p_codigo      IN  PRODUCTO.CODIGO%TYPE,
        p_nombre      IN  PRODUCTO.NOMBRE%TYPE,
        p_marca       IN  PRODUCTO.MARCA%TYPE,
        p_modelo      IN  PRODUCTO.MODELO%TYPE,
        p_precio      IN  PRODUCTO.PRECIO%TYPE,
        p_stock       IN  PRODUCTO.STOCK%TYPE,
        p_error_cod   OUT NUMBER,
        p_error_msg   OUT VARCHAR2
    ) AS
        v_existe        NUMBER;
        v_cod_duplicado NUMBER;
    BEGIN
        p_error_cod := C_OK;
        p_error_msg := NULL;

        SELECT COUNT(1) INTO v_existe
          FROM PRODUCTO
         WHERE ID_PRODUCTO = p_id_producto
           AND ESTADO = 'A';

        IF v_existe = 0 THEN
            p_error_cod := C_ERR_NEGOCIO;
            p_error_msg := 'Producto no encontrado o inactivo. ID: ' || p_id_producto;
            RETURN;
        END IF;

        IF p_precio < 0 THEN
            p_error_cod := C_ERR_NEGOCIO;
            p_error_msg := 'El precio no puede ser negativo.';
            RETURN;
        END IF;

        IF p_stock < 0 THEN
            p_error_cod := C_ERR_NEGOCIO;
            p_error_msg := 'El stock no puede ser negativo.';
            RETURN;
        END IF;

        -- Codigo unico excluyendo el propio registro
        SELECT COUNT(1) INTO v_cod_duplicado
          FROM PRODUCTO
         WHERE CODIGO = p_codigo
           AND ESTADO = 'A'
           AND ID_PRODUCTO <> p_id_producto;

        IF v_cod_duplicado > 0 THEN
            p_error_cod := C_ERR_NEGOCIO;
            p_error_msg := 'El código ' || p_codigo || ' ya está en uso por otro producto.';
            RETURN;
        END IF;

        UPDATE PRODUCTO
           SET CODIGO      = p_codigo,
               NOMBRE      = p_nombre,
               MARCA       = p_marca,
               MODELO      = p_modelo,
               PRECIO      = p_precio,
               STOCK       = p_stock,
               FECHA_MODIF = SYSTIMESTAMP
         WHERE ID_PRODUCTO = p_id_producto;

        COMMIT;
    EXCEPTION
        WHEN OTHERS THEN
            ROLLBACK;
            p_error_cod := C_ERR_SISTEMA;
            p_error_msg := 'Error al actualizar producto: ' || SQLERRM;
    END SP_ACTUALIZAR_PRODUCTO;


    -- SP_ELIMINAR_LOGICO_PRODUCTO

    PROCEDURE SP_ELIMINAR_LOGICO_PRODUCTO (
        p_id_producto IN  PRODUCTO.ID_PRODUCTO%TYPE,
        p_error_cod   OUT NUMBER,
        p_error_msg   OUT VARCHAR2
    ) AS
        v_existe NUMBER;
    BEGIN
        p_error_cod := C_OK;
        p_error_msg := NULL;

        SELECT COUNT(1) INTO v_existe
          FROM PRODUCTO
         WHERE ID_PRODUCTO = p_id_producto
           AND ESTADO = 'A';

        IF v_existe = 0 THEN
            p_error_cod := C_ERR_NEGOCIO;
            p_error_msg := 'Producto no encontrado o ya está inactivo. ID: ' || p_id_producto;
            RETURN;
        END IF;

        UPDATE PRODUCTO
           SET ESTADO      = 'I',
               FECHA_MODIF = SYSTIMESTAMP
         WHERE ID_PRODUCTO = p_id_producto;

        COMMIT;
    EXCEPTION
        WHEN OTHERS THEN
            ROLLBACK;
            p_error_cod := C_ERR_SISTEMA;
            p_error_msg := 'Error al eliminar producto: ' || SQLERRM;
    END SP_ELIMINAR_LOGICO_PRODUCTO;


    -- SP_OBTENER_PRODUCTO_ID

    PROCEDURE SP_OBTENER_PRODUCTO_ID (
        p_id_producto IN  PRODUCTO.ID_PRODUCTO%TYPE,
        p_cursor      OUT T_CURSOR,
        p_error_cod   OUT NUMBER,
        p_error_msg   OUT VARCHAR2
    ) AS
    BEGIN
        p_error_cod := C_OK;
        p_error_msg := NULL;

        OPEN p_cursor FOR
            SELECT ID_PRODUCTO, CODIGO, NOMBRE, MARCA, MODELO,
                   PRECIO, STOCK, ESTADO, FECHA_CREACION, FECHA_MODIF
              FROM PRODUCTO
             WHERE ID_PRODUCTO = p_id_producto
               AND ESTADO = 'A';
    EXCEPTION
        WHEN OTHERS THEN
            p_error_cod := C_ERR_SISTEMA;
            p_error_msg := 'Error al obtener producto: ' || SQLERRM;
    END SP_OBTENER_PRODUCTO_ID;


    -- SP_LISTAR_PRODUCTOS

    PROCEDURE SP_LISTAR_PRODUCTOS (
        p_marca     IN  PRODUCTO.MARCA%TYPE  DEFAULT NULL,
        p_modelo    IN  PRODUCTO.MODELO%TYPE DEFAULT NULL,
        p_cursor    OUT T_CURSOR,
        p_error_cod OUT NUMBER,
        p_error_msg OUT VARCHAR2
    ) AS
    BEGIN
        p_error_cod := C_OK;
        p_error_msg := NULL;

        OPEN p_cursor FOR
            SELECT ID_PRODUCTO, CODIGO, NOMBRE, MARCA, MODELO,
                   PRECIO, STOCK, ESTADO, FECHA_CREACION, FECHA_MODIF
              FROM PRODUCTO
             WHERE ESTADO = 'A'
               AND (p_marca  IS NULL OR UPPER(MARCA)  LIKE '%' || UPPER(p_marca)  || '%')
               AND (p_modelo IS NULL OR UPPER(MODELO) LIKE '%' || UPPER(p_modelo) || '%')
             ORDER BY ID_PRODUCTO DESC;
    EXCEPTION
        WHEN OTHERS THEN
            p_error_cod := C_ERR_SISTEMA;
            p_error_msg := 'Error al listar productos: ' || SQLERRM;
    END SP_LISTAR_PRODUCTOS;

END PKG_PRODUCTO;

