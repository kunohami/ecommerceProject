-- Crear la base de datos "my_eshop_db" con codificación UTF-8
/*
CREATE DATABASE my_eshop_db 
	CHARACTER SET utf8mb4 
	COLLATE utf8mb4_es_0900_ai_ci;
	*/
	
-- Creamos el usuario administrador para esta base de datos (y le damos permisos)
CREATE USER 'usuarios_db'@'%' IDENTIFIED BY 'Abcd1234';
GRANT ALL PRIVILEGES ON usuarios_db.* TO 'usuarios_db'@'%';
FLUSH PRIVILEGES;

-- Usar la base de datos creada
USE usuarios_db;

-- Mostrar el estado de la base de datos creada
SHOW TABLE STATUS FROM usuarios_db;