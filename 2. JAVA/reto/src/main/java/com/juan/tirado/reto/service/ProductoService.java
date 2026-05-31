package com.juan.tirado.reto.service;


import com.juan.tirado.reto.dto.PagedResponseDTO;
import com.juan.tirado.reto.dto.ProductoRequestDTO;
import com.juan.tirado.reto.dto.ProductoResponseDTO;

public interface ProductoService {

    ProductoResponseDTO crear(ProductoRequestDTO request);

    ProductoResponseDTO obtenerPorId(Long id);

    PagedResponseDTO<ProductoResponseDTO> listar(String marca, String modelo, int page, int size);

    ProductoResponseDTO actualizar(Long id, ProductoRequestDTO request);

    void eliminarLogico(Long id);
}

