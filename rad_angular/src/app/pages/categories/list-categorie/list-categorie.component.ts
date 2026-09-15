import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { Categorie } from '../../../models/categorie.model';
import { CategorieService } from '../../../services/categories.service';

@Component({
  selector: 'app-list-categorie',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './list-categorie.component.html',
  styleUrls: ['./list-categorie.component.scss']
})
export class ListCategorieComponent implements OnInit {

  categories: Categorie[] = [];
  loading = false;
  errorMessage?: string;

  constructor(private categorieService: CategorieService) {}

  ngOnInit(): void {
    this.loadCategories();
  }

  loadCategories(): void {
    this.loading = true;
    this.categorieService.findAll().subscribe({
      next: (data) => {
        this.categories = data;
        this.loading = false;
      },
      error: (err) => {
        console.error('Erreur loading categories', err);
        this.errorMessage = 'Impossible de charger les catégories';
        this.loading = false;
      }
    });
  }

  deleteCategory(id: number): void {
    if (!confirm('Supprimer cette catégorie ?')) return;
    this.categorieService.delete(id).subscribe({
      next: () => this.loadCategories(),
      error: (err) => {
        console.error('Erreur suppression', err);
        alert('Suppression échouée');
      }
    });
  }
}
