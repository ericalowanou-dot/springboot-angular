import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class CategoriesService {

  private apiUrl = 'http://localhost:8080/categorie-plats';

  constructor(private http: HttpClient) {}

  findAll(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/find_all`);
  }

  findById(id: number): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/find_by_id/${id}`);
  }

  save(data: any): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}/save`, data);
  }

  update(data: any): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}/update`, data);
  }

  delete(id: number): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/delete/${id}`, {});
  }
}
