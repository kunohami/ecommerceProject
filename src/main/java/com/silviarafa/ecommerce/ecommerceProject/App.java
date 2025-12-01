package com.silviarafa.ecommerce.ecommerceProject;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.silviarafa.ecommerce.ecommerceProject.model.Articulo;
import com.silviarafa.ecommerce.ecommerceProject.model.ArticuloCompra;
import com.silviarafa.ecommerce.ecommerceProject.model.Cliente;
import com.silviarafa.ecommerce.ecommerceProject.model.Compra;
import com.silviarafa.ecommerce.ecommerceProject.model.InformacionFiscal;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import jakarta.persistence.PersistenceException;

/**
 * Aplicación principal de pruebas (Test Runner).
 * <p>
 * Ejecuta secuencialmente: 1. Lectura de datos existentes (probar conexión y
 * mapeo). 2. Creación de un escenario completo de venta (Cliente -> Compra ->
 * Artículos). 3. Prueba de integridad: Borrado del cliente para verificar que
 * la compra persiste (ON DELETE SET NULL).
 * </p>
 * @authors Silvia Balmaseda, Rafael Robles
 */
public class App {

	public static void main(String[] args) {
		// Configurar el EntityManagerFactory
		EntityManagerFactory emf = null;
		EntityManager em = null;

		// Variables para pasar datos de la prueba de CREACIÓN a la de BORRADO
		String nifClienteCreado = null;
		Integer idCompraCreada = null;

		try {
			// NOTA IMPORTANTE: Corregido a "my_sehop-jpa-pu" para coincidir con tu
			// persistence.xml
			emf = Persistence.createEntityManagerFactory("my_eshop-jpa-pu");
			em = emf.createEntityManager();

			// ====================================================
			// PRUEBA 1: LEER DATOS EXISTENTES (LAZY loading)
			// ====================================================
			/**
			 * Valida que Hibernate puede leer datos del script SQL inicial y que la
			 * relación Lazy con Información Fiscal funciona.
			 */
			System.out.println("--- 1. LEYENDO DATOS EXISTENTES ---");

			// A) Consultar un cliente y su información fiscal (1:1)
			// CAMBIO: Usamos un String (NIF) porque el ID en Cliente es String, no int.
			String nifExistente = "X1234567A";
			System.out.println("\n[CONSULTA] Buscando Cliente NIF " + nifExistente + "...");
			Cliente cliente1 = em.find(Cliente.class, nifExistente);

			if (cliente1 != null) {
				System.out.println("-> Cliente encontrado: " + cliente1.getNombreCompleto());
				// Acceso a LAZY para probar el proxy
				if (cliente1.getInformacionFiscal() != null) {
					System.out.println("-> NIF/CIF (LAZY 1:1): " + cliente1.getInformacionFiscal().getNifCif());
				}
			} else {
				System.out.println("-> Cliente " + nifExistente + " no existe (¿Se ejecutó el script SQL?).");
			}

			// B) Consultar artículos existentes (LAZY)
			System.out.println("\n[CONSULTA] Listando artículos existentes...");
			List<Articulo> articulos = em.createQuery("SELECT a FROM Articulo a", Articulo.class).getResultList();
			for (Articulo a : articulos) {
				System.out.println("   - " + a.getNombre());
			}

			// ====================================================
			// PRUEBA 2: CREAR DATOS NUEVOS
			// ====================================================
			/**
			 * Valida la persistencia en cascada y la inserción compleja en la tabla
			 * intermedia ArticuloCompra con claves compuestas.
			 */
			System.out.println("\n--- 2. CREANDO DATOS NUEVOS (Transacción 1) ---");

			EntityTransaction tx = em.getTransaction();
			tx.begin();

			try {
				String nifTest = "00000000T";

				// --- LIMPIEZA DE SEGURIDAD (CORRECCIÓN ERROR DUPLICATE ENTRY) ---
				// Si se ha ejecutado el programa antes, el cliente ya existe. Lo borramos para
				// empezar limpios.
				Cliente clientePrevio = em.find(Cliente.class, nifTest);
				if (clientePrevio != null) {
					System.out.println("!!! AVISO: El cliente " + nifTest + " ya existía de una prueba anterior.");
					System.out.println("!!! Limpiando datos antiguos para reiniciar la prueba...");
					for (Compra c : clientePrevio.getCompras()) {
						c.setCliente(null);
					}
					em.remove(clientePrevio);
					em.flush();
					System.out.println("!!! Limpieza completada.");
				}
				// ----------------------------------------------------------------

				// A) Crear cliente con información fiscal (1:1)
				Cliente nuevoCliente = new Cliente();
				nuevoCliente.setNifCif(nifTest);
				nuevoCliente.setNombreCompleto("Cliente Temporal");
				nuevoCliente.setEmail("cliente.temp@example.com");
				nuevoCliente.setFechaRegistro(LocalDateTime.now());

				InformacionFiscal info = new InformacionFiscal();
				info.setTelefono("666777888");
				info.setDireccionFiscal("Calle Temporal 123");

				// Helper 1:1
				nuevoCliente.setInformacionFiscal(info);

				System.out.println("\n[CREANDO] Cliente '" + nuevoCliente.getNombreCompleto()
						+ "' con su información fiscal asociada.");

				// B) Crear 2 artículos nuevos
				Articulo articulo1 = new Articulo();
				articulo1.setNombre("Teclado Mecánico RGB");
				articulo1.setDescripcion("Teclado gaming con switches azules.");
				articulo1.setPrecioActual(new BigDecimal("39.99"));

				Articulo articulo2 = new Articulo();
				articulo2.setNombre("Ratón Inalámbrico Pro");
				articulo2.setDescripcion("Ratón ergonómico de alta precisión.");
				articulo2.setPrecioActual(new BigDecimal("29.99"));

				System.out.println("[CREANDO] Artículos nuevos:");
				System.out.println("   - " + articulo1.getNombre());
				System.out.println("   - " + articulo2.getNombre());

				// C) Crear compra y líneas ArticuloCompra (@EmbeddedId)
				Compra compra = new Compra();
				compra.setFechaCompra(LocalDateTime.now());
				compra.setEstado("PENDIENTE");
				compra.setDireccionEntrega("Calle Temporal 1");
				compra.setPrecioTotal(new BigDecimal("69.98"));
				compra.setCliente(nuevoCliente);

				System.out.println("[CREANDO] Compra nueva para el cliente: " + nuevoCliente.getNombreCompleto());

				em.persist(nuevoCliente);
				em.persist(articulo1);
				em.persist(articulo2);
				em.persist(compra);

				System.out.println("\n[FLUSH] Enviando INSERTs a la base de datos (Generando IDs)...");
				em.flush();

				// D) Crear las líneas usando los objetos ya persistidos
				ArticuloCompra ac1 = new ArticuloCompra(articulo1, compra, 2, articulo1.getPrecioActual());
				ArticuloCompra ac2 = new ArticuloCompra(articulo2, compra, 2, articulo2.getPrecioActual());

				// Añadir líneas a la compra (helper)
				compra.addLinea(ac1);
				compra.addLinea(ac2);

				em.persist(ac1);
				em.persist(ac2);

				System.out.println("\n[FLUSH] Enviando el resto de INSERTs a la base de datos...");
				em.flush();

				System.out.println("\n[COMMIT] Guardando todos los cambios...");
				tx.commit();

				// GUARDAMOS LOS DATOS PARA LA SIGUIENTE PRUEBA
				nifClienteCreado = nuevoCliente.getNifCif();
				idCompraCreada = compra.getId();
				System.out.println("-> Datos guardados. NIF: " + nifClienteCreado + " | ID Compra: " + idCompraCreada);

			} catch (PersistenceException e) {
				System.err.println("!!! ERROR EN LA TRANSACCIÓN !!!");
				e.printStackTrace();
				if (tx.isActive()) {
					tx.rollback();
				}
			}

			// ====================================================================================
			// LIMPIEZA DE CACHÉ
			// ====================================================================================
			// Esto fuerza a Hibernate a olvidar los objetos en memoria y recargarlos de la BD
			// en la siguiente transacción. Evita el error "TransientObjectException".
			em.clear();
			System.out.println("\n[INFO] Contexto de persistencia limpiado (em.clear()).");

			// ====================================================
			// PRUEBA 3: BORRADO DE CLIENTE (INTEGRIDAD)
			// ====================================================
			/**
			 * Valida que al borrar el cliente, la compra NO se borra, sino que su campo
			 * 'cliente' pasa a NULL (ON DELETE SET NULL).
			 */
			if (nifClienteCreado != null && idCompraCreada != null) {
				System.out.println("\n--- 3. BORRADO DE CLIENTE (Transacción 2) ---");

				EntityTransaction txBorrado = em.getTransaction();
				txBorrado.begin();

				try {
					// 1. Buscamos al cliente que acabamos de crear
					Cliente clienteABorrar = em.find(Cliente.class, nifClienteCreado);

					if (clienteABorrar != null) {
						System.out.println("-> Borrando cliente: " + clienteABorrar.getNombreCompleto());

						// IMPORTANTE: Desvincular en memoria Java para evitar conflictos de caché
						// aunque la BD lo haría sola, Java necesita saberlo.
						for (Compra c : clienteABorrar.getCompras()) {
							c.setCliente(null);
						}

						// 2. Ejecutamos el borrado
						em.remove(clienteABorrar);

						// 3. Forzamos el SQL DELETE inmediato
						em.flush();

						// 4.  Limpiamos la caché de Hibernate.
						// Si no hacemos esto, em.find() nos devolvería la 'compra' vieja que tiene en
						// memoria
						// y pensaríamos que el cliente sigue ahí. Queremos leer la verdad de la BBDD.
						em.clear();

						System.out
								.println("-> Contexto limpiado (em.clear()). Verificando estado de la compra en BD...");

						// 5. Verificamos la supervivencia de la compra
						Compra compraHuerfana = em.find(Compra.class, idCompraCreada);

						if (compraHuerfana != null) {
							System.out.println("¡ÉXITO! La compra ID " + idCompraCreada + " sigue existiendo.");

							if (compraHuerfana.getCliente() == null) {
								System.out.println("CORRECTO: El cliente de la compra es NULL (Compra anónima).");
								System.out.println(
										"Líneas de la compra mantenidas: " + compraHuerfana.getLineas().size());
							} else {
								System.err.println("FALLO: La compra en Java todavía apunta a un cliente.");
							}
						} else {
							System.err.println("FALLO CRÍTICO: La compra fue eliminada junto con el cliente.");
						}
					} else {
						System.err.println("No se pudo encontrar el cliente para borrar.");
					}

					txBorrado.commit();

				} catch (Exception e) {
					if (txBorrado.isActive())
						txBorrado.rollback();
					System.err.println("Error en prueba de borrado: " + e.getMessage());
					e.printStackTrace();
				}
			}

		} catch (Exception e) {
			System.err.println("!!! ERROR AL INICIAR JPA !!!");
			e.printStackTrace();
		} finally {
			if (em != null)
				em.close();
			if (emf != null)
				emf.close();
			System.out.println("\n--- APLICACIÓN FINALIZADA ---");
		}
	}
}