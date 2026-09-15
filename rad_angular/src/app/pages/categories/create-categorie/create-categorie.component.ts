import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule, Router } from '@angular/router';
import { CategorieService } from '../../../services/categories.service';
import { Categorie } from '../../../models/categorie.model';

@Component({
  selector: 'app-create-categorie',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './create-categorie.component.html',
  styleUrls: ['./create-categorie.component.css']
})
export class CreateCategorieComponent {

  categorie: Categorie = {
    idCategorie: 0,
    nom: ''
  };

  constructor(
    private categorieService: CategorieService,
    private router: Router
  ) {}

  save() {
    this.categorieService.save(this.categorie).subscribe(() => {
      this.router.navigate(['/categories']);
    });
  }
}
