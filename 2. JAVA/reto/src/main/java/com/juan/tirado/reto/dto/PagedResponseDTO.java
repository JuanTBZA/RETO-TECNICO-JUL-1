package com.juan.tirado.reto.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class PagedResponseDTO<T> {
    private List<T> content;
    private int     page;
    private int     size;
    private long    totalElements;
    private int     totalPages;
}
