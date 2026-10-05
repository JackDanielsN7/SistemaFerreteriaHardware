import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HardwareApiService } from '../services/hardware-api.service';
import { AuthService } from '../services/auth.service';

@Component({
  selector: 'app-dashboard-page',
  standalone: true,
  imports: [CommonModule],
  template: `
    <h1>Dashboard</h1>
    <div class="cards" *ngIf="data">
      <article><span>Total vendido</span><strong>S/ {{ data.totalVentas | number:'1.2-2' }}</strong></article>
      <article><span>Ventas</span><strong>{{ data.cantidadVentas }}</strong></article>
      <article><span>Productos</span><strong>{{ data.productos }}</strong></article>
      <article><span>Clientes</span><strong>{{ data.clientes }}</strong></article>
      <article><span>Stock bajo</span><strong>{{ data.stockBajo }}</strong></article>
    </div>
    <h2>Productos más vendidos</h2>
    <ul><li *ngFor="let item of data?.masVendidos || []">{{ item.nombre }} · {{ item.cantidadVendida }} uds.</li></ul>
    <h2>Stock bajo</h2>
    <ul><li *ngFor="let item of data?.productosStockBajo || []">{{ item.nombre }} · stock {{ item.stock }}</li></ul>
    <p class="error" *ngIf="error">{{ error }}</p>
  `
})
export class DashboardPage implements OnInit {
  data: any;
  error = '';
  constructor(private api: HardwareApiService) {}
  ngOnInit() { this.api.dashboard().subscribe({ next: d => this.data = d, error: () => this.error = 'No se pudo cargar el dashboard.' }); }
}

@Component({
  selector: 'app-categorias-page',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <h1>Categorías</h1>
    <form class="row" (ngSubmit)="guardar()">
      <input [(ngModel)]="form.nombre" name="nombre" placeholder="Nombre" required>
      <input [(ngModel)]="form.descripcion" name="descripcion" placeholder="Descripción">
      <button>{{ form.id ? 'Actualizar' : 'Crear' }}</button>
    </form>
    <table>
      <tr><th>Nombre</th><th>Descripción</th><th></th></tr>
      <tr *ngFor="let item of items">
        <td>{{ item.nombre }}</td><td>{{ item.descripcion }}</td>
        <td><button type="button" (click)="editar(item)">Editar</button>
            <button type="button" (click)="eliminar(item.id)">Eliminar</button></td>
      </tr>
    </table>
    <p class="error" *ngIf="error">{{ error }}</p>
  `
})
export class CategoriasPage implements OnInit {
  items: any[] = [];
  form: any = {};
  error = '';
  constructor(private api: HardwareApiService) {}
  ngOnInit() { this.cargar(); }
  cargar() { this.api.categorias().subscribe(items => this.items = items); }
  editar(item: any) { this.form = { id: item.id, nombre: item.nombre, descripcion: item.descripcion }; }
  guardar() {
    const req = this.form.id ? this.api.actualizarCategoria(this.form.id, this.form) : this.api.crearCategoria(this.form);
    req.subscribe({ next: () => { this.form = {}; this.cargar(); }, error: () => this.error = 'No se pudo guardar la categoría.' });
  }
  eliminar(id: number) { this.api.eliminarCategoria(id).subscribe({ next: () => this.cargar(), error: () => this.error = 'La categoría tiene productos.' }); }
}

@Component({
  selector: 'app-marcas-page',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <h1>Marcas</h1>
    <form class="row" (ngSubmit)="guardar()">
      <input [(ngModel)]="form.nombre" name="nombre" placeholder="Nombre" required>
      <input [(ngModel)]="form.descripcion" name="descripcion" placeholder="Descripción">
      <button>{{ form.id ? 'Actualizar' : 'Crear' }}</button>
    </form>
    <table>
      <tr><th>Nombre</th><th>Descripción</th><th></th></tr>
      <tr *ngFor="let item of items">
        <td>{{ item.nombre }}</td><td>{{ item.descripcion }}</td>
        <td><button type="button" (click)="editar(item)">Editar</button>
            <button type="button" (click)="eliminar(item.id)">Eliminar</button></td>
      </tr>
    </table>
    <p class="error" *ngIf="error">{{ error }}</p>
  `
})
export class MarcasPage implements OnInit {
  items: any[] = [];
  form: any = { estado: 'ACTIVO' };
  error = '';
  constructor(private api: HardwareApiService) {}
  ngOnInit() { this.cargar(); }
  cargar() { this.api.marcas().subscribe(items => this.items = items); }
  editar(item: any) { this.form = { id: item.id, nombre: item.nombre, descripcion: item.descripcion }; }
  guardar() {
    const req = this.form.id ? this.api.actualizarMarca(this.form.id, this.form) : this.api.crearMarca(this.form);
    req.subscribe({ next: () => { this.form = {}; this.cargar(); }, error: () => this.error = 'No se pudo guardar la marca.' });
  }
  eliminar(id: number) { this.api.eliminarMarca(id).subscribe({ next: () => this.cargar(), error: () => this.error = 'La marca tiene productos.' }); }
}

