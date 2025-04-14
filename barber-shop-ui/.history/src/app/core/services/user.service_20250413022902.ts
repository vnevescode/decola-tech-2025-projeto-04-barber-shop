import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface RegisterUserRequest {
  email: string;
  password: string;
}

export interface UserResponse {
  id: string;
  email: string;
  role: 'ROLE_USER' | 'ROLE_ADMIN';
}

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
