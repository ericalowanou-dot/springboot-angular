import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent {
  identifiant: string = '';
  motDePasse: string = '';

  constructor(private router: Router) {}

  login() {
    // NOTE: l'authentification a été désactivée côté backend pour ce test
    // (tous les endpoints sont en accès libre). On redirige donc directement.
    this.router.navigate(['/clients']);
  }
}
