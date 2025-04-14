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
import { addMonths, subMonths } from 'date-fns';
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
import { MatIconModule } from '@angular/material/icon';

import { CalendarCommonModule } from 'angular-calendar';
import { CalendarDateFormatter } from 'angular-calendar';

import { ScheduleService } from '../../core/services/schedule.service';
import { ClientService } from '../../core/services/client.service';
import { RouterModule } from '@angular/router';

import { startOfDay } from 'date-fns';
import { animate, style, transition, trigger } from '@angular/animations';
import { ConfirmationDialogComponent } from './modal/confirmation-dialog.component';
import { MatDialog } from '@angular/material/dialog';

import { ScheduleResponse } from '../../core/models/schedule-response.model';
import { endOfMonth, startOfMonth } from 'date-fns';

import { HaircutTypeService } from '../../core/services/haircut-type.service';
import { HaircutType } from '../../core/models/haircut-type.model';

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
    MatIconModule,
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

  monthlySchedules: ScheduleResponse[] = [];
  fullDays: Date[] = [];

  haircutTypes: HaircutType[] = [];

  constructor(
    private fb: FormBuilder,
    private scheduleService: ScheduleService,
    private clientService: ClientService,
    private snackBar: MatSnackBar,
    private dialog: MatDialog,
    private haircutTypeService: HaircutTypeService
  ) {}

  ngOnInit() {
    this.scheduleForm = this.fb.group({
      time: ['', Validators.required],
      haircutTypeId: ['', Validators.required],
    });

    this.loadSchedulesForMonth(this.viewDate);
    this.haircutTypeService.getAll().subscribe((types) => {
      this.haircutTypes = types;
    });
  }

  subMonth(date: Date): Date {
    const newDate = subMonths(date, 1);
    this.loadSchedulesForMonth(newDate);
    return newDate;
  }

  addMonth(date: Date): Date {
    const newDate = addMonths(date, 1);
    this.loadSchedulesForMonth(newDate);
    return newDate;
  }

  loadSchedulesForMonth(date: Date) {
    const start = startOfMonth(date).toISOString();
    const end = endOfMonth(date).toISOString();

    this.scheduleService.getMonth(start, end).subscribe((schedules) => {
      this.monthlySchedules = schedules;
      this.fullDays = this.findFullyBookedDays(schedules);
    });
  }

  findFullyBookedDays(schedules: ScheduleResponse[]): Date[] {
    const dayMap = new Map<string, number>();

    schedules.forEach((s) => {
      const date = s.startAt.split('T')[0];
      const count = dayMap.get(date) ?? 0;
      dayMap.set(date, count + 1);
    });

    const fullyBooked: Date[] = [];
    dayMap.forEach((count, date) => {
      if (count >= 18) {
        fullyBooked.push(new Date(date));
      }
    });

    return fullyBooked;
  }

  isFullyBooked(day: Date): boolean {
    return this.fullDays.some((d) => d.toDateString() === day.toDateString());
  }

  onDateSelected(date: Date) {
    this.selectedDate = date;
    this.availableTimes = this.generateTimeSlots(date);
  }

  generateTimeSlots(date: Date): string[] {
    const startHour = 9;
    const endHour = 18;
    const times: string[] = [];

    for (let hour = startHour; hour < endHour; hour++) {
      times.push(`${hour.toString().padStart(2, '0')}:00`);
      times.push(`${hour.toString().padStart(2, '0')}:30`);
    }

    return times;
  }

  onSubmit() {
    if (!this.selectedDate || this.scheduleForm.invalid) return;

    const { time, haircutTypeId } = this.scheduleForm.value;
    const [hours, minutes] = time.split(':').map(Number);
    const startAt = new Date(this.selectedDate);
    startAt.setHours(hours, minutes, 0);
    const endAt = new Date(startAt.getTime() + 30 * 60000);

    const dialogRef = this.dialog.open(ConfirmationDialogComponent, {
      data: {
        startAt: startAt.toLocaleString(),
        endAt: endAt.toLocaleTimeString(),
        haircutTypeId,
      },
    });

    dialogRef.afterClosed().subscribe((confirmed) => {
      if (confirmed) {
        this.clientService.getClientByUserId('current').subscribe({
          next: (client) => {
            const scheduleData = {
              startAt: startAt.toISOString(),
              endAt: endAt.toISOString(),
              haircutTypeId,
              clientId: client.id,
            };
            this.scheduleService.create(scheduleData).subscribe(() => {
              this.snackBar.open('Agendamento confirmado!', 'Fechar', {
                duration: 3000,
              });
              this.scheduleForm.reset();
              this.selectedDate = null;
              this.loadSchedulesForMonth(this.viewDate);
            });
          },
        });
      }
    });
  }

  getOccupiedTimesForDay(date: Date): string[] {
    return this.monthlySchedules
      .filter((s) => new Date(s.startAt).toDateString() === date.toDateString())
      .map((s) => new Date(s.startAt).toTimeString().substring(0, 5));
  }

  dayModifier: (day: any) => void = (day) => {
    if (this.isFullyBooked(day.date)) {
      day.cssClass = 'fully-booked-day';
    }
  };

  isTimeOccupied(time: string): boolean {
    if (!this.selectedDate) return false;
    return this.getOccupiedTimesForDay(this.selectedDate).includes(time);
  }
}
