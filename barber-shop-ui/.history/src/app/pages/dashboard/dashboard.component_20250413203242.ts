import {
  Component,
  inject,
  OnInit,
  signal,
  computed,
  effect,
} from '@angular/core';
import { CommonModule } from '@angular/common';
import { CalendarModule, DateAdapter, CalendarView } from 'angular-calendar';
import { adapterFactory } from 'angular-calendar/date-adapters/date-fns';
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

import { CalendarCommonModule } from 'angular-calendar';
import { CalendarDateFormatter } from 'angular-calendar';

import { ScheduleService } from '../../core/services/schedule.service';
import { ClientService } from '../../core/services/client.service';
import { RouterModule } from '@angular/router';

import { startOfDay } from 'date-fns';
import { animate, style, transition, trigger } from '@angular/animations';
import { ConfirmationDialogComponent } from './modal/confirmation-dialog.component';
import { MatDialog } from '@angular/material/dialog';

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
    CalendarModule,
  ],
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.scss'],
  animations: [
    trigger('fadeIn', [
      transition(':enter', [
        style({ opacity: 0, transform: 'translateY(20px)' }),
        animate(
          '300ms ease-out',
          style({ opacity: 1, transform: 'translateY(0)' })
        ),
      ]),
    ]),
  ],
})
export class DashboardComponent implements OnInit {
  scheduleForm!: FormGroup;
  selectedDate: Date | null = null;
  availableTimes: string[] = [];
  view: CalendarView = CalendarView.Month;
  viewDate: Date = new Date();

  constructor(
    private fb: FormBuilder,
    private scheduleService: ScheduleService,
    private clientService: ClientService,
    private snackBar: MatSnackBar,
    private dialog: MatDialog
  ) {}

  ngOnInit() {
    this.scheduleForm = this.fb.group({
      time: ['', Validators.required],
      haircutTypeId: ['', Validators.required],
    });
  }

  onDateSelected(date: Date) {
    this.selectedDate = date;
    this.availableTimes = this.generateTimeSlots();
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
