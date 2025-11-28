USE my_eshop_db;

SET FOREIGN_KEY_CHECKS=0;

DROP TABLE IF EXISTS articulo_compra;
DROP TABLE IF EXISTS articulo;
DROP TABLE IF EXISTS compra;
DROP TABLE IF EXISTS informacion_fiscal;
DROP TABLE IF EXISTS cliente;

SET FOREIGN_KEY_CHECKS=1;

############################################################
# TABLA: CLIENTE
# Un cliente puede tener muchas compras (1:N con compra)
# Un cliente tiene una sola información fiscal (1:1)
# ID es autonumérico y puede convertirse a negativo al anonimizar
############################################################

CREATE TABLE cliente (
  id INTEGER NOT NULL AUTO_INCREMENT,   -- PK numérica
  nif_cif VARCHAR(20) DEFAULT NULL,     -- dato personal (se puede vaciar)
  nombre_completo VARCHAR(100) DEFAULT '',
  email VARCHAR(150) DEFAULT '',
  fecha_registro DATETIME DEFAULT NULL,

  PRIMARY KEY (id),
  UNIQUE KEY unique_nif (nif_cif),
  UNIQUE KEY unique_email (email)
) ENGINE=InnoDB;


############################################################
# TABLA: INFORMACION_FISCAL
# Relación 1:1 con cliente
# Cada cliente tiene una sola fila en esta tabla
# PK = FK hacia cliente.id
############################################################
CREATE TABLE informacion_fiscal (
  cliente_id INTEGER NOT NULL,  -- FK 1:1 con cliente.id

  telefono VARCHAR(20) DEFAULT '',
  direccion_fiscal VARCHAR(255) DEFAULT '',

  PRIMARY KEY (cliente_id),

  CONSTRAINT informacion_fiscal_fk
    FOREIGN KEY (cliente_id) REFERENCES cliente(id)
    ON DELETE CASCADE
) ENGINE=InnoDB;


############################################################
# TABLA: COMPRA
# Relación N:1 con cliente
# Un cliente puede tener muchas compras
# Una compra pertenece a un único cliente
############################################################
CREATE TABLE compra (
  id INTEGER NOT NULL AUTO_INCREMENT,
  fecha_compra DATETIME NOT NULL,
  estado VARCHAR(20) NOT NULL,
  direccion_entrega VARCHAR(255) NOT NULL,
  precio_total DECIMAL(10,2) NOT NULL,
  cliente_id INTEGER NOT NULL,  -- FK cliente.id (N:1)

  PRIMARY KEY (id),
  KEY compra_cliente_fk (cliente_id),

  CONSTRAINT compra_cliente_fk
    FOREIGN KEY (cliente_id) REFERENCES cliente(id)
    ON UPDATE CASCADE
) ENGINE=InnoDB;


############################################################
# TABLA: ARTICULO
# Tabla independiente para los productos del catálogo
############################################################
CREATE TABLE articulo (
  id INTEGER NOT NULL AUTO_INCREMENT,
  nombre VARCHAR(100) NOT NULL,
  descripcion TEXT DEFAULT NULL,
  precio_actual DECIMAL(10,2) NOT NULL,
  stock INTEGER NOT NULL,

  PRIMARY KEY (id)
) ENGINE=InnoDB;


############################################################
# TABLA: ARTICULO_COMPRA
# Relación N:N entre articulo y compra
# Una compra puede tener muchos artículos
# Un artículo puede aparecer en muchas compras
# Esta tabla intermedia rompe la N:N
############################################################
CREATE TABLE articulo_compra (
  articulo_id INTEGER NOT NULL,   -- FK hacia articulo.id (N:1)
  compra_id INTEGER NOT NULL,     -- FK hacia compra.id   (N:1)
  unidades INTEGER NOT NULL,
  precio_compra DECIMAL(10,2) NOT NULL,

  PRIMARY KEY (articulo_id, compra_id),

  CONSTRAINT articulo_compra_articulo_fk
    FOREIGN KEY (articulo_id) REFERENCES articulo(id)
    ON UPDATE CASCADE,

  CONSTRAINT articulo_compra_compra_fk
    FOREIGN KEY (compra_id) REFERENCES compra(id)
    ON UPDATE CASCADE
) ENGINE=InnoDB;


############################################################
# INSERTS DE EJEMPLO
############################################################

-- Clientes (IDs autogenerados: 1, 2, 3)
INSERT INTO cliente (nif_cif, nombre_completo, email, fecha_registro) VALUES
  ('X1234567A', 'Ana García', 'ana.garcia@example.com', '2024-01-10 09:15:00'),
  ('Y7654321B', 'Luis Pérez',  'luis.perez@example.com',  '2024-02-05 14:30:00'),
  ('Z9998887C', 'María López', 'maria.lopez@example.com', '2024-03-12 11:00:00');

-- Información fiscal 1:1
INSERT INTO informacion_fiscal (cliente_id, telefono, direccion_fiscal) VALUES
  (1, '+34 600 111 222', 'C/ Ejemplo 1, 28001 Madrid'),
  (2, '+34 600 333 444', 'Av. Prueba 10, 08002 Barcelona'),
  (3, '+34 600 555 666', 'Plaza Demo 5, 41001 Sevilla');

-- Compras 1:N (cliente → compras)
INSERT INTO compra (fecha_compra, estado, direccion_entrega, precio_total, cliente_id) VALUES
  ('2024-04-01 10:00:00', 'procesando', 'C/ Ejemplo 1, 28001 Madrid', 39.98, 1),
  ('2024-04-02 16:20:00', 'enviado',     'Av. Prueba 10, 08002 Barcelona', 64.50, 2);

-- Artículos
INSERT INTO articulo (nombre, descripcion, precio_actual, stock) VALUES
  ('Camiseta básica', 'Camiseta 100% algodón, talla M', 19.99, 100),
  ('Auriculares bluetooth', 'Auriculares inalámbricos con cancelación', 49.50, 50),
  ('Taza cerámica', 'Taza 300ml, apta para lavavajillas', 5.00, 200);

-- Relación N:N compra <-> artículo
INSERT INTO articulo_compra (articulo_id, compra_id, unidades, precio_compra) VALUES
  (1, 1, 2, 19.99),
  (2, 2, 1, 49.50),
  (3, 2, 3, 5.00);
