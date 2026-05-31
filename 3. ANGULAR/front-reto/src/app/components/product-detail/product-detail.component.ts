// src/app/components/product-detail/product-detail.component.ts
import { CommonModule } from '@angular/common';
import { Component, Inject } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatChipsModule } from '@angular/material/chips';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { MatDialogModule } from '@angular/material/dialog';
import { MatIconModule } from '@angular/material/icon';
import { Router } from '@angular/router';
import { Producto } from '../../models/producto.model';

@Component({
  selector: 'app-product-detail',
  standalone: true,
  imports: [CommonModule, MatButtonModule, MatChipsModule, MatDialogModule, MatIconModule],
  templateUrl: './product-detail.component.html'
})
export class ProductDetailComponent {

  constructor(
    @Inject(MAT_DIALOG_DATA) public producto: Producto,
    private dialogRef: MatDialogRef<ProductDetailComponent>,
    private router: Router
  ) {}

  editar(): void {
    this.dialogRef.close();
    this.router.navigate(['/productos/editar', this.producto.idProducto]);
  }
}
