import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import {
  ReactiveFormsModule,
  FormBuilder,
  FormGroup,
  Validators,
} from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatSelectModule } from '@angular/material/select';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { ScheduleService } from '../../core/services/schedule.service';
import { ClientService } from '../../core/services/client.service';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatSelectModule,
    MatDatepickerModule,
    MatSnackBarModule,
  ],
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.scss'],
})
export class DashboardComponent implements OnInit {
  scheduleForm!: FormGroup;
  minDate: Date = new Date();

  constructor(
    private fb: FormBuilder,
    private scheduleService: ScheduleService,
    private clientService: ClientService,
    private snackBar: MatSnackBar
  ) {}

  ngOnInit() {
    this.scheduleForm = this.fb.group({
      date: [null, Validators.required],
      time: ['', Validators.required],
      haircutTypeId: ['', Validators.required],
    });
  }

  onSubmit() {
    if (this.scheduleForm.invalid) return;

    this.clientService.getClientByUserId('current').subscribe({
      next: (client) => {
        const { date, time, haircutTypeId } = this.scheduleForm.value;
        const startAt = new Date(date);
        const [hours, minutes] = time.split(':').map(Number);
        startAt.setHours(hours, minutes, 0);
        const endAt = new Date(startAt.getTime() + 30 * 60000);

        const scheduleData = {
          startAt: startAt.toISOString(),
          endAt: endAt.toISOString(),
          haircutTypeId,
          clientId: client.id,
        };

        this.scheduleService.create(scheduleData).subscribe({
          next: () => {
            this.snackBar.open('Horário agendado com sucesso!', 'Fechar', {
              duration: 3000,
            });
            this.scheduleForm.reset();
          },
        });
      },
    });
  }
}
