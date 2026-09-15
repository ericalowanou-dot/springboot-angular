import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { MenusService } from '../../../services/menus.service';

@Component({
  selector: 'app-create-menu',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './create-menu.component.html',
})
export class CreateMenuComponent {
  menu: any = { plats: [] };

  constructor(
    private menuService: MenusService,
    private router: Router
  ) {}

  save() {
    this.menuService.save(this.menu).subscribe(() => {
      this.router.navigate(['/menus']);
    });
  }
}
