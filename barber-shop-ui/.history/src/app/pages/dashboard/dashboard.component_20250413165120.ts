import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ScheduleService } from '../../core/services/schedule.service';
import { MatSnackBar } from '@angular/material/snack-bar';
import { ScheduleRequest } from '../../core/models/schedule-request.model';

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.scss'],
})
export class DashboardComponent implements OnInit{
  scheduleForm!: FormGroup;
  minDate: Date = new Date();

  constructor(
    private fb: FormBuilder,
    private scheduleService: ScheduleService,
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

    const { date, time, haircutTypeId } = this.scheduleForm.value;
    
    const startAt = new Date(date);
    const [hours, minutes] = time.split(':').map(Number);
    startAt.setHours(hours, minutes, 0);

    const endAt = new Date(startAt.getTime() + 30 * 60000); // 30 minutos após

    const scheduleData: ScheduleRequest = {
      startAt: startAt.toISOString(),
      endAt: endAt.toISOString(),
      haircutTypeId,
    };

    this.scheduleService.create(scheduleData).subscribe({
      next: () => {
        this.snackBar.open('Horário agendado com sucesso!', 'Fechar', { duration: 3000 });
        this.scheduleForm.reset();
      },
    });
  }
