import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Router } from '@angular/router';
import { CommandesService } from '../../../services/commandes.service';

@Component({
  selector: 'app-list-commande',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './list-commande.component.html',
})
export class ListCommandeComponent {
  commandes: any[] = [];

  constructor(
    private commandeService: CommandesService,
    private router: Router
  ) {}

  ngOnInit() {
    this.commandeService.findAll().subscribe(res => (this.commandes = res));
  }

  edit(id: number) {
    this.router.navigate(['/commandes/update', id]);
  }
}
