import { Routes } from '@angular/router';
import { LoginComponent } from './login/login';
import { AuthGuard } from './services/login/auth-guard';
import { ShellComponent } from './layout/shell';
import {
  CategoriasPage, ClientesPage, DashboardPage, InventarioPage, MarcasPage,
  MovimientosPage, ProductosPage, UsuariosPage, VentasPage
} from './pages/pages';

export const routes: Routes = [
  { path: '', pathMatch: 'full', redirectTo: 'login' },
  { path: 'login', component: LoginComponent },
  {
    path: 'app',
    component: ShellComponent,
    canActivate: [AuthGuard],
    children: [
      { path: '', component: DashboardPage },
      { path: 'productos', component: ProductosPage },
      { path: 'categorias', component: CategoriasPage },
      { path: 'marcas', component: MarcasPage },
      { path: 'inventario', component: InventarioPage },
      { path: 'movimientos', component: MovimientosPage },
      { path: 'ventas', component: VentasPage },
      { path: 'clientes', component: ClientesPage },
      { path: 'usuarios', component: UsuariosPage, canActivate: [AuthGuard], data: { role: 'ADMIN' } }
    ]
  },
  { path: '**', redirectTo: 'login' }
];
