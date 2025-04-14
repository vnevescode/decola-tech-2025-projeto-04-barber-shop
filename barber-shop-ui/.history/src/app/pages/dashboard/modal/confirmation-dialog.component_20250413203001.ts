import { Component, Inject } from '@angular/core';
import {
  MAT_DIALOG_DATA,
  MatDialogModule,
  MatDialogRef,
} from '@angular/material/dialog';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-confirmation-dialog',
  standalone: true,
  imports: [CommonModule, MatDialogModule],
  template: `
    <h2 mat-dialog-title>Confirmar Agendamento</h2>
    <mat-dialog-content>
      <p><strong>Data:</strong> {{ data.startAt }}</p>
      <p><strong>Até:</strong> {{ data.endAt }}</p>
      <p><strong>Tipo de corte:</strong> {{ data.haircutTypeId }}</p>
    </mat-dialog-content>
    <mat-dialog-actions align="end">
      <button mat-button (click)="dialogRef.close(false)">Não</button>
      <button mat-raised-button color="primary" (click)="dialogRef.close(true)">
        Sim
      </button>
    </mat-dialog-actions>
  `,
})
export class ConfirmationDialogComponent {
  constructor(
    @Inject(MAT_DIALOG_DATA) public data: any,
    public dialogRef: MatDialogRef<ConfirmationDialogComponent>
  ) {}
}
