-- ==============================================================
-- Schema para lab_1806 — versión con usuarios + roles en BD
-- Spring Boot lo ejecuta automáticamente al arrancar gracias a
--   spring.sql.init.mode=always
-- Es idempotente: si la tabla ya existe, no hace nada.
-- ==============================================================

-- -------------------- PRODUCTOS --------------------
CREATE TABLE IF NOT EXISTS productos (
    id     BIGINT       NOT NULL AUTO_INCREMENT,
    nombre VARCHAR(80)  NOT NULL,
    precio DOUBLE       NOT NULL,
    stock  INT          NOT NULL,
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- -------------------- USUARIOS --------------------
CREATE TABLE IF NOT EXISTS usuarios (
    id       BIGINT       NOT NULL AUTO_INCREMENT,
    username VARCHAR(60)  NOT NULL UNIQUE,
    password VARCHAR(200) NOT NULL,        -- BCrypt hash, NUNCA texto plano
    enabled  BOOLEAN      NOT NULL DEFAULT TRUE,
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- -------------------- ROLES --------------------
CREATE TABLE IF NOT EXISTS roles (
    id     BIGINT      NOT NULL AUTO_INCREMENT,
    nombre VARCHAR(40) NOT NULL UNIQUE,    -- 'ROLE_USER' o 'ROLE_ADMIN'
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- -------------------- USUARIO_ROLES (N:M) --------------------
CREATE TABLE IF NOT EXISTS usuario_roles (
    usuario_id BIGINT NOT NULL,
    rol_id     BIGINT NOT NULL,
    PRIMARY KEY (usuario_id, rol_id),
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE,
    FOREIGN KEY (rol_id)     REFERENCES roles(id)     ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================
-- Datos de ejemplo (solo si las tablas están vacías)
-- admin / admin123   → ROLE_ADMIN
-- user  / user123    → ROLE_USER
-- Los password fueron generados con BCryptPasswordEncoder.encode()
-- ============================================================

INSERT INTO roles (nombre)
SELECT 'ROLE_USER'  WHERE NOT EXISTS (SELECT 1 FROM roles WHERE nombre = 'ROLE_USER');

INSERT INTO roles (nombre)
SELECT 'ROLE_ADMIN' WHERE NOT EXISTS (SELECT 1 FROM roles WHERE nombre = 'ROLE_ADMIN');

INSERT INTO usuarios (username, password, enabled)
SELECT 'admin', '$2a$10$/DtDY/ThPK9.L9GNnenwe.G6sr4j.K6BNsqWzpzkgiaILNgnaMcCy', TRUE
WHERE NOT EXISTS (SELECT 1 FROM usuarios WHERE username = 'admin');

INSERT INTO usuarios (username, password, enabled)
SELECT 'user', '$2a$10$yrNb2M96VsuESYUgs9IvVusRQ.dJ5fAFS86p83tX73DyauH3gZ8zi', TRUE
WHERE NOT EXISTS (SELECT 1 FROM usuarios WHERE username = 'user');

INSERT INTO usuario_roles (usuario_id, rol_id)
SELECT u.id, r.id
FROM usuarios u, roles r
WHERE u.username = 'admin' AND r.nombre = 'ROLE_ADMIN'
  AND NOT EXISTS (SELECT 1 FROM usuario_roles ur
                  WHERE ur.usuario_id = u.id AND ur.rol_id = r.id);

INSERT INTO usuario_roles (usuario_id, rol_id)
SELECT u.id, r.id
FROM usuarios u, roles r
WHERE u.username = 'user' AND r.nombre = 'ROLE_USER'
  AND NOT EXISTS (SELECT 1 FROM usuario_roles ur
                  WHERE ur.usuario_id = u.id AND ur.rol_id = r.id);

INSERT INTO productos (nombre, precio, stock)
SELECT 'Laptop',  3500.0, 10 WHERE NOT EXISTS (SELECT 1 FROM productos WHERE nombre = 'Laptop');

INSERT INTO productos (nombre, precio, stock)
SELECT 'Mouse',   50.0,   100 WHERE NOT EXISTS (SELECT 1 FROM productos WHERE nombre = 'Mouse');

INSERT INTO productos (nombre, precio, stock)
SELECT 'Teclado', 150.0,  40  WHERE NOT EXISTS (SELECT 1 FROM productos WHERE nombre = 'Teclado');