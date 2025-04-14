import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface ClientRequest {
  name: string;
  phone: string;
}

export interface ClientResponse {
  id: string;
  name: string;
  phone: string;
}

@Injectable({ providedIn: 'root' })
export class ClientService {
  private api = 'http://localhost:8084';

  constructor(private http: HttpClient) {}

  createClient(data: ClientRequest): Observable<ClientResponse> {
    return this.http.post<ClientResponse>(`${this.api}/clients`, data);
  }

  getClientByUserId(userId: string): Observable<ClientResponse> {
    return this.http.get<ClientResponse>(`${this.api}/clients/user/${userId}`);
  }
}
