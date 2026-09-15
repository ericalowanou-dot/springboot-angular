import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { ClientsService } from '../../../services/clients.service';

@Component({
  selector: 'app-create-client',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './create-client.component.html',
})
export class CreateClientComponent {
  client: any = {};

  constructor(
    private clientService: ClientsService,
    private router: Router
  ) {}

  save() {
    this.clientService.save(this.client).subscribe(() => {
      this.router.navigate(['/clients']);
    });
  }
}
