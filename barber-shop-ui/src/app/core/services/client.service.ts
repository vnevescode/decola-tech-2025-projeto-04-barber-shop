import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ClientRequest } from '../models/client-request.model';
import { ClientResponse } from '../models/client-response.model';
import { environment } from '../../../environments/environment';

@Injectable({ providedIn: 'root' })
export class ClientService {
  private api = environment.apiUrl;

  constructor(private http: HttpClient) {}

  createClient(data: ClientRequest): Observable<ClientResponse> {
    return this.http.post<ClientResponse>(`${this.api}/clients`, data);
  }

  getClientByUserId(userId: string): Observable<ClientResponse> {
    return this.http.get<ClientResponse>(`${this.api}/clients/user/${userId}`);
  }

  getCurrentClient(): Observable<ClientResponse> {
    return this.http.get<ClientResponse>(`${this.api}/clients/me`);
  }
}
