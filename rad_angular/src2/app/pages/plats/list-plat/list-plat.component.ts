import { Component, OnInit } from '@angular/core';
import { PlatService } from '../../../services/plats.service';

@Component({
  selector: 'app-list-plat',
  templateUrl: './list-plat.component.html',
  styleUrls: ['./list-plat.component.css']
})
export class ListPlatComponent implements OnInit {

  plats: any[] = [];

  constructor(private platService: PlatService) {}

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    this.platService.findAll().subscribe(res => {
      this.plats = res;
    });
  }

  delete(id: number): void {
    if (confirm("Supprimer ce plat ?")) {
      this.platService.delete(id).subscribe(() => {
        this.load();
      });
    }
  }
}
