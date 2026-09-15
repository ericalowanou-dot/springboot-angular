import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Plat } from '../models/plat.model';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class PlatService {

  private apiUrl = `${environment.apiUrl}/plats`;

  constructor(private http: HttpClient) {}

  findAll(): Observable<Plat[]> {
    return this.http.get<Plat[]>(`${this.apiUrl}/find_all`);
  }

  findById(id: number): Observable<Plat> {
    return this.http.get<Plat>(`${this.apiUrl}/find_by_id/${id}`);
  }

  save(data: Plat): Observable<Plat> {
    return this.http.post<Plat>(`${this.apiUrl}/save`, data);
  }

  update(data: Plat): Observable<Plat> {
    return this.http.put<Plat>(`${this.apiUrl}/update/${data.idPlat}`, data);
  }

  delete(id: number): Observable<Plat> {
    return this.http.delete<Plat>(`${this.apiUrl}/delete/${id}`);
  }
}
