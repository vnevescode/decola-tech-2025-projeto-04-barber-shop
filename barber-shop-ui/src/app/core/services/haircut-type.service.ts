import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import { Observable } from 'rxjs';
import { HaircutType } from '../models/haircut-type.model';

@Injectable({
  providedIn: 'root',
})
export class HaircutTypeService {
  private readonly api = environment.apiUrl + '/haircuts';

  constructor(private http: HttpClient) {}

  getAll(): Observable<HaircutType[]> {
    return this.http.get<HaircutType[]>(this.api);
  }
}
