import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { API_CONFIG } from '../config/api.config';

@Injectable({ providedIn: 'root' })
export class HardwareApiService {
  private base = API_CONFIG.baseUrl;

  constructor(private http: HttpClient) {}

  dashboard() { return this.http.get<any>(`${this.base}/api/dashboard`); }

  roles() { return this.http.get<any[]>(`${this.base}/api/roles`); }
  usuarios() { return this.http.get<any[]>(`${this.base}/api/usuarios`); }
  crearUsuario(body: any) { return this.http.post(`${this.base}/api/usuarios`, body); }
  actualizarUsuario(id: number, body: any) { return this.http.put(`${this.base}/api/usuarios/${id}`, body); }
  cambiarEstadoUsuario(id: number, estado: string) {
    return this.http.patch(`${this.base}/api/usuarios/${id}/estado`, { estado });
  }

  categorias() { return this.http.get<any[]>(`${this.base}/api/categorias`); }
  crearCategoria(body: any) { return this.http.post(`${this.base}/api/categorias`, body); }
  actualizarCategoria(id: number, body: any) { return this.http.put(`${this.base}/api/categorias/${id}`, body); }
  eliminarCategoria(id: number) { return this.http.delete(`${this.base}/api/categorias/${id}`); }

  marcas() { return this.http.get<any[]>(`${this.base}/api/marcas`); }
  crearMarca(body: any) { return this.http.post(`${this.base}/api/marcas`, body); }
  actualizarMarca(id: number, body: any) { return this.http.put(`${this.base}/api/marcas/${id}`, body); }
  eliminarMarca(id: number) { return this.http.delete(`${this.base}/api/marcas/${id}`); }

  productos(params: Record<string, string> = {}) {
    return this.http.get<any[]>(`${this.base}/api/productos`, { params: new HttpParams({ fromObject: params }) });
  }
  crearProducto(body: any) { return this.http.post(`${this.base}/api/productos`, body); }
  actualizarProducto(id: number, body: any) { return this.http.put(`${this.base}/api/productos/${id}`, body); }
  eliminarProducto(id: number) { return this.http.delete(`${this.base}/api/productos/${id}`); }

  inventario() { return this.http.get<any[]>(`${this.base}/api/inventario`); }
  movimientos(productoId?: number) {
    const params = productoId ? new HttpParams().set('productoId', productoId) : undefined;
    return this.http.get<any[]>(`${this.base}/api/movimientos`, { params });
  }
  registrarMovimiento(body: any) { return this.http.post(`${this.base}/api/movimientos`, body); }

  clientes(params: Record<string, string> = {}) {
    return this.http.get<any[]>(`${this.base}/api/clientes`, { params: new HttpParams({ fromObject: params }) });
  }
  crearCliente(body: any) { return this.http.post(`${this.base}/api/clientes`, body); }
  actualizarCliente(id: number, body: any) { return this.http.put(`${this.base}/api/clientes/${id}`, body); }
  eliminarCliente(id: number) { return this.http.delete(`${this.base}/api/clientes/${id}`); }

  ventas(params: Record<string, string> = {}) {
    return this.http.get<any[]>(`${this.base}/api/ventas`, { params: new HttpParams({ fromObject: params }) });
  }
  registrarVenta(body: any) { return this.http.post(`${this.base}/api/ventas`, body); }
}
