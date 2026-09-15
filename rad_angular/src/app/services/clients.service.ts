import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Client } from '../models/client.model';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class ClientsService {

  private apiUrl = `${environment.apiUrl}/clients`;

  constructor(private http: HttpClient) {}

  findAll(): Observable<Client[]> {
    return this.http.get<Client[]>(`${this.apiUrl}/find_all`);
  }

  findById(id: number): Observable<Client> {
    return this.http.get<Client>(`${this.apiUrl}/find_by_id/${id}`);
  }

  save(data: Client): Observable<Client> {
    return this.http.post<Client>(`${this.apiUrl}/save`, data);
  }

  update(data: Client): Observable<Client> {
    return this.http.put<Client>(`${this.apiUrl}/update/${data.idClient}`, data);
  }

  delete(id: number): Observable<Client> {
    return this.http.delete<Client>(`${this.apiUrl}/delete/${id}`);
  }
}
