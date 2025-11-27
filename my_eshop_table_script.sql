USE my_eshop_db;

-- Desactivamos temporalmente la comprobación de claves foráneas
-- Esto permite borrar y crear las tablas en cualquier orden sin errores.
SET FOREIGN_KEY_CHECKS=0;

-- BORRADO SEGURO DE TABLAS
DROP TABLE IF EXISTS articulo_compra;
DROP TABLE IF EXISTS articulo;
DROP TABLE IF EXISTS compra;
DROP TABLE IF EXISTS informacion_fiscal;
DROP TABLE IF EXISTS cliente;

-- Reactivamos la comprobación de claves foráneas
SET FOREIGN_KEY_CHECKS=1;


-- CREACIÓN DE TABLAS

-- CLIENTE
CREATE TABLE IF NOT EXISTS cliente (
  nif_cif varchar(20) NOT NULL,
  nombre_completo varchar(100) NOT NULL,
  email varchar(150) NOT NULL,
  fecha_registro datetime NOT NULL,
  PRIMARY KEY (nif_cif),
  UNIQUE KEY NewTable_UNIQUE (email)
) ENGINE=InnoDB;


-- INFORMACION_FISCAL (Relación 1:1 con Cliente)
CREATE TABLE IF NOT EXISTS informacion_fiscal (
  nif_cif varchar(20) NOT NULL,
  telefono varchar(20) NOT NULL,
  direccion_fiscal varchar(255) NOT NULL,
  PRIMARY KEY (nif_cif),
  CONSTRAINT informacion_fiscal_cliente_FK 
  FOREIGN KEY (nif_cif) REFERENCES cliente (nif_cif) 
  		ON UPDATE CASCADE
) ENGINE=InnoDB;


-- COMPRA 
CREATE TABLE IF NOT EXISTS compra (
  id int(11) NOT NULL AUTO_INCREMENT,
  fecha_compra datetime NOT NULL,
  estado varchar(20) NOT NULL,
  direccion_entrega varchar(255) NOT NULL,
  precio_total decimal(10,2) NOT NULL,
  cliente_nif_cif varchar(20) NOT NULL,
  PRIMARY KEY (id),
  KEY compra_cliente_FK (cliente_nif_cif),
  CONSTRAINT compra_cliente_FK 
  FOREIGN KEY (cliente_nif_cif) REFERENCES cliente (nif_cif) 
  		ON UPDATE CASCADE
) ENGINE=InnoDB;


-- ARTICULO
CREATE TABLE IF NOT EXISTS articulo (
  id int(11) NOT NULL AUTO_INCREMENT,
  nombre varchar(100) NOT NULL,
  descripcion text DEFAULT NULL,
  precio_actual decimal(10,2) NOT NULL,
  stock int(11) NOT NULL,
  PRIMARY KEY (id)
) ENGINE=InnoDB;

-- ARTICULO_COMPRA
CREATE TABLE IF NOT EXISTS articulo_compra (
  articulo_id int(11) NOT NULL,
  compra_id int(11) NOT NULL,
  unidades int(11) NOT NULL,
  precio_compra decimal(10,2) NOT NULL,
  PRIMARY KEY (articulo_id,compra_id),
  KEY articulo_compra_compra_FK (compra_id),
  CONSTRAINT articulo_compra_articulo_FK 
  FOREIGN KEY (articulo_id) REFERENCES articulo (id) 
  		ON UPDATE CASCADE,
  CONSTRAINT articulo_compra_compra_FK 
  FOREIGN KEY (compra_id) REFERENCES compra (id) 
  		ON UPDATE CASCADE
) ENGINE=InnoDB;

-- Example data for testing

-- Clientes
INSERT INTO cliente (nif_cif, nombre_completo, email, fecha_registro) VALUES
  ('X1234567A', 'Ana García', 'ana.garcia@example.com', '2024-01-10 09:15:00'),
  ('Y7654321B', 'Luis Pérez',  'luis.perez@example.com',  '2024-02-05 14:30:00'),
  ('Z9998887C', 'María López', 'maria.lopez@example.com', '2024-03-12 11:00:00');

-- Informacion fiscal (1:1 con cliente)
INSERT INTO informacion_fiscal (nif_cif, telefono, direccion_fiscal) VALUES
  ('X1234567A', '+34 600 111 222', 'C/ Ejemplo 1, 28001 Madrid'),
  ('Y7654321B', '+34 600 333 444', 'Av. Prueba 10, 08002 Barcelona'),
  ('Z9998887C', '+34 600 555 666', 'Plaza Demo 5, 41001 Sevilla');

-- Articulos
INSERT INTO articulo (nombre, descripcion, precio_actual, stock) VALUES
  ('Camiseta básica', 'Camiseta 100% algodón, talla M', 19.99, 100),
  ('Auriculares bluetooth', 'Auriculares inalámbricos con cancelación', 49.50, 50),
  ('Taza cerámica', 'Taza 300ml, apta para lavavajillas', 5.00, 200);

-- Compras (ids autogenerados)
INSERT INTO compra (fecha_compra, estado, direccion_entrega, precio_total, cliente_nif_cif) VALUES
  ('2024-04-01 10:00:00', 'procesando', 'C/ Ejemplo 1, 28001 Madrid', 39.98, 'X1234567A'),
  ('2024-04-02 16:20:00', 'enviado',     'Av. Prueba 10, 08002 Barcelona', 64.50, 'Y7654321B');

-- Articulo_compra (relación muchos a muchos)
-- Compra 1: 2 unidades de Camiseta básica a 19.99 = 39.98
INSERT INTO articulo_compra (articulo_id, compra_id, unidades, precio_compra) VALUES
  (1, 1, 2, 19.99);

-- Compra 2: 1 Auriculares bluetooth a 49.50 + 3 Taza cerámica a 5.00 = 49.50 + 15.00 = 64.50
INSERT INTO articulo_compra (articulo_id, compra_id, unidades, precio_compra) VALUES
  (2, 2, 1, 49.50),
  (3, 2, 3, 5.00);


