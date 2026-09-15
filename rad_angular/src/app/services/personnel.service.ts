import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Personnel } from '../models/personnel.model';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class PersonnelService {

  private apiUrl = `${environment.apiUrl}/personnels`;

  constructor(private http: HttpClient) {}

  findAll(): Observable<Personnel[]> {
    return this.http.get<Personnel[]>(`${this.apiUrl}/find_all`);
  }

  findById(id: number): Observable<Personnel> {
    return this.http.get<Personnel>(`${this.apiUrl}/find_by_id/${id}`);
  }

  save(data: Personnel): Observable<Personnel> {
    return this.http.post<Personnel>(`${this.apiUrl}/save`, data);
  }

  update(data: Personnel): Observable<Personnel> {
    return this.http.put<Personnel>(`${this.apiUrl}/update/${data.idPersonnel}`, data);
  }

  delete(id: number): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}/delete/${id}`, {});
  }
}
