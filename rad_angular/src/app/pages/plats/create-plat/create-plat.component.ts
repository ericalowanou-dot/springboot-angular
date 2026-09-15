import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { PlatService } from '../../../services/plats.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-create-plat',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './create-plat.component.html',
  styleUrls: ['./create-plat.component.css']
})
export class CreatePlatComponent {

  plat: any = {
    nom: "",
    prix: 0
  };

  constructor(private platService: PlatService, private router: Router) {}

  save(): void {
    this.platService.save(this.plat).subscribe(() => {
      this.router.navigate(['/plats']);
    });
  }
}
