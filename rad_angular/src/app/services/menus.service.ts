import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Menu } from '../models/menu.model';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class MenusService {

  private apiUrl = `${environment.apiUrl}/menus`;

  constructor(private http: HttpClient) {}

  findAll(): Observable<Menu[]> {
    return this.http.get<Menu[]>(`${this.apiUrl}/find_all`);
  }

  findById(id: number): Observable<Menu> {
    return this.http.get<Menu>(`${this.apiUrl}/find_by_id/${id}`);
  }

  save(data: Menu): Observable<Menu> {
    return this.http.post<Menu>(`${this.apiUrl}/save`, data);
  }

  update(data: Menu): Observable<Menu> {
    return this.http.put<Menu>(`${this.apiUrl}/update/${data.idMenu}`, data);
  }

  delete(id: number): Observable<Menu> {
    return this.http.delete<Menu>(`${this.apiUrl}/delete/${id}`);
  }
}
