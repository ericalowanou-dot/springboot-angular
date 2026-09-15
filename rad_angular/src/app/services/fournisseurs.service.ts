import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Fournisseur } from '../models/fournisseur.model';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class FournisseursService {

  // Attention: le backend expose ce contrôleur au singulier "/fournisseur"
  private apiUrl = `${environment.apiUrl}/fournisseur`;

  constructor(private http: HttpClient) {}

  findAll(): Observable<Fournisseur[]> {
    return this.http.get<Fournisseur[]>(`${this.apiUrl}/find_all`);
  }

  findById(id: number): Observable<Fournisseur> {
    return this.http.get<Fournisseur>(`${this.apiUrl}/find_by_id/${id}`);
  }

  save(data: Fournisseur): Observable<Fournisseur> {
    return this.http.post<Fournisseur>(`${this.apiUrl}/save`, data);
  }

  update(data: Fournisseur): Observable<Fournisseur> {
    return this.http.put<Fournisseur>(`${this.apiUrl}/update/${data.idFournisseur}`, data);
  }

  delete(id: number): Observable<Fournisseur> {
    return this.http.delete<Fournisseur>(`${this.apiUrl}/delete/${id}`);
  }
}
