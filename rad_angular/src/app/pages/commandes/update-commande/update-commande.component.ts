import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { CommandesService } from '../../../services/commandes.service';

@Component({
  selector: 'app-update-commande',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './update-commande.component.html',
})
export class UpdateCommandeComponent implements OnInit {
  commande: any = {};

  constructor(
    private route: ActivatedRoute,
    private commandeService: CommandesService,
    private router: Router
  ) {}

  ngOnInit() {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    this.commandeService.findById(id).subscribe(res => {
      this.commande = res;
    });
  }

  update() {
    this.commandeService.update(this.commande).subscribe(() => {
      this.router.navigate(['/commandes']);
    });
  }
}
