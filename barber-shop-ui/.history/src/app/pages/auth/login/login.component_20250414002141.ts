import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';

// Angular Material
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { MatIconModule } from '@angular/material/icon';
import { MatError } from '@angular/material/form-field';

import { Router } from '@angular/router';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { AuthService } from '../../../core/services/auth.service';
import { AuthRequest } from '../../../core/models/auth-request.model';
import { fadeInScale, shakeAnimation } from '../../../shared/animations';

@Component({
  selector: 'app-login',
  standalone: true,
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss'],
  animations: [fadeInScale, shakeAnimation],
  imports: [
    CommonModule,
    RouterModule,
    FormsModule,
    ReactiveFormsModule,

    // Angular Material
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatSnackBarModule,
    MatIconModule,
  ],
})
export class LoginComponent {
  form!: FormGroup;
  hide = true;
  shake = false;

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router
  ) {
    this.form = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', Validators.required],
    });
  }

  onSubmit() {
    if (this.form.invalid) {
      this.shake = true;

      setTimeout(() => {
        this.shake = false;
      }, 400); // igual ao tempo do shakeAnimation

      return;
    }

    const { email, password } = this.form.value;

    const loginData: AuthRequest = {
      email: email ?? '',
      password: password ?? '',
    };

    this.authService.login(loginData).subscribe({
      next: (res) => {
        console.log('✅ Login sucesso: ', res); // adicione isso!
        if (String(res.isClient) == 'true') {
          this.router.navigate(['/dashboard']);
        } else {
          this.router.navigate(['/onboarding']);
        }
      },
      error: (err) => {
        console.error('Erro no login:', err);
      },
    });
  }
}
