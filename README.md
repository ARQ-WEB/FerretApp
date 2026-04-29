# FerretApp — Sistema de Gestión para Ferretería

> Backend REST API desarrollado con **Spring Boot 3** y **PostgreSQL**, diseñado para gestionar inventario, ventas, pedidos a proveedores, usuarios y auditoría de una ferretería. Base de datos normalizada hasta **5FN**.

---
prueba frank
## Tabla de contenidos

- [Descripción del proyecto](#descripción-del-proyecto)
- [Características principales](#características-principales)
- [Tecnologías utilizadas](#tecnologías-utilizadas)
- [Arquitectura del proyecto](#arquitectura-del-proyecto)
- [Modelo de base de datos](#modelo-de-base-de-datos)
- [Normalización aplicada](#normalización-aplicada)
- [Estructura del proyecto](#estructura-del-proyecto)
- [Endpoints REST](#endpoints-rest)
- [Requisitos previos](#requisitos-previos)
- [Instalación y ejecución](#instalación-y-ejecución)
- [Variables de configuración](#variables-de-configuración)
- [Roles y permisos](#roles-y-permisos)
- [Módulos del sistema](#módulos-del-sistema)

---

## Descripción del proyecto

**FerretApp** es una API REST backend para el sistema de gestión integral de una ferretería. Permite a administradores y vendedores gestionar el inventario de productos, registrar ventas, realizar pedidos de reabastecimiento a proveedores, administrar usuarios y mantener un log completo de auditoría.

El sistema implementa control de acceso por roles (Administrador / Vendedor), alertas automáticas de stock mínimo, reportes estadísticos de ventas con filtros por fechas, y una base de datos completamente normalizada hasta la Quinta Forma Normal (5FN).

---

## Características principales

- Gestión completa de inventario con alertas de stock bajo
- Punto de venta con descuento automático de stock al registrar ventas
- Pedidos de reabastecimiento a proveedores con actualización de stock al recibir
- Sistema de roles con control de acceso (Administrador y Vendedor)
- Log de auditoría completo de todas las acciones del sistema
- Reportes de ventas con KPIs, gráfico por día y distribución por categoría
- Base de datos normalizada hasta 5FN (sin datos derivados almacenados)
- Eliminación lógica (soft delete) en todas las entidades principales
- Notificaciones por email vía SendGrid cuando el stock cae bajo el mínimo

---

## Tecnologías utilizadas

| Tecnología | Versión | Uso |
|---|---|---|
| Java | 17 | Lenguaje principal |
| Spring Boot | 3.5.14 | Framework principal |
| Spring Data JPA | 3.5.14 | Persistencia y repositorios |
| Spring Web | 3.5.14 | API REST |
| Spring Security | 3.5.14 | Autenticación y autorización JWT |
| PostgreSQL | 15+ | Base de datos relacional |
| Lombok | latest | Reducción de boilerplate |
| Maven | 3.9+ | Gestión de dependencias |
| BCrypt | built-in | Hash de contraseñas |
| SendGrid | vía REST | Notificaciones por email |

---

## Arquitectura del proyecto

El proyecto sigue una arquitectura en capas estándar de Spring Boot:

```
Cliente (Angular)
       │
       ▼
  Controladores  ←── Capa de presentación (REST)
       │
       ▼
   Servicios     ←── Lógica de negocio + Auditoría
       │
       ▼
  Repositorios   ←── Acceso a datos (Spring Data JPA)
       │
       ▼
   PostgreSQL     ←── Base de datos normalizada (5FN)
```

---

## Modelo de base de datos

El esquema contiene las siguientes entidades principales:

| Tabla | Descripción |
|---|---|
| `ROL` | Roles del sistema (Administrador, Vendedor) |
| `USUARIO` | Usuarios del sistema con su rol asignado |
| `CLIENTE` | Clientes asociados a las ventas (5FN) |
| `CATEGORIA` | Categorías de productos |
| `PROVEEDOR` | Proveedores de productos |
| `PRODUCTO` | Catálogo de productos del inventario |
| `PRODUCTO_CATEGORIA` | Relación N:M entre productos y categorías (4FN) |
| `PRODUCTO_PROVEEDOR` | Relación N:M entre productos y proveedores (4FN) |
| `VENTA` | Cabecera de venta |
| `DETALLE_VENTA` | Líneas de cada venta (sin subtotal almacenado) |
| `PEDIDO` | Cabecera de pedido a proveedor |
| `DETALLE_PEDIDO` | Líneas de cada pedido con FK compuesta (5FN) |
| `AUDITORIA` | Registro de todas las acciones del sistema |

---

## Normalización aplicada

### 1FN y 2FN — Cumplidas
Todos los atributos son atómicos, todas las PKs son simples, no existen dependencias parciales.

### 3FN — Datos derivados eliminados
- `subtotal` eliminado de `DETALLE_VENTA` y `DETALLE_PEDIDO`. Se calcula en tiempo de consulta como `cantidad × precio_unitario`.
- `total` eliminado de `VENTA` y `PEDIDO`. Se calcula mediante vistas SQL y en la capa de servicio con `SUM(subtotal)`.

### 4FN — Dependencias multivaluadas descompuestas
- `ID_CATEGORIA` e `ID_PROVEEDOR` eliminados de `PRODUCTO`.
- Creadas tablas de unión `PRODUCTO_CATEGORIA` y `PRODUCTO_PROVEEDOR` que permiten que un producto tenga múltiples categorías y múltiples proveedores sin redundancia.

### 5FN — Relación ternaria explícita
- Nueva entidad `CLIENTE` para completar la relación ternaria `VENTA ↔ VENDEDOR ↔ CLIENTE`.
- `DETALLE_PEDIDO` incluye `ID_PROVEEDOR` con FK compuesta hacia `PRODUCTO_PROVEEDOR`, garantizando que solo se puedan agregar a un pedido productos que realmente suministra el proveedor del encabezado.
- Trigger `trg_validar_proveedor_detalle_pedido` refuerza esta integridad a nivel de base de datos.

---

## Estructura del proyecto

```
src/
└── main/
    └── java/
        └── com/ferretapp/
            ├── entidades/
            │   ├── Rol.java
            │   ├── Usuario.java
            │   ├── Auditoria.java
            │   ├── Categoria.java
            │   ├── Proveedor.java
            │   ├── Producto.java
            │   ├── Cliente.java
            │   ├── Venta.java
            │   ├── DetalleVenta.java
            │   ├── Pedido.java
            │   └── DetallePedido.java
            ├── repositorios/
            │   ├── RolRepositorio.java
            │   ├── UsuarioRepositorio.java
            │   ├── AuditoriaRepositorio.java
            │   ├── CategoriaRepositorio.java
            │   ├── ProveedorRepositorio.java
            │   ├── ProductoRepositorio.java
            │   ├── ClienteRepositorio.java
            │   ├── VentaRepositorio.java
            │   ├── DetalleVentaRepositorio.java
            │   ├── PedidoRepositorio.java
            │   └── DetallePedidoRepositorio.java
            ├── servicios/
            │   ├── AuditoriaServicio.java
            │   ├── UsuarioServicio.java
            │   ├── CategoriaServicio.java
            │   ├── ProveedorServicio.java
            │   ├── ProductoServicio.java
            │   ├── ClienteServicio.java
            │   ├── VentaServicio.java
            │   ├── PedidoServicio.java
            │   └── ReporteServicio.java
            ├── controladores/
            │   ├── RolControlador.java
            │   ├── UsuarioControlador.java
            │   ├── AuditoriaControlador.java
            │   ├── CategoriaControlador.java
            │   ├── ProveedorControlador.java
            │   ├── ProductoControlador.java
            │   ├── ClienteControlador.java
            │   ├── VentaControlador.java
            │   ├── PedidoControlador.java
            │   └── ReporteControlador.java
            └── dtos/
                ├── RolDTO.java
                ├── UsuarioDTO.java
                ├── AuditoriaDTO.java
                ├── CategoriaDTO.java
                ├── ProveedorDTO.java
                ├── ProductoDTO.java
                ├── ClienteDTO.java
                ├── VentaDTO.java
                ├── PedidoDTO.java
                └── ReporteDTO.java
```

---

## Endpoints REST

### Autenticación
| Método | Endpoint | Descripción |
|---|---|---|
| POST | `/api/auth/login` | Inicio de sesión, retorna JWT |
| POST | `/api/auth/logout` | Cierre de sesión |

### Usuarios
| Método | Endpoint | Rol requerido |
|---|---|---|
| GET | `/api/usuarios` | ADMIN |
| GET | `/api/usuarios/{id}` | ADMIN |
| POST | `/api/usuarios` | ADMIN |
| PUT | `/api/usuarios/{id}` | ADMIN |
| DELETE | `/api/usuarios/{id}` | ADMIN |

### Inventario — Productos
| Método | Endpoint | Rol requerido |
|---|---|---|
| GET | `/api/productos` | ADMIN, VENDEDOR |
| GET | `/api/productos/{id}` | ADMIN, VENDEDOR |
| GET | `/api/productos/buscar?q=` | ADMIN, VENDEDOR |
| GET | `/api/productos/stock-bajo` | ADMIN, VENDEDOR |
| POST | `/api/productos` | ADMIN |
| PUT | `/api/productos/{id}` | ADMIN |
| DELETE | `/api/productos/{id}` | ADMIN |

### Categorías
| Método | Endpoint | Rol requerido |
|---|---|---|
| GET | `/api/categorias` | ADMIN, VENDEDOR |
| POST | `/api/categorias` | ADMIN |
| PUT | `/api/categorias/{id}` | ADMIN |
| DELETE | `/api/categorias/{id}` | ADMIN |

### Proveedores
| Método | Endpoint | Rol requerido |
|---|---|---|
| GET | `/api/proveedores` | ADMIN |
| GET | `/api/proveedores/buscar?q=` | ADMIN |
| POST | `/api/proveedores` | ADMIN |
| PUT | `/api/proveedores/{id}` | ADMIN |
| DELETE | `/api/proveedores/{id}` | ADMIN |

### Ventas
| Método | Endpoint | Rol requerido |
|---|---|---|
| GET | `/api/ventas` | ADMIN, VENDEDOR |
| GET | `/api/ventas/{id}` | ADMIN, VENDEDOR |
| GET | `/api/ventas/recientes` | ADMIN, VENDEDOR |
| POST | `/api/ventas` | ADMIN, VENDEDOR |

### Pedidos
| Método | Endpoint | Rol requerido |
|---|---|---|
| GET | `/api/pedidos` | ADMIN |
| GET | `/api/pedidos/{id}` | ADMIN |
| POST | `/api/pedidos` | ADMIN |
| PATCH | `/api/pedidos/{id}/recibir` | ADMIN |

### Reportes
| Método | Endpoint | Rol requerido |
|---|---|---|
| GET | `/api/reportes/ventas?desde=&hasta=` | ADMIN |
| GET | `/api/reportes/stock-bajo` | ADMIN |

### Auditoría
| Método | Endpoint | Rol requerido |
|---|---|---|
| GET | `/api/auditoria` | ADMIN |
| GET | `/api/auditoria/resumen` | ADMIN |

---

## Requisitos previos

- **Java 17** o superior
- **Maven 3.9+**
- **PostgreSQL 15+** corriendo localmente o en un servidor
- (Opcional) **Docker** para levantar PostgreSQL en un contenedor

---

## Instalación y ejecución

### 1. Clonar el repositorio

```bash
git clone https://github.com/tu-usuario/FerretApp.git
cd FerretApp
```

### 2. Crear la base de datos en PostgreSQL

```sql
CREATE DATABASE ferreteria;
```

### 3. Ejecutar el script SQL normalizado

```bash
psql -U postgres -d ferreteria -f ferreteria_normalizado_pg.sql
```

### 4. Configurar las variables de entorno

Copia el archivo de ejemplo y edita tus credenciales:

```bash
cp src/main/resources/application.properties.example src/main/resources/application.properties
```

### 5. Compilar y ejecutar

```bash
mvn clean install
mvn spring-boot:run
```

La API estará disponible en `http://localhost:8080`.

---

## Variables de configuración

```properties
# Base de datos
spring.datasource.url=jdbc:postgresql://localhost:5432/ferreteria
spring.datasource.username=postgres
spring.datasource.password=tu_password

# JPA
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=false
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect

# JWT
app.jwt.secret=tu_clave_secreta_muy_larga
app.jwt.expiration-ms=28800000

# SendGrid (alertas de stock)
app.sendgrid.api-key=SG.xxxx
app.sendgrid.from-email=sistema@ferreteria.com
app.admin.email=admin@ferreteria.com

# Puerto del servidor
server.port=8080
```

---

## Roles y permisos

| Módulo | Administrador | Vendedor |
|---|---|---|
| Dashboard | ✅ | ✅ |
| Inventario — consultar | ✅ | ✅ |
| Inventario — crear/editar/eliminar | ✅ | ❌ |
| Ventas — registrar | ✅ | ✅ |
| Ventas — historial | ✅ | ✅ |
| Proveedores | ✅ | ❌ |
| Pedidos | ✅ | ❌ |
| Reportes | ✅ | ❌ |
| Usuarios | ✅ | ❌ |
| Auditoría | ✅ | ❌ |

---

## Módulos del sistema

### Autenticación y seguridad
Inicio de sesión con JWT (8 horas de expiración), cierre de sesión seguro y protección de rutas por rol mediante Spring Security.

### Gestión de inventario
CRUD completo de productos con SKU único, alertas visuales de stock bajo, búsqueda en tiempo real por nombre, SKU o categoría.

### Punto de venta
Registro de ventas con carrito, validación de stock disponible y descuento automático del inventario al completar una venta.

### Gestión de proveedores
CRUD de proveedores con bloqueo de eliminación si tienen productos activos asociados.

### Pedidos a proveedores
Creación de órdenes de reabastecimiento, seguimiento de estado (Pendiente / Recibido / Cancelado) y actualización automática del stock al marcar como recibido.

### Reportes y estadísticas
KPIs de ventas con filtro por rango de fechas: ingresos totales, ventas totales, unidades vendidas y promedio. Gráfico de barras por día y pastel por categoría.

### Gestión de usuarios
Administración de usuarios con roles, contraseñas hasheadas con BCrypt y bloqueo de auto-eliminación.

### Auditoría
Registro automático de todas las operaciones de escritura (crear, editar, eliminar) con filtros por usuario, fecha y tipo de acción.

### Integración SendGrid
Envío asíncrono de alertas por email al administrador cuando el stock de un producto cae por debajo del mínimo configurado.

---
