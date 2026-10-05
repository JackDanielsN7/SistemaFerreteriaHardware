# SistemaFerreteriaHardware

Sistema web para una ferretería de hardware: usuarios, catálogo, inventario, ventas y clientes.

## Stack

- **Frontend:** Angular 20 (`frontendferreteria/`)
- **Backend:** Spring Boot 3 + JWT (`SistemaFerreteriaHardwarebackend/`)
- **Base de datos:** MySQL 8 en Docker (`ferreteria_hardware`)

## Instalación

### 1. Base de datos

```bash
copy .env.example .env
docker compose up -d
```

Si Docker ya tenía un volumen de MySQL de otro proyecto, crea el contenedor con un volumen limpio para que la base `ferreteria_hardware` nazca vacía.

### 2. Backend

```bat
cd SistemaFerreteriaHardwarebackend
mvnw.cmd spring-boot:run
```

API: http://localhost:8080

### 3. Frontend

```bash
cd frontendferreteria
npm install
npx ng serve
```

App: http://localhost:4200

## Credenciales

| Rol | Usuario | Contraseña |
|---|---|---|
| Admin | admin | admin123 |
| Vendedor | vendedor | vendedor123 |

## Módulos

1. Usuarios y roles
2. Productos hardware (categorías y marcas)
3. Inventario y movimientos
4. Ventas cabecera-detalle
5. Clientes
