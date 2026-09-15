import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Categorie } from '../models/categorie.model';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class CategorieService {

  private apiUrl = `${environment.apiUrl}/categorie_plats`;

  constructor(private http: HttpClient) {}

  findAll(): Observable<Categorie[]> {
    return this.http.get<Categorie[]>(`${this.apiUrl}/find_all`);
  }

  findById(id: number): Observable<Categorie> {
    return this.http.get<Categorie>(`${this.apiUrl}/find_by_id/${id}`);
  }

  save(data: Categorie): Observable<Categorie> {
    return this.http.post<Categorie>(`${this.apiUrl}/save`, data);
  }

  update(data: Categorie): Observable<Categorie> {
    return this.http.put<Categorie>(`${this.apiUrl}/update/${data.idCategorie}`, data);
  }

  delete(id: number): Observable<Categorie> {
    return this.http.delete<Categorie>(`${this.apiUrl}/delete/${id}`);
  }
}
