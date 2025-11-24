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
  FOREIGN KEY (nif_cif) REFERENCES cliente (email) 
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

