import { Component } from '@angular/core';
import { Router, RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { AuthService } from '../services/auth.service';

@Component({
  selector: 'app-shell',
  standalone: true,
  imports: [RouterOutlet, RouterLink, RouterLinkActive],
  template: `
    <div class="shell">
      <aside>
        <p class="brand">Ferretería Hardware</p>
        <a routerLink="/app" routerLinkActive="active" [routerLinkActiveOptions]="{exact:true}">Dashboard</a>
        <a routerLink="/app/productos" routerLinkActive="active">Productos</a>
        <a routerLink="/app/categorias" routerLinkActive="active">Categorías</a>
        <a routerLink="/app/marcas" routerLinkActive="active">Marcas</a>
        <a routerLink="/app/inventario" routerLinkActive="active">Inventario</a>
        <a routerLink="/app/movimientos" routerLinkActive="active">Movimientos</a>
        <a routerLink="/app/ventas" routerLinkActive="active">Ventas</a>
        <a routerLink="/app/clientes" routerLinkActive="active">Clientes</a>
        @if (esAdmin) {
          <a routerLink="/app/usuarios" routerLinkActive="active">Usuarios</a>
        }
        <button type="button" (click)="salir()">Cerrar sesión</button>
        <small>{{ usuario }}</small>
      </aside>
      <main><router-outlet /></main>
    </div>
  `
})
export class ShellComponent {
  constructor(private auth: AuthService, private router: Router) {}
  get esAdmin() { return this.auth.hasRole('ADMIN'); }
  get usuario() { return this.auth.getCurrentUser()?.nombre || ''; }
  salir() { this.auth.logout(); this.router.navigate(['/login']); }
}
