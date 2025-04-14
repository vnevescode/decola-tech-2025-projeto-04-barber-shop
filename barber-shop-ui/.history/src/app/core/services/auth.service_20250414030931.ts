import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';
import { Router } from '@angular/router';
import { AuthRequest } from '../models/auth-request.model';
import { AuthResponse } from '../models/auth-response.model';
import { environment } from '../../../environments/environment';
import jwtDecode from 'jwt-decode';

export interface JwtPayload {
  sub: string;
  role: 'ROLE_USER' | 'ROLE_ADMIN';
  iat: number;
  exp: number;
}

@Injectable({ providedIn: 'root' })
export class AuthService {
  private api = environment.apiUrl;

  constructor(private http: HttpClient, private router: Router) {}

  login(data: AuthRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.api}/auth/login`, data).pipe(
      tap((res: AuthResponse) => {
        localStorage.setItem('token', res.token);
        localStorage.setItem('userId', res.userId); // necessário para o onboard
        localStorage.setItem('isClient', String(res.isClient));
      })
    );
  }

  register(data: AuthRequest): Observable<{ message: string }> {
    return this.http.post<{ message: string }>(
      `${this.api}/auth/register`,
      data
    );
  }

  logout(): void {
    localStorage.removeItem('token');
    this.router.navigate(['/login']);
  }

  getToken(): string | null {
    return localStorage.getItem('token');
  }

  isAuthenticated(): boolean {
    return !!this.getToken();
  }

  getUserRole(): 'ROLE_USER' | 'ROLE_ADMIN' | null {
    const token = this.getToken();
    if (!token) return null;

    try {
      const decoded = jwtDecode<JwtPayload>(token);
      return decoded?.role || null;
    } catch (e) {
      console.error('Erro ao decodificar token:', e);
      return null;
    }
  }
}
