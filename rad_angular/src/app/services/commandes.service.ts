import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Commande } from '../models/commande.model';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class CommandesService {

  private apiUrl = `${environment.apiUrl}/commandes`;

  constructor(private http: HttpClient) {}

  findAll(): Observable<Commande[]> {
    return this.http.get<Commande[]>(`${this.apiUrl}/find_all`);
  }

  findById(id: number): Observable<Commande> {
    return this.http.get<Commande>(`${this.apiUrl}/find_by_id/${id}`);
  }

  save(data: Commande): Observable<Commande> {
    return this.http.post<Commande>(`${this.apiUrl}/save`, data);
  }

  update(data: Commande): Observable<Commande> {
    return this.http.put<Commande>(`${this.apiUrl}/update/${data.idCommande}`, data);
  }

  delete(id: number): Observable<Commande> {
    return this.http.delete<Commande>(`${this.apiUrl}/delete/${id}`);
  }
}
