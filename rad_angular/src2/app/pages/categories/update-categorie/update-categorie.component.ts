import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { CategorieService } from '../../../services/categories.service';
import { Categorie } from '../../../models/categorie.model';

@Component({
  selector: 'app-update-categorie',
  templateUrl: './update-categorie.component.html',
  styleUrls: ['./update-categorie.component.css']
})
export class UpdateCategorieComponent implements OnInit {

  categorie: Categorie = {
    idCategorie: 0,
    nom: ''
  };

  constructor(
    private route: ActivatedRoute,
    private categorieService: CategorieService,
    private router: Router
  ) {}

  ngOnInit(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));

    this.categorieService.findById(id).subscribe(data => {
      this.categorie = data;
    });
  }

  update() {
    this.categorieService.update(this.categorie).subscribe(() => {
      this.router.navigate(['/categories']);
    });
  }
}
