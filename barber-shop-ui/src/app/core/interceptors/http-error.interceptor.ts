import {
  HttpEvent,
  HttpHandler,
  HttpInterceptor,
  HttpRequest,
  HttpErrorResponse,
} from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, catchError, throwError } from 'rxjs';
import { Router } from '@angular/router';
import { MatSnackBar } from '@angular/material/snack-bar';

@Injectable()
export class HttpErrorInterceptor implements HttpInterceptor {
  constructor(private router: Router, private snackBar: MatSnackBar) {}

  intercept(
    req: HttpRequest<any>,
    next: HttpHandler
  ): Observable<HttpEvent<any>> {
    return next.handle(req).pipe(
      catchError((error: HttpErrorResponse) => {
        let errorMessage = 'Erro inesperado. Tente novamente mais tarde.';

        if (error.error instanceof ErrorEvent) {
          // Erro do lado do cliente
          errorMessage = `Erro: ${error.error.message}`;
        } else {
          // Erro do lado do servidor
          switch (error.status) {
            case 0:
              errorMessage = 'Servidor indisponível. Verifique sua conexão.';
              break;
            case 400:
              errorMessage = error.error?.message || 'Requisição inválida.';
              break;
            case 401:
              errorMessage = 'Não autorizado. Faça login novamente.';
              this.router.navigate(['/login']);
              break;
            case 403:
              errorMessage = 'Acesso negado.';
              break;
            case 404:
              errorMessage = 'Recurso não encontrado.';
              break;
            case 409:
              errorMessage =
                error.error?.message || 'Conflito nos dados enviados.';
              break;
            case 500:
              errorMessage = 'Erro interno do servidor.';
              break;
          }
        }

        this.snackBar.open(errorMessage, 'Fechar', {
          duration: 5000,
          horizontalPosition: 'center',
          verticalPosition: 'bottom',
        });

        return throwError(() => error);
      })
    );
  }
}
