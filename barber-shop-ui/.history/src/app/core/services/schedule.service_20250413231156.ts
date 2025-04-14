import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { ScheduleRequest } from '../models/schedule-request.model';
import { ScheduleResponse } from '../models/schedule-response.model';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

@Injectable({
  providedIn: 'root',
})
export class ScheduleService {
  private api = environment.apiUrl;

  private readonly apiUrl = this.api + '/schedules';

  constructor(private http: HttpClient) {}

  create(schedule: ScheduleRequest): Observable<ScheduleResponse> {
    return this.http.post<ScheduleResponse>(`${this.api}/schedules`, schedule);
  }

  getAll(): Observable<ScheduleResponse[]> {
    return this.http.get<ScheduleResponse[]>(`${this.api}/schedules`);
  }

  getMonth(start: string, end: string): Observable<ScheduleResponse[]> {
    return this.http.get<ScheduleResponse[]>(`${this.api}/schedules/month`, {
      params: { start, end },
    });
  }

  getClientHistory(clientId: string): Observable<ScheduleResponse[]> {
    return this.http.get<ScheduleResponse[]>(
      `${this.api}/schedules/client/${clientId}/history`
    );
  }

  getClientUpcoming(clientId: string): Observable<ScheduleResponse[]> {
    return this.http.get<ScheduleResponse[]>(
      `${this.api}/schedules/client/${clientId}/upcoming`
    );
  }

  getAdminFiltered(
    confirmed: boolean,
    canceled: boolean,
    start: string,
    end: string,
    haircutTypeId: string
  ): Observable<ScheduleResponse[]> {
    return this.http.get<ScheduleResponse[]>(`${this.apiUrl}/admin-filter`, {
      params: { confirmed, canceled, start, end, haircutTypeId },
    });
  }

  confirm(scheduleId: string): Observable<void> {
    return this.http.patch<void>(`${this.apiUrl}/${scheduleId}/confirm`, {});
  }
}
