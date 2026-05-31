
-- PRUEBAS

INSERT INTO PRODUCTO (CODIGO, NOMBRE, MARCA, MODELO, PRECIO, STOCK)
VALUES ('PROD-001', 'Laptop UltraBook Pro', 'Dell', 'XPS 15 9530', 5499.99, 20);

INSERT INTO PRODUCTO (CODIGO, NOMBRE, MARCA, MODELO, PRECIO, STOCK)
VALUES ('PROD-002', 'Monitor Curvo 34"', 'LG', '34WP65G-B', 1299.90, 15);

INSERT INTO PRODUCTO (CODIGO, NOMBRE, MARCA, MODELO, PRECIO, STOCK)
VALUES ('PROD-003', 'Teclado Mecánico RGB', 'Logitech', 'G915 TKL', 649.00, 50);

INSERT INTO PRODUCTO (CODIGO, NOMBRE, MARCA, MODELO, PRECIO, STOCK)
VALUES ('PROD-004', 'Mouse Inalámbrico', 'Logitech', 'MX Master 3', 399.90, 80);

INSERT INTO PRODUCTO (CODIGO, NOMBRE, MARCA, MODELO, PRECIO, STOCK)
VALUES ('PROD-005', 'Auriculares Noise Cancelling', 'Sony', 'WH-1000XM5', 1099.00, 30);


-- CREAR PRODUCTO

DECLARE
    v_id  PRODUCTO.ID_PRODUCTO%TYPE;
    v_cod NUMBER;
    v_msg VARCHAR2(500);
BEGIN
    PKG_PRODUCTO.SP_CREAR_PRODUCTO(
        p_codigo    => 'PROD-006',
        p_nombre    => 'Laptop UltraBook Pro',
        p_marca     => 'Dell',
        p_modelo    => 'XPS 15 9530',
        p_precio    => 5499.99,
        p_stock     => 20,
        p_id_nuevo  => v_id,
        p_error_cod => v_cod,
        p_error_msg => v_msg
    );
    IF v_cod = 0 THEN
        DBMS_OUTPUT.PUT_LINE('Creado con ID: ' || v_id);
    ELSE
        DBMS_OUTPUT.PUT_LINE('Error (' || v_cod || '): ' || v_msg);
    END IF;
END;


--ACTUALIZAR PRODUCTO

DECLARE
    v_cod NUMBER;
    v_msg VARCHAR2(500);
BEGIN
    PKG_PRODUCTO.SP_ACTUALIZAR_PRODUCTO(
        p_id_producto => 1,
        p_codigo      => 'PROD-001',
        p_nombre      => 'Laptop UltraBook Pro MAX',
        p_marca       => 'Dell',
        p_modelo      => 'XPS 15 9530',
        p_precio      => 5999.99,
        p_stock       => 15,
        p_error_cod   => v_cod,
        p_error_msg   => v_msg
    );
    IF v_cod = 0 THEN
        DBMS_OUTPUT.PUT_LINE('Actualizado correctamente.');
    ELSE
        DBMS_OUTPUT.PUT_LINE('Error (' || v_cod || '): ' || v_msg);
    END IF;
END;


-- ELIMINAR LOGICO

DECLARE
    v_cod NUMBER;
    v_msg VARCHAR2(500);
BEGIN
    PKG_PRODUCTO.SP_ELIMINAR_LOGICO_PRODUCTO(
        p_id_producto => 1,
        p_error_cod   => v_cod,
        p_error_msg   => v_msg
    );
    IF v_cod = 0 THEN
        DBMS_OUTPUT.PUT_LINE('Eliminado lógicamente (ESTADO=I).');
    ELSE
        DBMS_OUTPUT.PUT_LINE('Error (' || v_cod || '): ' || v_msg);
    END IF;
END;

-- OBTENER POR ID


DECLARE
    v_cursor SYS_REFCURSOR;
    v_cod    NUMBER;
    v_msg    VARCHAR2(500);
BEGIN
    PKG_PRODUCTO.SP_OBTENER_PRODUCTO_ID(
        2,
        v_cursor,
        v_cod,
        v_msg
    );

    DBMS_OUTPUT.PUT_LINE('CODIGO ERROR: ' || v_cod);
    DBMS_OUTPUT.PUT_LINE('MENSAJE: ' || v_msg);

    DBMS_SQL.RETURN_RESULT(v_cursor);
END;


-- LISTAR PRODUCTOS SIN FILTROS

DECLARE
    v_cursor SYS_REFCURSOR;
    v_cod    NUMBER;
    v_msg    VARCHAR2(500);
BEGIN
    PKG_PRODUCTO.SP_LISTAR_PRODUCTOS(
        NULL, -- Marca
        NULL, -- Modelo
        v_cursor,
        v_cod,
        v_msg
    );

    DBMS_OUTPUT.PUT_LINE('CODIGO ERROR: ' || v_cod);
    DBMS_OUTPUT.PUT_LINE('MENSAJE: ' || v_msg);

    DBMS_SQL.RETURN_RESULT(v_cursor);
END;



-- LISTAR PRODUCTO SOLO MARCA

DECLARE
    v_cursor SYS_REFCURSOR;
    v_cod    NUMBER;
    v_msg    VARCHAR2(500);
BEGIN
    PKG_PRODUCTO.SP_LISTAR_PRODUCTOS(
        'Dell', -- Marca
        NULL,   -- Modelo
        v_cursor,
        v_cod,
        v_msg
    );

    DBMS_OUTPUT.PUT_LINE('CODIGO ERROR: ' || v_cod);
    DBMS_OUTPUT.PUT_LINE('MENSAJE: ' || v_msg);

    DBMS_SQL.RETURN_RESULT(v_cursor);
END;

-- LISTAR PRODUCTO SOLO POR MODELO

DECLARE
    v_cursor SYS_REFCURSOR;
    v_cod    NUMBER;
    v_msg    VARCHAR2(500);
BEGIN
    PKG_PRODUCTO.SP_LISTAR_PRODUCTOS(
        NULL,   -- Marca
        'WH',  -- Modelo
        v_cursor,
        v_cod,
        v_msg
    );

    DBMS_OUTPUT.PUT_LINE('CODIGO ERROR: ' || v_cod);
    DBMS_OUTPUT.PUT_LINE('MENSAJE: ' || v_msg);

    DBMS_SQL.RETURN_RESULT(v_cursor);
END;

-- LISTAR PRODUCTO MARCA Y MODELO

DECLARE
    v_cursor SYS_REFCURSOR;
    v_cod    NUMBER;
    v_msg    VARCHAR2(500);
BEGIN
    PKG_PRODUCTO.SP_LISTAR_PRODUCTOS(
        'Logitech', -- Marca
        'MX',  -- Modelo
        v_cursor,
        v_cod,
        v_msg
    );

    DBMS_OUTPUT.PUT_LINE('CODIGO ERROR: ' || v_cod);
    DBMS_OUTPUT.PUT_LINE('MENSAJE: ' || v_msg);

    DBMS_SQL.RETURN_RESULT(v_cursor);
END;
