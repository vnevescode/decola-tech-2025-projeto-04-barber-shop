import { bootstrapApplication } from '@angular/platform-browser';
import { provideAnimations } from '@angular/platform-browser/animations';
import { provideHttpClient, withInterceptors } from '@angular/common/http';
import { provideRouter } from '@angular/router';

import { AppComponent } from './app/app.component';
import { routes } from './app/app.routes';

import { AuthInterceptor } from './app/core/interceptors/auth.interceptor';
import { HttpErrorInterceptor } from './app/core/interceptors/http-error.interceptor';
import { LoaderInterceptor } from './app/core/interceptors/loader.interceptor';

bootstrapApplication(AppComponent, {
  providers: [
    provideAnimations(),
    provideHttpClient(
      withInterceptors([
        AuthInterceptor,
        HttpErrorInterceptor,
        LoaderInterceptor,
      ])
    ),
    provideRouter(routes),
  ],
}).catch((err) => console.error(err));
