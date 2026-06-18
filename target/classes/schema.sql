-- ==============================================================
-- Schema para lab_1806 (MySQL)
-- Spring Boot lo ejecuta automáticamente al arrancar gracias a
--   spring.sql.init.mode=always
-- Es idempotente: si la tabla ya existe, no hace nada.
-- ==============================================================

CREATE TABLE IF NOT EXISTS productos (
    id     BIGINT       NOT NULL AUTO_INCREMENT,
    nombre VARCHAR(80)  NOT NULL,
    precio DOUBLE       NOT NULL,
    stock  INT          NOT NULL,
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
