import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormsModule } from '@angular/forms';

// Angular Material Modules
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatSnackBarModule } from '@angular/material/snack-bar';

// Routing Module
import { OnboardingRoutingModule } from './onboarding-routing.module';

// Shared Module
import { SharedModule } from 'src/app/shared/shared.module';

// Components
import { OnboardingComponent } from './onboarding.component';

@NgModule({
  declarations: [
    OnboardingComponent, // Componente principal do onboarding
  ],
  imports: [
    CommonModule,
    ReactiveFormsModule,
    FormsModule,

    // Angular Material
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatSnackBarModule,

    SharedModule, // componentes reutilizáveis (ex.: loader)
    OnboardingRoutingModule, // rotas específicas do módulo
  ],
})
export class OnboardingModule {}