@Component({
  selector: 'app-productos-page',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <h1>Productos hardware</h1>
    <form class="row" (ngSubmit)="buscar()">
      <input [(ngModel)]="nombre" name="nombre" placeholder="Buscar por nombre">
      <button type="submit">Buscar</button>
    </form>
    <form class="grid-form" (ngSubmit)="guardar()">
      <input [(ngModel)]="form.nombre" name="pnombre" placeholder="Nombre" required>
      <input [(ngModel)]="form.modelo" name="modelo" placeholder="Modelo">
      <input [(ngModel)]="form.precioCompra" name="compra" type="number" step="0.01" placeholder="Precio compra" required>
      <input [(ngModel)]="form.precioVenta" name="venta" type="number" step="0.01" placeholder="Precio venta" required>
      <select [(ngModel)]="form.categoriaId" name="categoria" required>
        <option [ngValue]="c.id" *ngFor="let c of categorias">{{ c.nombre }}</option>
      </select>
      <select [(ngModel)]="form.marcaId" name="marca" required>
        <option [ngValue]="m.id" *ngFor="let m of marcas">{{ m.nombre }}</option>
      </select>
      <input [(ngModel)]="form.stockInicial" name="stock" type="number" placeholder="Stock" required>
      <input [(ngModel)]="form.stockMinimo" name="minimo" type="number" placeholder="Stock mínimo" required>
      <input [(ngModel)]="form.ubicacion" name="ubicacion" placeholder="Ubicación">
      <select [(ngModel)]="form.estado" name="estado"><option>ACTIVO</option><option>INACTIVO</option></select>
      <button>{{ form.id ? 'Actualizar' : 'Crear' }}</button>
    </form>
    <table>
      <tr><th>Producto</th><th>Modelo</th><th>Marca</th><th>Precio venta</th><th>Stock</th><th>Estado</th><th></th></tr>
      <tr *ngFor="let item of items">
        <td>{{ item.nombre }}</td><td>{{ item.modelo }}</td><td>{{ item.marcaNombre }}</td>
        <td>{{ item.precioVenta }}</td><td>{{ item.stock }}</td><td>{{ item.estado }}</td>
        <td><button type="button" (click)="editar(item)">Editar</button>
            <button type="button" (click)="eliminar(item.id)">Eliminar</button></td>
      </tr>
    </table>
    <p class="error" *ngIf="error">{{ error }}</p>
  `
})
export class ProductosPage implements OnInit {
  items: any[] = [];
  categorias: any[] = [];
  marcas: any[] = [];
  form: any = { estado: 'ACTIVO', stockInicial: 0, stockMinimo: 1 };
  nombre = '';
  error = '';
  constructor(private api: HardwareApiService) {}
  ngOnInit() {
    this.api.categorias().subscribe(c => this.categorias = c);
    this.api.marcas().subscribe(m => this.marcas = m);
    this.buscar();
  }
  buscar() {
    const params: Record<string, string> = {};
    if (this.nombre) params['nombre'] = this.nombre;
    this.api.productos(params).subscribe(items => this.items = items);
  }
  editar(item: any) {
    this.form = { ...item, stockInicial: item.stock };
  }
  guardar() {
    const req = this.form.id ? this.api.actualizarProducto(this.form.id, this.form) : this.api.crearProducto(this.form);
    req.subscribe({ next: () => { this.form = { estado: 'ACTIVO', stockInicial: 0, stockMinimo: 1 }; this.buscar(); }, error: () => this.error = 'Revisa precios, stock y categoría.' });
  }
  eliminar(id: number) { this.api.eliminarProducto(id).subscribe({ next: () => this.buscar(), error: () => this.error = 'El producto tiene ventas asociadas.' }); }
}

@Component({
  selector: 'app-inventario-page',
  standalone: true,
  imports: [CommonModule],
  template: `
    <h1>Inventario</h1>
    <table>
      <tr><th>Producto</th><th>Modelo</th><th>Stock</th><th>Mínimo</th><th>Ubicación</th></tr>
      <tr *ngFor="let item of items" [class.bajo]="item.stock <= item.stockMinimo">
        <td>{{ item.productoNombre }}</td><td>{{ item.modelo }}</td><td>{{ item.stock }}</td>
        <td>{{ item.stockMinimo }}</td><td>{{ item.ubicacion }}</td>
      </tr>
    </table>
  `
})
export class InventarioPage implements OnInit {
  items: any[] = [];
  constructor(private api: HardwareApiService) {}
  ngOnInit() { this.api.inventario().subscribe(items => this.items = items); }
}

@Component({
  selector: 'app-movimientos-page',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <h1>Movimientos de inventario</h1>
    <form class="row" (ngSubmit)="registrar()">
      <select [(ngModel)]="form.productoId" name="producto" required>
        <option [ngValue]="p.id" *ngFor="let p of productos">{{ p.nombre }}</option>
      </select>
      <select [(ngModel)]="form.tipoMovimiento" name="tipo"><option>ENTRADA</option><option>SALIDA</option></select>
      <input [(ngModel)]="form.cantidad" name="cantidad" type="number" min="1" required placeholder="Cantidad">
      <input [(ngModel)]="form.motivo" name="motivo" placeholder="Motivo" required>
      <button>Registrar</button>
    </form>
    <table>
      <tr><th>Fecha</th><th>Producto</th><th>Tipo</th><th>Cantidad</th><th>Motivo</th><th>Stock</th></tr>
      <tr *ngFor="let item of items">
        <td>{{ item.fecha | date:'short' }}</td><td>{{ item.productoNombre }}</td><td>{{ item.tipoMovimiento }}</td>
        <td>{{ item.cantidad }}</td><td>{{ item.motivo }}</td><td>{{ item.stockResultante }}</td>
      </tr>
    </table>
    <p class="error" *ngIf="error">{{ error }}</p>
  `
})
export class MovimientosPage implements OnInit {
  items: any[] = [];
  productos: any[] = [];
  form: any = { tipoMovimiento: 'ENTRADA', cantidad: 1, motivo: '' };
  error = '';
  constructor(private api: HardwareApiService, private auth: AuthService) {}
  ngOnInit() {
    this.api.productos().subscribe(p => this.productos = p);
    this.cargar();
  }
  cargar() { this.api.movimientos().subscribe(items => this.items = items); }
  registrar() {
    this.form.usuarioId = this.auth.getCurrentUser()?.id;
    this.api.registrarMovimiento(this.form).subscribe({
      next: () => { this.cargar(); this.error = ''; },
      error: () => this.error = 'No hay stock suficiente para esa salida.'
    });
  }
}

