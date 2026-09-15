import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Commande } from '../models/commande.model';

@Injectable({
  providedIn: 'root'
})d4
export class CommandesService {

  private apiUrl = 'http://localhost:3000/commandes';

  constructor(private http: HttpClient) {}

  getAll(): Observable<Commande[]> {
    return this.http.get<Commande[]>(this.apiUrl);
  }

  getById(id: number): Observable<Commande> {
    return this.http.get<Commande>(`${this.apiUrl}/${id}`);
  }

  create(data: Commande): Observable<Commande> {
    return this.http.post<Commande>(this.apiUrl, data);
  }

  update(id: number, data: Commande): Observable<Commande> {
    return this.http.put<Commande>(`${this.apiUrl}/${id}`, data);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
