package com.juan.tirado.reto.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.juan.tirado.reto.dto.PagedResponseDTO;
import com.juan.tirado.reto.dto.ProductoRequestDTO;
import com.juan.tirado.reto.dto.ProductoResponseDTO;
import com.juan.tirado.reto.exception.GlobalExceptionHandler;
import com.juan.tirado.reto.exception.ProductoNotFoundException;
import com.juan.tirado.reto.service.ProductoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.Collections;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ProductoControllerTest {

    @Mock
    private ProductoService service;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new ProductoController(service))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void crearRetornaCreatedYProducto() throws Exception {
        when(service.crear(any(ProductoRequestDTO.class))).thenReturn(response(1L));

        mockMvc.perform(post("/api/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idProducto").value(1L))
                .andExpect(jsonPath("$.codigo").value("PROD001"));
    }

    @Test
    void crearRetornaBadRequestCuandoBodyEsInvalido() throws Exception {
        ProductoRequestDTO request = request();
        request.setCodigo("");

		mockMvc.perform(post("/api/productos")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.mensaje", containsString("obligatorio")));
	}

    @Test
    void listarRetornaPagina() throws Exception {
        PagedResponseDTO<ProductoResponseDTO> page = new PagedResponseDTO<ProductoResponseDTO>(
                Collections.singletonList(response(1L)), 0, 10, 1, 1);
        when(service.listar("Lenovo", "T14", 0, 10)).thenReturn(page);

        mockMvc.perform(get("/api/productos")
                        .param("marca", "Lenovo")
                        .param("modelo", "T14")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].idProducto").value(1L))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    void obtenerPorIdRetornaProducto() throws Exception {
        when(service.obtenerPorId(1L)).thenReturn(response(1L));

        mockMvc.perform(get("/api/productos/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idProducto").value(1L));
    }

    @Test
    void obtenerPorIdRetornaNotFoundCuandoNoExiste() throws Exception {
        when(service.obtenerPorId(99L)).thenThrow(new ProductoNotFoundException(99L));

        mockMvc.perform(get("/api/productos/{id}", 99L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensaje", containsString("99")));
    }

    @Test
    void actualizarRetornaProductoActualizado() throws Exception {
        when(service.actualizar(any(Long.class), any(ProductoRequestDTO.class))).thenReturn(response(1L));

        mockMvc.perform(put("/api/productos/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idProducto").value(1L));
    }

    @Test
    void eliminarRetornaNoContent() throws Exception {
        mockMvc.perform(delete("/api/productos/{id}", 1L))
                .andExpect(status().isNoContent());

        verify(service).eliminarLogico(1L);
    }

    private ProductoRequestDTO request() {
        ProductoRequestDTO request = new ProductoRequestDTO();
        request.setCodigo("PROD001");
        request.setNombre("Laptop Lenovo ThinkPad");
        request.setMarca("Lenovo");
        request.setModelo("T14");
        request.setPrecio(new BigDecimal("3499.90"));
        request.setStock(10);
        return request;
    }

    private ProductoResponseDTO response(Long id) {
        return ProductoResponseDTO.builder()
                .idProducto(id)
                .codigo("PROD001")
                .nombre("Laptop Lenovo ThinkPad")
                .marca("Lenovo")
                .modelo("T14")
                .precio(new BigDecimal("3499.90"))
                .stock(10)
                .estado("A")
                .build();
    }
}
