import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { CommandesService } from '../../../services/commandes.service';

@Component({
  selector: 'app-create-commande',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './create-commande.component.html',
})
export class CreateCommandeComponent {
  commande: any = {};

  constructor(
    private commandeService: CommandesService,
    private router: Router
  ) {}

  save() {
    this.commandeService.save(this.commande).subscribe(() => {
      this.router.navigate(['/commandes']);
    });
  }
}
