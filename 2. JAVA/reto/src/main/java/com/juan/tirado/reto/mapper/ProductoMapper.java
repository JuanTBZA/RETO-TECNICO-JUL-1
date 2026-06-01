package com.juan.tirado.reto.mapper;

import com.juan.tirado.reto.dto.ProductoRequestDTO;
import com.juan.tirado.reto.dto.ProductoResponseDTO;
import com.juan.tirado.reto.model.Producto;
import org.springframework.stereotype.Component;

@Component
public class ProductoMapper {

    public Producto toEntity(ProductoRequestDTO dto) {
        return updateEntity(new Producto(), dto);
    }

    public Producto updateEntity(Producto producto, ProductoRequestDTO dto) {
        producto.setCodigo(dto.getCodigo().trim().toUpperCase());
        producto.setNombre(dto.getNombre().trim());
        producto.setMarca(dto.getMarca().trim());
        producto.setModelo(dto.getModelo().trim());
        producto.setPrecio(dto.getPrecio());
        producto.setStock(dto.getStock());
        return producto;
    }

    public ProductoResponseDTO toDTO(Producto producto) {
        return ProductoResponseDTO.builder()
                .idProducto(producto.getIdProducto())
                .codigo(producto.getCodigo())
                .nombre(producto.getNombre())
                .marca(producto.getMarca())
                .modelo(producto.getModelo())
                .precio(producto.getPrecio())
                .stock(producto.getStock())
                .estado(producto.getEstado())
                .fechaCreacion(producto.getFechaCreacion())
                .fechaModif(producto.getFechaModif())
                .build();
    }
}
