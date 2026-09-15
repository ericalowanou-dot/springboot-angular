import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { PlatService } from '../../../services/plats.service';

@Component({
  selector: 'app-update-plat',
  templateUrl: './update-plat.component.html',
  styleUrls: ['./update-plat.component.css']
})
export class UpdatePlatComponent implements OnInit {

  plat: any = {};

  constructor(
    private route: ActivatedRoute,
    private platService: PlatService,
    private router: Router
  ) {}

  ngOnInit(): void {
    let id = this.route.snapshot.params['id'];

    this.platService.findById(id).subscribe(res => {
      this.plat = res;
    });
  }

  update(): void {
    this.platService.update(this.plat).subscribe(() => {
      this.router.navigate(['/plats']);
    });
  }
}
