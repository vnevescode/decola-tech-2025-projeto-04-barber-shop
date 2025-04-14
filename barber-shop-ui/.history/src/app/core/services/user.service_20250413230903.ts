import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { RegisterUserRequest } from '../models/user-request.model';
import { UserResponse } from '../models/user-response.model';
import { environment } from '../../../environments/environment';

@Injectable({ providedIn: 'root' })
export class UserService {
  private api = 'http://localhost:8084';

  constructor(private http: HttpClient) {}

  registerUser(data: RegisterUserRequest): Observable<UserResponse> {
    return this.http.post<UserResponse>(`${this.api}/users/register`, data);
  }

  getProfile(): Observable<UserResponse> {
    return this.http.get<UserResponse>(`${this.api}/me`); // caso tenha endpoint para perfil
  }
}
