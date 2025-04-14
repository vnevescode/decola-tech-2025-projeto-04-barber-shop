import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { ScheduleService } from '../../core/services/schedule.service';
import { ConfirmationDialogComponent } from '../dashboard/modal/confirmation-dialog.component';
import { HaircutTypeService } from 'src/app/core/services/haircut-type.service';
import { FormBuilder, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { ScheduleResponse } from 'src/app/core/models/schedule-response.model';
import { HaircutType } from 'src/app/core/models/haircut-type.model';
import { addDays, startOfDay, endOfDay } from 'date-fns';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatSelectModule } from '@angular/material/select';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatInputModule } from '@angular/material/input';

@Component({
  selector: 'app-admin',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatCardModule,
    MatIconModule,
    MatButtonModule,
    MatDialogModule,
    MatSnackBarModule,
    MatFormFieldModule,
    MatSelectModule,
    MatDatepickerModule,
    MatInputModule,
  ],
  templateUrl: './admin.component.html',
  styleUrls: ['./admin.component.scss'],
})
export class AdminComponent implements OnInit {
  private dialog = inject(MatDialog);
  private snackBar = inject(MatSnackBar);

  schedules: ScheduleResponse[] = [];
  haircutTypes: HaircutType[] = [];
  filterForm!: FormGroup;

  constructor(
    private scheduleService: ScheduleService,
    private haircutTypeService: HaircutTypeService,
    private fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.filterForm = this.fb.group({
      confirmed: [false],
      canceled: [false],
      start: [startOfDay(new Date())],
      end: [endOfDay(addDays(new Date(), 7))],
      haircutTypeId: [null],
    });

    this.haircutTypeService.getAll().subscribe((types) => {
      this.haircutTypes = types;
    });

    this.onFilterSubmit(); // inicializa com os dados da semana atual
  }

  loadSchedules(): void {
    this.scheduleService.getAll().subscribe({
      next: (data) => (this.schedules = data),
    });
  }

  onFilterSubmit(): void {
    const { confirmed, canceled, start, end, haircutTypeId } =
      this.filterForm.value;

    if (!confirmed && !canceled) {
      this.snackBar.open(
        'Selecione pelo menos um status: confirmado ou cancelado.',
        'Fechar',
        {
          duration: 3000,
        }
      );
      return;
    }

    if (!haircutTypeId) {
      this.snackBar.open('Selecione um tipo de corte para filtrar.', 'Fechar', {
        duration: 3000,
      });
      return;
    }

    this.scheduleService
      .getAdminFiltered(
        confirmed,
        canceled,
        start.toISOString(),
        end.toISOString(),
        haircutTypeId
      )
      .subscribe({
        next: (schedules) => {
          this.schedules = schedules;
        },
        error: (err) => {
          console.error('Erro ao buscar agendamentos filtrados:', err);
        },
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
