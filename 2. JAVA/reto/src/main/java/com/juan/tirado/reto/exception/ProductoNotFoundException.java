package com.juan.tirado.reto.exception;

//404
public class ProductoNotFoundException extends RuntimeException {
    public ProductoNotFoundException(Long id) {
        super("Producto no encontrado con ID: " + id);
    }
}
