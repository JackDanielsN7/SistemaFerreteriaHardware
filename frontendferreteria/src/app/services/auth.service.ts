import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable, of } from 'rxjs';
import { catchError, map, tap } from 'rxjs/operators';
import { API_CONFIG } from '../config/api.config';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private apiUrl = API_CONFIG.authUrl;
  private currentUserSubject = new BehaviorSubject<any>(null);
  public currentUser$ = this.currentUserSubject.asObservable();

  constructor(private http: HttpClient) {
    const user = localStorage.getItem('currentUser');
    if (user) {
      try {
        this.currentUserSubject.next(JSON.parse(user));
      } catch {
        localStorage.removeItem('currentUser');
      }
    }
  }

  login(usuario: string, password: string): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}/login`, { usuario, password }).pipe(tap(res => {
      if (!res.token) {
        return;
      }
      localStorage.setItem('token', res.token);
      const rol = res.roles?.[0]?.replace('ROLE_', '') ?? 'VENDEDOR';
      const sesion = {
        id: res.id,
        usuario: res.usuario,
        nombre: `${res.nombre} ${res.apellido}`.trim(),
        rol,
        roles: res.roles
      };
      localStorage.setItem('currentUser', JSON.stringify(sesion));
      this.currentUserSubject.next(sesion);
    }));
  }

  checkServerHealth(): Observable<boolean> {
    return this.http.get(API_CONFIG.healthUrl).pipe(map(() => true), catchError(() => of(false)));
  }

  logout() {
    localStorage.removeItem('token');
    localStorage.removeItem('currentUser');
    this.currentUserSubject.next(null);
  }

  getToken(): string | null {
    const token = localStorage.getItem('token');
    if (!token) {
      return null;
    }
    try {
      const payload = JSON.parse(atob(token.split('.')[1]));
      if (payload.exp && payload.exp < Math.floor(Date.now() / 1000)) {
        this.logout();
        return null;
      }
    } catch {
      this.logout();
      return null;
    }
    return token;
  }

  getCurrentUser(): any {
    return this.currentUserSubject.value;
  }

  isLoggedIn(): boolean {
    return !!this.getToken();
  }

  hasRole(role: string): boolean {
    return this.getCurrentUser()?.rol === role;
  }
}
