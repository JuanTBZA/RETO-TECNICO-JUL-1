package com.juan.tirado.reto.exception;

public class CodigoConflictException extends RuntimeException {
    public CodigoConflictException(String codigo) {
        super("Ya existe un producto activo con el código: " + codigo);
    }
}
