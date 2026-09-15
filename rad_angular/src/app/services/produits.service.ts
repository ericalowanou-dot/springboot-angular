import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Produit } from '../models/produit.model';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class ProduitsService {

  private apiUrl = `${environment.apiUrl}/produits`;

  constructor(private http: HttpClient) {}

  findAll(): Observable<Produit[]> {
    return this.http.get<Produit[]>(`${this.apiUrl}/find_all`);
  }

  findById(id: number): Observable<Produit> {
    return this.http.get<Produit>(`${this.apiUrl}/find_by_id/${id}`);
  }

  save(data: Produit): Observable<Produit> {
    return this.http.post<Produit>(`${this.apiUrl}/save`, data);
  }

  update(data: Produit): Observable<Produit> {
    return this.http.put<Produit>(`${this.apiUrl}/update/${data.idProduit}`, data);
  }

  delete(id: number): Observable<Produit> {
    return this.http.delete<Produit>(`${this.apiUrl}/delete/${id}`);
  }
}
