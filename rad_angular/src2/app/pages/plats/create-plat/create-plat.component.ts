import { Component } from '@angular/core';
import { PlatService } from '../../../services/plats.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-create-plat',
  templateUrl: './create-plat.component.html',
  styleUrls: ['./create-plat.component.css']
})
export class CreatePlatComponent {

  plat = {
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
