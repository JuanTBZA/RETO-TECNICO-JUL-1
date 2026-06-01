package com.juan.tirado.reto.service;

import com.juan.tirado.reto.dto.PagedResponseDTO;
import com.juan.tirado.reto.dto.ProductoRequestDTO;
import com.juan.tirado.reto.dto.ProductoResponseDTO;
import com.juan.tirado.reto.exception.CodigoConflictException;
import com.juan.tirado.reto.exception.ProductoNotFoundException;
import com.juan.tirado.reto.mapper.ProductoMapper;
import com.juan.tirado.reto.model.Producto;
import com.juan.tirado.reto.repository.ProductoRepository;
import com.juan.tirado.reto.service.impl.ProductoServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductoServiceImplTest {

    @Mock
    private ProductoRepository repository;

    private ProductoService service;

    @BeforeEach
    void setUp() {
        service = new ProductoServiceImpl(repository, new ProductoMapper());
    }

    @Test
    void crearGuardaProductoActivoConCodigoNormalizado() {
        ProductoRequestDTO request = request(" prod001 ", " Laptop ", " Lenovo ", " T14 ");

        when(repository.existsByCodigoAndEstado(" prod001 ", "A")).thenReturn(false);
        when(repository.save(any(Producto.class))).thenAnswer(invocation -> {
            Producto producto = invocation.getArgument(0);
            producto.setIdProducto(1L);
            producto.setEstado("A");
            return producto;
        });

        ProductoResponseDTO response = service.crear(request);

        assertThat(response.getIdProducto()).isEqualTo(1L);
        assertThat(response.getCodigo()).isEqualTo("PROD001");
        assertThat(response.getNombre()).isEqualTo("Laptop");
        assertThat(response.getMarca()).isEqualTo("Lenovo");
        assertThat(response.getModelo()).isEqualTo("T14");
        verify(repository).save(any(Producto.class));
    }

    @Test
    void crearLanzaConflictoCuandoCodigoYaExiste() {
        ProductoRequestDTO request = request("PROD001", "Laptop", "Lenovo", "T14");
        when(repository.existsByCodigoAndEstado("PROD001", "A")).thenReturn(true);

        assertThatThrownBy(() -> service.crear(request))
                .isInstanceOf(CodigoConflictException.class)
                .hasMessageContaining("PROD001");
    }

    @Test
    void obtenerPorIdRetornaProductoActivo() {
        when(repository.findByIdProductoAndEstado(1L, "A")).thenReturn(Optional.of(producto(1L, "PROD001")));

        ProductoResponseDTO response = service.obtenerPorId(1L);

        assertThat(response.getIdProducto()).isEqualTo(1L);
        assertThat(response.getCodigo()).isEqualTo("PROD001");
    }

    @Test
    void obtenerPorIdLanzaNotFoundCuandoNoExisteActivo() {
        when(repository.findByIdProductoAndEstado(99L, "A")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.obtenerPorId(99L))
                .isInstanceOf(ProductoNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void listarNormalizaFiltrosYPaginaResultados() {
        when(repository.listarConFiltro(eq("Lenovo"), eq("T14"), any(PageRequest.class)))
                .thenReturn(new PageImpl<Producto>(Arrays.asList(producto(1L, "PROD001")), PageRequest.of(0, 10), 1));

        PagedResponseDTO<ProductoResponseDTO> response = service.listar(" Lenovo ", " T14 ", 0, 10);

        assertThat(response.getContent()).hasSize(1);
        assertThat(response.getTotalElements()).isEqualTo(1);
        assertThat(response.getPage()).isEqualTo(0);
        assertThat(response.getSize()).isEqualTo(10);
    }

    @Test
    void listarConvierteFiltrosVaciosEnNull() {
        when(repository.listarConFiltro(eq(null), eq(null), any(PageRequest.class)))
                .thenReturn(new PageImpl<Producto>(Collections.emptyList(), PageRequest.of(0, 10), 0));

        PagedResponseDTO<ProductoResponseDTO> response = service.listar(" ", " ", 0, 10);

        assertThat(response.getContent()).isEmpty();
        verify(repository).listarConFiltro(eq(null), eq(null), any(PageRequest.class));
    }

    @Test
    void actualizarGuardaCambiosCuandoProductoExiste() {
        Producto existente = producto(1L, "PROD001");
        ProductoRequestDTO request = request("PROD001", "Laptop Pro", "Lenovo", "T14 Gen 2");

        when(repository.findByIdProductoAndEstado(1L, "A")).thenReturn(Optional.of(existente));
        when(repository.save(existente)).thenReturn(existente);

        ProductoResponseDTO response = service.actualizar(1L, request);

        assertThat(response.getNombre()).isEqualTo("Laptop Pro");
        assertThat(response.getModelo()).isEqualTo("T14 Gen 2");
        assertThat(response.getFechaModif()).isNotNull();
        verify(repository).save(existente);
    }

    @Test
    void actualizarValidaCodigoUnicoCuandoCambia() {
        Producto existente = producto(1L, "PROD001");
        ProductoRequestDTO request = request("PROD002", "Laptop", "Lenovo", "T14");

        when(repository.findByIdProductoAndEstado(1L, "A")).thenReturn(Optional.of(existente));
        when(repository.existsByCodigoAndEstado("PROD002", "A")).thenReturn(true);

        assertThatThrownBy(() -> service.actualizar(1L, request))
                .isInstanceOf(CodigoConflictException.class)
                .hasMessageContaining("PROD002");
    }

    @Test
    void eliminarLogicoMarcaProductoComoInactivo() {
        Producto existente = producto(1L, "PROD001");
        when(repository.findByIdProductoAndEstado(1L, "A")).thenReturn(Optional.of(existente));

        service.eliminarLogico(1L);

        assertThat(existente.getEstado()).isEqualTo("I");
        verify(repository).save(existente);
    }

    private ProductoRequestDTO request(String codigo, String nombre, String marca, String modelo) {
        ProductoRequestDTO request = new ProductoRequestDTO();
        request.setCodigo(codigo);
        request.setNombre(nombre);
        request.setMarca(marca);
        request.setModelo(modelo);
        request.setPrecio(new BigDecimal("3499.90"));
        request.setStock(10);
        return request;
    }

    private Producto producto(Long id, String codigo) {
        Producto producto = new Producto();
        producto.setIdProducto(id);
        producto.setCodigo(codigo);
        producto.setNombre("Laptop");
        producto.setMarca("Lenovo");
        producto.setModelo("T14");
        producto.setPrecio(new BigDecimal("3499.90"));
        producto.setStock(10);
        producto.setEstado("A");
        return producto;
    }
}
