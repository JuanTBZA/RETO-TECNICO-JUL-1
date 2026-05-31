// src/app/components/product-list/product-list.component.ts
import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatDialog } from '@angular/material/dialog';
import { MatDialogModule } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatPaginatorModule, PageEvent } from '@angular/material/paginator';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { MatTableModule } from '@angular/material/table';
import { MatTooltipModule } from '@angular/material/tooltip';
import { Router, RouterLink } from '@angular/router';
import { ProductoService } from '../../services/producto.service';
import { Producto } from '../../models/producto.model';
import { ConfirmDialogComponent } from '../confirm-dialog/confirm-dialog.component';
import { ProductDetailComponent } from '../product-detail/product-detail.component';

@Component({
  selector: 'app-product-list',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    RouterLink,
    MatButtonModule,
    MatCardModule,
    MatDialogModule,
    MatFormFieldModule,
    MatIconModule,
    MatInputModule,
    MatPaginatorModule,
    MatProgressSpinnerModule,
    MatSnackBarModule,
    MatTableModule,
    MatTooltipModule
  ],
  templateUrl: './product-list.component.html',
  styleUrls: []
})
export class ProductListComponent implements OnInit {

  productos: Producto[] = [];
  totalElements = 0;
  pageSize = 10;
  pageIndex = 0;
  isLoading = false;

  filtroMarca  = '';
  filtroModelo = '';

  displayedColumns = ['codigo','nombre','marca','modelo','precio','stock','acciones'];

  constructor(
    private productoService: ProductoService,
    private router: Router,
    private dialog: MatDialog,
    private snack: MatSnackBar
  ) {}

  ngOnInit(): void {
    this.cargar();
  }

  cargar(): void {
    this.isLoading = true;
    this.productoService.listar({
      marca:  this.filtroMarca  || undefined,
      modelo: this.filtroModelo || undefined,
      page:   this.pageIndex,
      size:   this.pageSize
    }).subscribe({
      next: res => {
        this.productos      = res.content;
        this.totalElements  = res.totalElements;
        this.isLoading      = false;
      },
      error: () => {
        this.snack.open('Error al cargar productos', 'Cerrar', { duration: 3000 });
        this.isLoading = false;
      }
    });
  }

  buscar(): void {
    this.pageIndex = 0;
    this.cargar();
  }

  limpiarFiltro(): void {
    this.filtroMarca  = '';
    this.filtroModelo = '';
    this.pageIndex    = 0;
    this.cargar();
  }

  onPage(event: PageEvent): void {
    this.pageIndex = event.pageIndex;
    this.pageSize  = event.pageSize;
    this.cargar();
  }

  verDetalle(producto: Producto): void {
    this.dialog.open(ProductDetailComponent, {
      width: '520px',
      data: producto
    });
  }

  editar(id: number): void {
    this.router.navigate(['/productos/editar', id]);
  }

  confirmarEliminar(producto: Producto): void {
    const ref = this.dialog.open(ConfirmDialogComponent, {
      width: '380px',
      data: { mensaje: `¿Eliminar el producto "${producto.nombre}"?` }
    });

    ref.afterClosed().subscribe((confirmado: boolean) => {
      if (confirmado) {
        this.productoService.eliminar(producto.idProducto!).subscribe({
          next: () => {
            this.snack.open('Producto eliminado correctamente', 'OK', { duration: 3000 });
            this.cargar();
          },
          error: err => {
            const msg = err?.error?.mensaje || 'Error al eliminar el producto';
            this.snack.open(msg, 'Cerrar', { duration: 4000 });
          }
        });
      }
    });
  }
}
