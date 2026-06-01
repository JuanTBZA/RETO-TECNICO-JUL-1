package com.juan.tirado.reto.service.impl;

import com.juan.tirado.reto.dto.PagedResponseDTO;
import com.juan.tirado.reto.dto.ProductoRequestDTO;
import com.juan.tirado.reto.dto.ProductoResponseDTO;
import com.juan.tirado.reto.exception.CodigoConflictException;
import com.juan.tirado.reto.exception.ProductoNotFoundException;
import com.juan.tirado.reto.mapper.ProductoMapper;
import com.juan.tirado.reto.model.Producto;
import com.juan.tirado.reto.repository.ProductoRepository;
import com.juan.tirado.reto.service.ProductoService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductoServiceImpl implements ProductoService {

    private static final String ESTADO_ACTIVO = "A";

    private final ProductoRepository repository;
    private final ProductoMapper mapper;

    // ── CREAR ─────────────────────────────────────────────────────────────────
    @Override
    @Transactional
    public ProductoResponseDTO crear(ProductoRequestDTO request) {
        validarCodigoUnicoNuevo(request.getCodigo());

        Producto producto = mapper.toEntity(request);
        return mapper.toDTO(repository.save(producto));
    }

    // ── OBTENER POR ID ────────────────────────────────────────────────────────
    @Override
    @Transactional(readOnly = true)
    public ProductoResponseDTO obtenerPorId(Long id) {
        return mapper.toDTO(findActiveOrThrow(id));
    }

    // ── LISTAR CON FILTRO + PAGINACIÓN ────────────────────────────────────────
    @Override
    @Transactional(readOnly = true)
    public PagedResponseDTO<ProductoResponseDTO> listar(String marca, String modelo,
                                                        int page, int size) {
        // Normalizar: string vacío → null para que el filtro OR sea transparente
        String marcaFiltro  = (marca  != null && !marca.trim().isEmpty())  ? marca.trim()  : null;
        String modeloFiltro = (modelo != null && !modelo.trim().isEmpty()) ? modelo.trim() : null;

        Page<Producto> pageResult = repository.listarConFiltro(
                marcaFiltro, modeloFiltro, PageRequest.of(page, size));

        List<ProductoResponseDTO> content = pageResult.getContent()
                .stream()
                .map(mapper::toDTO)
                .collect(Collectors.toList());

        return new PagedResponseDTO<>(
                content,
                pageResult.getNumber(),
                pageResult.getSize(),
                pageResult.getTotalElements(),
                pageResult.getTotalPages()
        );
    }

    // ── ACTUALIZAR ────────────────────────────────────────────────────────────
    @Override
    @Transactional
    public ProductoResponseDTO actualizar(Long id, ProductoRequestDTO request) {
        Producto existente = findActiveOrThrow(id);

        // Código único: sólo validar si cambió
        if (!existente.getCodigo().equalsIgnoreCase(request.getCodigo())) {
            validarCodigoUnicoNuevo(request.getCodigo());
        }

        mapper.updateEntity(existente, request);
        existente.setFechaModif(LocalDateTime.now());
        return mapper.toDTO(repository.save(existente));
    }

    // ── ELIMINAR LÓGICO ───────────────────────────────────────────────────────
    @Override
    @Transactional
    public void eliminarLogico(Long id) {
        Producto producto = findActiveOrThrow(id);
        producto.setEstado("I");
        repository.save(producto);
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private Producto findActiveOrThrow(Long id) {
        return repository.findByIdProductoAndEstado(id, ESTADO_ACTIVO)
                .orElseThrow(() -> new ProductoNotFoundException(id));
    }

    private void validarCodigoUnicoNuevo(String codigo) {
        if (repository.existsByCodigoAndEstado(codigo, ESTADO_ACTIVO)) {
            throw new CodigoConflictException(codigo);
        }
    }
}
