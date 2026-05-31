package com.juan.tirado.reto.dto;

import lombok.Data;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class ProductoResponseDTO {
    private Long      idProducto;
    private String    codigo;
    private String    nombre;
    private String    marca;
    private String    modelo;
    private BigDecimal precio;
    private Integer   stock;
    private String    estado;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaModif;
}
