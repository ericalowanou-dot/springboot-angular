import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Router } from '@angular/router';
import { MenusService } from '../../../services/menus.service';

@Component({
  selector: 'app-list-menu',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './list-menu.component.html',
})
export class ListMenuComponent implements OnInit {
  menus: any[] = [];

  constructor(
    private menuService: MenusService,
    private router: Router
  ) {}

  ngOnInit() {
    this.menuService.findAll().subscribe(res => (this.menus = res));
  }

  edit(id: number) {
    this.router.navigate(['/menus/update', id]);
  }

  delete(id: number) {
    if (confirm('Supprimer ce menu ?')) {
      this.menuService.delete(id).subscribe(() => this.ngOnInit());
    }
  }
}
