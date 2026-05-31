// src/app/components/product-form/product-form.component.ts
import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { ActivatedRoute, Router } from '@angular/router';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { ProductoService } from '../../services/producto.service';

@Component({
  selector: 'app-product-form',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatButtonModule,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatProgressSpinnerModule,
    MatSnackBarModule
  ],
  templateUrl: './product-form.component.html',
  styleUrls: []
})
export class ProductFormComponent implements OnInit {

  form!: FormGroup;
  isEditMode = false;
  productoId?: number;
  isLoading = false;
  isSaving  = false;

  constructor(
    private fb: FormBuilder,
    private route: ActivatedRoute,
    private router: Router,
    private productoService: ProductoService,
    private snack: MatSnackBar
  ) {}

  ngOnInit(): void {
    this.buildForm();

    this.productoId = Number(this.route.snapshot.paramMap.get('id')) || undefined;
    this.isEditMode = !!this.productoId;

    if (this.isEditMode) {
      this.cargarProducto();
    }
  }

  private buildForm(): void {
    this.form = this.fb.group({
      codigo:  ['', [Validators.required, Validators.maxLength(20)]],
      nombre:  ['', [Validators.required, Validators.maxLength(120)]],
      marca:   ['', [Validators.required, Validators.maxLength(60)]],
      modelo:  ['', [Validators.required, Validators.maxLength(60)]],
      precio:  [null, [Validators.required, Validators.min(0)]],
      stock:   [null, [Validators.required, Validators.min(0)]]
    });
  }

  private cargarProducto(): void {
    this.isLoading = true;
    this.productoService.obtenerPorId(this.productoId!).subscribe({
      next: p => {
        this.form.patchValue(p);
        this.isLoading = false;
      },
      error: () => {
        this.snack.open('Producto no encontrado', 'Cerrar', { duration: 3000 });
        this.router.navigate(['/productos']);
      }
    });
  }

  guardar(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.isSaving = true;
    const payload = this.form.value;

    const request$ = this.isEditMode
      ? this.productoService.actualizar(this.productoId!, payload)
      : this.productoService.crear(payload);

    request$.subscribe({
      next: () => {
        const msg = this.isEditMode ? 'Producto actualizado' : 'Producto creado';
        this.snack.open(msg, 'OK', { duration: 3000 });
        this.router.navigate(['/productos']);
      },
      error: err => {
        const msg = err?.error?.mensaje || 'Error al guardar el producto';
        this.snack.open(msg, 'Cerrar', { duration: 4000 });
        this.isSaving = false;
      }
    });
  }

  cancelar(): void {
    this.router.navigate(['/productos']);
  }

  // helpers para mostrar errores en template
  hasError(field: string, error: string): boolean {
    const ctrl = this.form.get(field);
    return !!(ctrl && ctrl.touched && ctrl.hasError(error));
  }
}
