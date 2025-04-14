// src/main.ts
import { bootstrapApplication } from '@angular/platform-browser';
import { provideAnimations } from '@angular/platform-browser/animations';
import {
  provideHttpClient,
  withInterceptorsFromDi,
} from '@angular/common/http';
import { provideRouter } from '@angular/router';
import { importProvidersFrom } from '@angular/core';

import { AppComponent } from './app/app.component';
import { routes } from './app/app.routes';

import { CoreModule } from './app/core/core.module'; // contém services, guards, interceptors
import { AuthInterceptor } from './app/core/interceptors/auth.interceptor';
import { HttpErrorInterceptor } from './app/core/interceptors/http-error.interceptor';
import { LoaderInterceptor } from './app/core/interceptors/loader.interceptor';

bootstrapApplication(AppComponent, {
  providers: [
    provideAnimations(),

    // Provide interceptors manualmente
    provideHttpClient(
      withInterceptorsFromDi(),
      withInterceptors([
        AuthInterceptor,
        HttpErrorInterceptor,
        LoaderInterceptor,
      ])
    ),

    provideRouter(routes),

    // Optional: provide services and guards grouped in CoreModule
    importProvidersFrom(CoreModule),
  ],
}).catch((err) => console.error(err));
