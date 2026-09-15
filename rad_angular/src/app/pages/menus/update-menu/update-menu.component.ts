import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { MenusService } from '../../../services/menus.service';

@Component({
  selector: 'app-update-menu',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './update-menu.component.html',
})
export class UpdateMenuComponent implements OnInit {
  menu: any = {};

  constructor(
    private route: ActivatedRoute,
    private menuService: MenusService,
    private router: Router
  ) {}

  ngOnInit() {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    this.menuService.findById(id).subscribe(res => {
      this.menu = res;
    });
  }

  update() {
    this.menuService.update(this.menu).subscribe(() => {
      this.router.navigate(['/menus']);
    });
  }
}
