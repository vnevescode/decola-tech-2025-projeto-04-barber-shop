import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import {
  FormBuilder,
  FormGroup,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { Router } from '@angular/router';

import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatIconModule } from '@angular/material/icon';

import { ClientService } from '../../core/services/client.service';

@Component({
  selector: 'app-onboarding',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatSnackBarModule,
    MatIconModule,
  ],
  templateUrl: './onboarding.component.html',
  styleUrls: ['./onboarding.component.scss'],
})
export class OnboardingComponent implements OnInit {
  clientForm!: FormGroup;

  constructor(
    private fb: FormBuilder,
    private clientService: ClientService,
    private snackBar: MatSnackBar,
    private router: Router
  ) {}

  ngOnInit(): void {
    const userId = localStorage.getItem('userId');

    if (!userId) {
      this.snackBar.open('Usuário não autenticado.', 'Fechar', {
        duration: 3000,
      });
      this.router.navigate(['/auth/login']);
      return;
    }

    // Verifica se já existe um cadastro
    this.clientService.getClientByUserId(userId).subscribe({
      next: (client) => {
        const hasName = !!client?.name?.trim();
        const hasPhone = !!client?.phone?.trim();

        if (hasName && hasPhone) {
          this.router.navigate(['/dashboard']);
        }
      },
      error: (err) => {
        console.error('Erro ao buscar cliente:', err);
      },
    });

    // Inicializa o formulário
    this.clientForm = this.fb.group({
      name: ['', [Validators.required, Validators.minLength(2)]],
      phone: ['', [Validators.required, Validators.pattern(/^[0-9]{10,11}$/)]],
    });
  }

  onSubmit(): void {
    if (this.clientForm.invalid) return;

    const userId = localStorage.getItem('userId');
    if (!userId) {
      this.snackBar.open('Erro: usuário não autenticado.', 'Fechar', {
        duration: 3000,
      });
      this.router.navigate(['/auth/login']);
      return;
    }

    const payload = {
      ...this.clientForm.value,
      userId: userId,
    };

    this.clientService.createClient(this.clientForm.value).subscribe({
      next: () => {
        this.snackBar.open('Dados salvos com sucesso!', 'Fechar', {
          duration: 3000,
        });
        this.router.navigate(['/dashboard']);
      },
      error: (err) => {
        this.snackBar.open('Erro ao salvar dados. Tente novamente.', 'Fechar', {
          duration: 4000,
        });
        console.error(err);
      },
    });
  }

  goBack(): void {
    this.router.navigate(['/auth/login']);
  }
}
