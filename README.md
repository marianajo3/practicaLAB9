# Lab 1806 — WS con Security + JavaScript + jQuery

Lienzo base de un proyecto **Spring Boot 3 + Spring Security (JWT) + JPA/H2** con un frontend
estático (HTML + jQuery) que consume los Web Services REST protegidos.

## 📁 Estructura

```
lab_1806/
├── pom.xml
└── src/main/
    ├── java/com/lab/lab1806/
    │   ├── Lab1806Application.java
    │   ├── config/DataInitializer.java
    │   ├── controller/
    │   │   ├── AuthController.java          # POST /auth/login (público)
    │   │   ├── ProductoController.java      # /api/productos  (USER/ADMIN)
    │   │   └── MensajeController.java       # /api/mensaje/**
    │   ├── dto/
    │   │   ├── LoginRequest.java
    │   │   ├── AuthResponse.java
    │   │   └── MessageResponse.java
    │   ├── model/
    │   │   ├── Usuario.java
    │   │   └── Producto.java
    │   ├── repository/
    │   │   ├── UsuarioRepository.java
    │   │   └── ProductoRepository.java
    │   └── security/
    │       ├── SecurityConfig.java          # reglas + BCrypt
    │       ├── JwtService.java              # genera/valida tokens
    │       ├── JwtAuthenticationFilter.java # extrae Bearer del header
    │       └── UserDetailsServiceImpl.java  # carga usuarios de la BD
    └── resources/
        ├── application.properties
        └── static/                          # frontend (jQuery)
            ├── index.html
            ├── css/styles.css
            └── js/{auth.js, api.js, app.js}
```

## ▶️ Cómo correr

```bash
mvn spring-boot:run
```

Abre: **http://localhost:8080/**

## 👤 Usuarios de prueba (creados al arrancar)

| Usuario | Contraseña | Rol       |
|---------|------------|-----------|
| `admin` | `admin123` | ROLE_ADMIN |
| `user`  | `user123`  | ROLE_USER  |

## 🔌 Endpoints

| Método | URL                       | Auth          | Descripción                  |
|--------|---------------------------|---------------|------------------------------|
| POST   | `/auth/login`             | público       | devuelve JWT                 |
| GET    | `/api/mensaje/publico`    | público       | sin token                    |
| GET    | `/api/mensaje/privado`    | USER / ADMIN  | con token                    |
| GET    | `/api/mensaje/admin`      | ADMIN         | solo rol ADMIN               |
| GET    | `/api/productos`          | USER / ADMIN  | listar                       |
| GET    | `/api/productos/{id}`     | USER / ADMIN  | obtener                      |
| POST   | `/api/productos`          | ADMIN         | crear                        |
| PUT    | `/api/productos/{id}`     | ADMIN         | actualizar                   |
| DELETE | `/api/productos/{id}`     | ADMIN         | eliminar                     |
| GET    | `/h2-console`             | público (dev) | consola H2                   |

## 🧪 Flujo de prueba (con curl)

```bash
# 1) Login -> obtienes el token
curl -s -X POST http://localhost:8080/auth/login \
     -H "Content-Type: application/json" \
     -d '{"username":"admin","password":"admin123"}'

# 2) Llamar a un endpoint protegido pasando el token
TOKEN="PEGA_AQUI_EL_TOKEN"
curl -s http://localhost:8080/api/mensaje/privado -H "Authorization: Bearer $TOKEN"
curl -s http://localhost:8080/api/productos     -H "Authorization: Bearer $TOKEN"
```

## 🔐 Cómo está protegido

1. `SecurityConfig` declara:
   - `POST /auth/login` → **público**
   - `/api/**` → **authenticated** (se valida rol con `@PreAuthorize` en cada controller)
   - `/h2-console/**` y estáticos → **público**
   - Sesión `STATELESS` (no se guarda nada en el servidor)
2. `JwtAuthenticationFilter` lee el header `Authorization: Bearer ...`, valida el token y
   monta el `Authentication` en el `SecurityContext`.
3. `JwtService` genera/valida los tokens con la clave secreta Base64 definida en
   `application.properties` (`app.security.jwt.secret`).
4. Los roles se controlan con `@PreAuthorize("hasRole('ADMIN')")` en los controllers.

## 🖥️ Frontend (jQuery)

- `js/auth.js` — wrapper sobre `localStorage` para guardar/recuperar el token.
- `js/api.js` — todas las llamadas a WS. Inyecta el `Authorization` automáticamente.
- `js/app.js` — UI: muestra login o app, conecta botones, renderiza la tabla.

Para cambiar la URL base del backend (por ejemplo, servir el frontend en otro origen),
edita la constante `BASE` en `js/api.js`.

## ⚙️ Notas

- **No uses la clave JWT de `application.properties` en producción.** Genera una nueva:
  ```bash
  openssl rand -base64 48
  ```
- Para producción cambia `spring.datasource.*` a MySQL/PostgreSQL y `ddl-auto` a `validate`.
- En `SecurityConfig` ya está `csrf().disable()` porque la API es **stateless** con JWT.
