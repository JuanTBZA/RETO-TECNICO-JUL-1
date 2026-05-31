// src/app/models/producto.model.ts

export interface Producto {
  idProducto?: number;
  codigo:      string;
  nombre:      string;
  marca:       string;
  modelo:      string;
  precio:      number;
  stock:       number;
  estado?:     string;
  fechaCreacion?: string;
  fechaModif?:    string;
}

export interface PagedResponse<T> {
  content:       T[];
  page:          number;
  size:          number;
  totalElements: number;
  totalPages:    number;
}

export interface FiltroProducto {
  marca?:  string;
  modelo?: string;
  page:    number;
  size:    number;
}