@Component({
  selector: 'app-clientes-page',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <h1>Clientes</h1>
    <form class="row" (ngSubmit)="buscar()">
      <input [(ngModel)]="nombre" name="nombre" placeholder="Buscar por nombre">
      <input [(ngModel)]="documento" name="documento" placeholder="Documento">
      <button type="submit">Buscar</button>
    </form>
    <form class="grid-form" (ngSubmit)="guardar()">
      <input [(ngModel)]="form.nombre" name="cnombre" placeholder="Nombre" required>
      <input [(ngModel)]="form.apellido" name="capellido" placeholder="Apellido" required>
      <input [(ngModel)]="form.documento" name="cdocumento" placeholder="Documento" required>
      <input [(ngModel)]="form.telefono" name="ctelefono" placeholder="Teléfono">
      <input [(ngModel)]="form.correo" name="ccorreo" type="email" placeholder="Correo" required>
      <button>{{ form.id ? 'Actualizar' : 'Registrar' }}</button>
    </form>
    <table>
      <tr><th>Nombre</th><th>Documento</th><th>Correo</th><th>Teléfono</th><th></th></tr>
      <tr *ngFor="let item of items">
        <td>{{ item.nombre }} {{ item.apellido }}</td><td>{{ item.documento }}</td><td>{{ item.correo }}</td><td>{{ item.telefono }}</td>
        <td><button type="button" (click)="editar(item)">Editar</button>
            <button type="button" (click)="eliminar(item.id)">Eliminar</button></td>
      </tr>
    </table>
    <p class="error" *ngIf="error">{{ error }}</p>
  `
})
export class ClientesPage implements OnInit {
  items: any[] = [];
  form: any = {};
  nombre = '';
  documento = '';
  error = '';
  constructor(private api: HardwareApiService) {}
  ngOnInit() { this.buscar(); }
  buscar() {
    const params: Record<string, string> = {};
    if (this.documento) params['documento'] = this.documento;
    else if (this.nombre) params['nombre'] = this.nombre;
    this.api.clientes(params).subscribe(items => this.items = items);
  }
  editar(item: any) {
    this.form = { id: item.id, nombre: item.nombre, apellido: item.apellido, documento: item.documento, telefono: item.telefono, correo: item.correo };
  }
  guardar() {
    const req = this.form.id ? this.api.actualizarCliente(this.form.id, this.form) : this.api.crearCliente(this.form);
    req.subscribe({ next: () => { this.form = {}; this.buscar(); }, error: () => this.error = 'Documento o correo inválido.' });
  }
  eliminar(id: number) { this.api.eliminarCliente(id).subscribe({ next: () => this.buscar(), error: () => this.error = 'El cliente tiene ventas.' }); }
}

@Component({
  selector: 'app-ventas-page',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <h1>Ventas</h1>
    <form class="grid-form" (ngSubmit)="registrar()">
      <select [(ngModel)]="clienteId" name="cliente" required>
        <option [ngValue]="c.id" *ngFor="let c of clientes">{{ c.nombre }} {{ c.apellido }}</option>
      </select>
      <select [(ngModel)]="productoId" name="producto">
        <option [ngValue]="p.id" *ngFor="let p of productos">{{ p.nombre }} · S/ {{ p.precioVenta }} · stock {{ p.stock }}</option>
      </select>
      <input [(ngModel)]="cantidad" name="cantidad" type="number" min="1" placeholder="Cantidad">
      <button type="button" (click)="agregar()">Agregar línea</button>
    </form>
    <table>
      <tr><th>Producto</th><th>Cantidad</th><th></th></tr>
      <tr *ngFor="let linea of lineas; let i = index">
        <td>{{ linea.nombre }}</td><td>{{ linea.cantidad }}</td>
        <td><button type="button" (click)="lineas.splice(i,1)">Quitar</button></td>
      </tr>
    </table>
    <button type="button" (click)="registrar()" [disabled]="!lineas.length">Registrar venta</button>
    <h2>Historial</h2>
    <table>
      <tr><th>Fecha</th><th>Cliente</th><th>Vendedor</th><th>Total</th><th>Estado</th></tr>
      <tr *ngFor="let venta of ventas">
        <td>{{ venta.fecha | date:'short' }}</td><td>{{ venta.clienteNombre }}</td>
        <td>{{ venta.usuarioNombre }}</td><td>{{ venta.total }}</td><td>{{ venta.estado }}</td>
      </tr>
    </table>
    <p class="error" *ngIf="error">{{ error }}</p>
  `
})
export class VentasPage implements OnInit {
  clientes: any[] = [];
  productos: any[] = [];
  ventas: any[] = [];
  lineas: any[] = [];
  clienteId: number | null = null;
  productoId: number | null = null;
  cantidad = 1;
  error = '';
  constructor(private api: HardwareApiService, private auth: AuthService) {}
  ngOnInit() {
    this.api.clientes().subscribe(c => this.clientes = c);
    this.api.productos().subscribe(p => this.productos = p);
    this.cargar();
  }
  cargar() { this.api.ventas().subscribe(v => this.ventas = v); }
  agregar() {
    const producto = this.productos.find(p => p.id === this.productoId);
    if (!producto) return;
    this.lineas.push({ productoId: producto.id, nombre: producto.nombre, cantidad: this.cantidad });
  }
  registrar() {
    const usuarioId = this.auth.getCurrentUser()?.id;
    this.api.registrarVenta({
      clienteId: this.clienteId,
      usuarioId,
      detalles: this.lineas.map(l => ({ productoId: l.productoId, cantidad: Number(l.cantidad) }))
    }).subscribe({
      next: () => { this.lineas = []; this.error = ''; this.cargar(); this.api.productos().subscribe(p => this.productos = p); },
      error: () => this.error = 'Stock insuficiente o datos incompletos.'
    });
  }
}

