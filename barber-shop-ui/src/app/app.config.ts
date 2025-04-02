import {
    ApplicationConfig,
    provideZoneChangeDetection,
    importProvidersFrom,
} from '@angular/core';
import { provideRouter } from '@angular/router';

import { routes } from './app.routes';
import { provideAnimationsAsync } from '@angular/platform-browser/animations/async';
import { provideHttpClient, withInterceptors } from '@angular/common/http';
import { provideNgxMask } from 'ngx-mask';
import { InMemoryWebApiModule } from 'angular-in-memory-web-api';
import { environment } from '../environments/environment';
import { MockDataService } from './services/mock/mock-data.service';

import { LoadingService } from './services/loading.service';
import { httpLoadingInterceptor } from './interceptors/http-loading.interceptor';

export const appConfig: ApplicationConfig = {
    providers: [
        provideZoneChangeDetection({ eventCoalescing: true }),
        provideRouter(routes),
        provideAnimationsAsync(),
        provideHttpClient(withInterceptors([httpLoadingInterceptor])),
        provideNgxMask({}),
        LoadingService,
    ],
};
