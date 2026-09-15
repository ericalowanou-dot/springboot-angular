import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ClientsService } from '../../../services/clients.service';
import { RouterModule, Router } from '@angular/router';

@Component({
  selector: 'app-list-client',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './list-client.component.html',
})
export class ListClientComponent {
  clients: any[] = [];

  constructor(
    private clientService: ClientsService,
    private router: Router
  ) {}

  ngOnInit() {
    this.clientService.findAll().subscribe(res => this.clients = res);
  }

  edit(id: number) {
    this.router.navigate(['/clients/update', id]);
  }
}