@Component({
  selector: 'app-usuarios-page',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <h1>Usuarios</h1>
    <form class="grid-form" (ngSubmit)="guardar()">
      <input [(ngModel)]="form.nombre" name="unombre" placeholder="Nombre" required>
      <input [(ngModel)]="form.apellido" name="uapellido" placeholder="Apellido" required>
      <input [(ngModel)]="form.usuario" name="uusuario" placeholder="Usuario" required>
      <input [(ngModel)]="form.password" name="upassword" type="password" placeholder="Contraseña">
      <select [(ngModel)]="form.rolId" name="urol" required>
        <option [ngValue]="r.id" *ngFor="let r of roles">{{ r.nombre }}</option>
      </select>
      <select [(ngModel)]="form.estado" name="uestado"><option>ACTIVO</option><option>INACTIVO</option></select>
      <button>{{ form.id ? 'Actualizar' : 'Crear' }}</button>
    </form>
    <table>
      <tr><th>Nombre</th><th>Usuario</th><th>Rol</th><th>Estado</th><th></th></tr>
      <tr *ngFor="let item of items">
        <td>{{ item.nombre }} {{ item.apellido }}</td><td>{{ item.usuario }}</td><td>{{ item.rol }}</td><td>{{ item.estado }}</td>
        <td>
          <button type="button" (click)="editar(item)">Editar</button>
          <button type="button" (click)="estado(item)">{{ item.estado === 'ACTIVO' ? 'Desactivar' : 'Activar' }}</button>
        </td>
      </tr>
    </table>
    <p class="error" *ngIf="error">{{ error }}</p>
  `
})
export class UsuariosPage implements OnInit {
  items: any[] = [];
  roles: any[] = [];
  form: any = { estado: 'ACTIVO' };
  error = '';
  constructor(private api: HardwareApiService) {}
  ngOnInit() {
    this.api.roles().subscribe(r => this.roles = r);
    this.cargar();
  }
  cargar() { this.api.usuarios().subscribe(items => this.items = items); }
  editar(item: any) {
    this.form = { id: item.id, nombre: item.nombre, apellido: item.apellido, usuario: item.usuario, rolId: item.rolId, estado: item.estado, password: '' };
  }
  guardar() {
    const body = { ...this.form };
    if (!body.password) delete body.password;
    const req = this.form.id ? this.api.actualizarUsuario(this.form.id, body) : this.api.crearUsuario(body);
    req.subscribe({ next: () => { this.form = { estado: 'ACTIVO' }; this.cargar(); }, error: () => this.error = 'No se pudo guardar el usuario.' });
  }
  estado(item: any) {
    const estado = item.estado === 'ACTIVO' ? 'INACTIVO' : 'ACTIVO';
    this.api.cambiarEstadoUsuario(item.id, estado).subscribe({ next: () => this.cargar() });
  }
}
