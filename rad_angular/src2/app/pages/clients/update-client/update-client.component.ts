import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ClientsService } from '../../../services/clients.service';
import { ActivatedRoute, Router } from '@angular/router';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-update-client',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './update-client.component.html',
})
export class UpdateClientComponent {
  client: any = {};
  id!: number;

  constructor(
    private route: ActivatedRoute,
    private clientService: ClientsService,
    private router: Router
  ) {}

  ngOnInit() {
    this.id = Number(this.route.snapshot.paramMap.get('id'));
    this.clientService.findById(this.id).subscribe(res => {
      this.client = res;
    });
  }

  update() {
    this.clientService.update(this.client).subscribe(() => {
      this.router.navigate(['/clients']);
    });
  }
}
