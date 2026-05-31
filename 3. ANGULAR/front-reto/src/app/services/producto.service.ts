// src/app/services/producto.service.ts
import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { FiltroProducto, PagedResponse, Producto } from '../models/producto.model';

@Injectable({ providedIn: 'root' })
export class ProductoService {

  private readonly base = `${environment.apiUrl}/productos`;

  constructor(private http: HttpClient) {}

  listar(filtro: FiltroProducto): Observable<PagedResponse<Producto>> {
    let params = new HttpParams()
      .set('page', filtro.page.toString())
      .set('size', filtro.size.toString());
    if (filtro.marca)  params = params.set('marca',  filtro.marca);
    if (filtro.modelo) params = params.set('modelo', filtro.modelo);
    return this.http.get<PagedResponse<Producto>>(this.base, { params });
  }

  obtenerPorId(id: number): Observable<Producto> {
    return this.http.get<Producto>(`${this.base}/${id}`);
  }

  crear(producto: Producto): Observable<Producto> {
    return this.http.post<Producto>(this.base, producto);
  }

  actualizar(id: number, producto: Producto): Observable<Producto> {
    return this.http.put<Producto>(`${this.base}/${id}`, producto);
  }

  eliminar(id: number): Observable<void> {
    return this.http.delete<void>(`${this.base}/${id}`);
  }
}
