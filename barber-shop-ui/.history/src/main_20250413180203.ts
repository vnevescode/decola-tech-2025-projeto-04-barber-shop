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

import { CoreModule } from './app/core/core.module';

bootstrapApplication(AppComponent, {
  providers: [
    provideAnimations(),
    provideHttpClient(withInterceptorsFromDi()), // ✅ correto para interceptors
    provideRouter(routes),
    importProvidersFrom(CoreModule), // ✅ garante DI de guards, services, etc
  ],
}).catch((err) => console.error(err));
