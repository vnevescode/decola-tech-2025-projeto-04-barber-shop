import {
  HttpEvent,
  HttpHandler,
  HttpInterceptor,
  HttpRequest,
} from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { AuthService } from '../services/auth.service';

@Injectable()
export class AuthInterceptor implements HttpInterceptor {
  constructor(private authService: AuthService) {}

  intercept(
    req: HttpRequest<any>,
    next: HttpHandler
  ): Observable<HttpEvent<any>> {
    const isAuthRequest =
      req.url.includes('/auth/login') || req.url.includes('/auth/register');

    if (isAuthRequest) {
      return next.handle(req);
    }

    const token = this.authService.getToken();

    // Clona e insere o token se existir
    const authReq = token
      ? req.clone({
          setHeaders: {
            Authorization: `Bearer ${token}`,
          },
        })
      : req;

    // Lida com erros globais (ex: token inválido/expirado)
    return next.handle(authReq).pipe(
      catchError((error: HttpErrorResponse) => {
        if (error.status === 401 || error.status === 403) {
          this.authService.logout(); // Limpa token e redireciona
          this.router.navigate(['/auth/login']);
        }
        return throwError(() => error);
      })
    );
  }
}
