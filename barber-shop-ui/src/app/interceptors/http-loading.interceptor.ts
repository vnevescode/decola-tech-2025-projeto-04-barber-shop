import { inject } from '@angular/core';
import { HttpInterceptorFn } from '@angular/common/http';
import { LoadingService } from '../services/loading.service';
import { delay, finalize } from 'rxjs';

export const httpLoadingInterceptor: HttpInterceptorFn = (req, next) => {
    const loadingService = inject(LoadingService);
    loadingService.show();
    return next(req).pipe(
        delay(800), // 👈 delay de 2 segundos (apenas para testes!)
        finalize(() => loadingService.hide())
    );
};
