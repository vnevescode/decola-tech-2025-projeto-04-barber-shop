import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { ScheduleService } from '../../core/services/schedule.service';
import { ConfirmationDialogComponent } from '../dashboard/modal/confirmation-dialog.component';

@Component({
  selector: 'app-admin',
  standalone: true,
  imports: [
    CommonModule,
    MatCardModule,
    MatIconModule,
    MatButtonModule,
    MatDialogModule,
    MatSnackBarModule,
  ],
  templateUrl: './admin.component.html',
  styleUrls: ['./admin.component.scss'],
})
export class AdminComponent implements OnInit {
  private scheduleService = inject(ScheduleService);
  private dialog = inject(MatDialog);
  private snackBar = inject(MatSnackBar);

  schedules: any[] = [];

  ngOnInit(): void {
    this.loadSchedules();
  }

  loadSchedules(): void {
    this.scheduleService.getAll().subscribe({
      next: (data) => (this.schedules = data),
    });
  }

  confirmSchedule(id: string): void {
    const dialogRef = this.dialog.open(ConfirmationDialogComponent, {
      data: {
        title: 'Confirmar Agendamento',
        message: 'Deseja realmente confirmar este agendamento?',
      },
    });

    dialogRef.afterClosed().subscribe((confirmed) => {
      if (confirmed) {
        this.scheduleService.confirm(id).subscribe({
          next: () => {
            this.snackBar.open(
              'Agendamento confirmado com sucesso!',
              'Fechar',
              {
                duration: 3000,
              }
            );
            this.loadSchedules();
          },
          error: () => {
            this.snackBar.open('Erro ao confirmar o agendamento.', 'Fechar', {
              duration: 3000,
            });
          },
        });
      }
    });
  }
}
