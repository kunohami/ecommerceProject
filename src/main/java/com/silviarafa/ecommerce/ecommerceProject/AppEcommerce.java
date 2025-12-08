package com.silviarafa.ecommerce.ecommerceProject;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.silviarafa.ecommerce.ecommerceProject.model.*;
import jakarta.persistence.*;

public class AppEcommerce {

	public static void main(String[] args) {

		EntityManagerFactory emf = null;
		EntityManager em = null;

		try {
			emf = Persistence.createEntityManagerFactory("my_eshop-jpa-pu");
			em = emf.createEntityManager();
			
			// X1234567A
			String nif = "00000000T";

			// 1. LEER DATOS CLIENTE
//			leerDatosCliente(em, nif);

			// 2. LEER DATOS ARTICULO
//			leerDatosArticulo(em);

			// 3. CREAR CLIENTE
			Cliente clienteNuevo = crearCliente(em); 
			leerDatosCliente(em, clienteNuevo.getNifCif());

			// 4. CREAR ARTICULO
			crearArticulo(em);
			leerDatosArticulo(em);

			// 5. CREAR COMPRA
			// Para añadir varíos artículos a una compra.
			Map<Articulo, Integer> carrito = new HashMap<>(); 
			carrito.put(em.find(Articulo.class, 1), 2); // Camiseta básica
			carrito.put(em.find(Articulo.class, 3), 3); // Taza Cerámica
			
			Compra c = crearCompra(em, em.find(Cliente.class, nif), carrito);
			System.out.println(c);

			// 6. UPDATE CLIENTE
			boolean updateC = actualizarCliente(em, nif);
			System.out.println(updateC);

			// 7. UPDATE ARTICULO
			boolean updateA = actualizarArticulo(em, 4);
			System.out.println(updateA);

			// 8. DELETE CLIENTE
			boolean deleteC = borrarCliente(em, nif);
			System.out.println(deleteC);

			// 9. DELETE ARTICULO
			boolean deleteA = borrarArticulo(em, 4);
			System.out.println(deleteA);

		} catch (IllegalArgumentException iae) {
			System.out.println(iae.getMessage());
		} catch (NullPointerException npe) {
			System.out.println(npe.getMessage());
		} catch (IllegalStateException ise) {
			System.out.println(ise.getMessage());
		} catch (TransactionRequiredException tre) {
			System.out.println(tre.getMessage());
		} catch (Exception e) {
			System.err.println("\nERROR GENERAL: " + e.getMessage());
		} finally {
			if (em != null)
				em.close();
			if (emf != null)
				emf.close();
			System.out.println("\n--- APLICACIÓN FINALIZADA ---");
		}
	}

	// 1. LEER DATOS CLIENTE
	public static void leerDatosCliente(EntityManager em, String nif)
			throws IllegalArgumentException, NullPointerException, Exception {
		System.out.println("\n--- 1. LEYENDO DATOS ---");

		Cliente cliente = em.find(Cliente.class, nif);

		if (cliente != null) {
			System.out.println("DATOS DEL CLIENTE: \n" + cliente.toString());
		} else {
			System.out.println("No existe ese cliente con ese nif: " + nif);
		}
	}

	// 2. LEER DATOS ARTICULO
	public static void leerDatosArticulo(EntityManager em)
			throws IllegalArgumentException, NullPointerException, Exception {
		System.out.println("\n--- 2. LEYENDO DATOS ARTICULO ---");

		System.out.println("\nArtículos:");
		List<Articulo> arts = em.createQuery("SELECT a FROM Articulo a", Articulo.class).getResultList();
		System.out.println("\n DATOS ARTÍCULOS");
		arts.forEach(a -> System.out.println("  - " + a.toString()));
	}

	// 3. CREAR CLIENTE
	public static Cliente crearCliente(EntityManager em)
			throws IllegalArgumentException, TransactionRequiredException, IllegalStateException, Exception {
		System.out.println("\n--- CREANDO Cliente ---");

		EntityTransaction tx = em.getTransaction();
		tx.begin();

		Cliente cli = new Cliente();
		String nif = "00000000T";

		try {
			Cliente esta = em.find(Cliente.class, nif);
			if (esta != null) {
				System.out.println("Este NIF/CIF: " + nif + " ya existe para un cliente, pon otro.");
				return null;
			}

			cli.setNifCif(nif);
			cli.setNombreCompleto("Cliente Temporal");
			cli.setEmail("cliente.temp@example.com");
			cli.setFechaRegistro(LocalDateTime.now());

			InformacionFiscal info = new InformacionFiscal();
			info.setNifCif(nif);
			info.setTelefono("666777888");
			info.setDireccionFiscal("Calle Temporal 123");
			cli.setInformacionFiscal(info);

			em.persist(cli);

			em.flush();
			tx.commit();

		} catch (Exception e) {
			if (tx.isActive())
				tx.rollback();
			throw e;
		} finally {
			em.clear();
		}

		return cli;
	}

	// 4. CREAR ARTICULO
	public static Articulo crearArticulo(EntityManager em)
			throws IllegalArgumentException, IllegalStateException, TransactionRequiredException {
		System.out.println("\n--- CREANDO Artículo ---");

		EntityTransaction tx = em.getTransaction();
		tx.begin();
		Articulo articulo = new Articulo();
		try {

			articulo.setNombre("Teclado Mecánico RGB");
			articulo.setDescripcion("Teclado gaming con switches azules.");
			articulo.setPrecioActual(new BigDecimal("39.99"));
			articulo.setStock(50);

			em.persist(articulo);
			em.flush();
			tx.commit();
		} catch (Exception e) {
			if (tx.isActive())
				tx.rollback();
			throw e;
		} finally {
			em.clear();
		}

		return articulo;
	}

	// 5. CREAR COMPRA
	public static Compra crearCompra(EntityManager em, Cliente cliente, Map<Articulo, Integer> articuloUnidades)
			throws IllegalArgumentException, IllegalStateException, TransactionRequiredException {
		System.out.println("\n--- CREANDO Compra ---");

		EntityTransaction tx = em.getTransaction();
		tx.begin();
		Compra compra = new Compra();

		try {
			compra.setFechaCompra(LocalDateTime.now());
			compra.setEstado("PENDIENTE");
			compra.setDireccionEntrega("Calle Temporal 1");
			compra.setPrecioTotal(new BigDecimal("0.00"));
			compra.setCliente(cliente);

			em.persist(compra);
			em.flush();

			BigDecimal precioTotal = BigDecimal.ZERO;

			// Recorrer el mapa de artículos y unidades
			for (Map.Entry<Articulo, Integer> entry : articuloUnidades.entrySet()) {
				Articulo articulo = entry.getKey();
				int cantidad = entry.getValue();

				// Calcular el precio_compra de artículo compra
				BigDecimal precioCompra = articulo.getPrecioActual().multiply(BigDecimal.valueOf(cantidad));
				ArticuloCompra artCom = new ArticuloCompra(articulo, compra, cantidad, precioCompra);

				compra.addLinea(artCom);
				em.persist(artCom);

				precioTotal = precioTotal.add(precioCompra);
			}

			compra.setPrecioTotal(precioTotal);

			em.persist(compra);
			em.flush();
			tx.commit();
		} catch (Exception e) {
			if (tx.isActive())
				tx.rollback();
			throw e;
		} finally {
			em.clear();
		}

		return compra;
	}

	// 6. UPDATE CLIENTE
	public static boolean actualizarCliente(EntityManager em, String nif)
			throws IllegalArgumentException, IllegalStateException, TransactionRequiredException {
		System.out.println("\n--- ACTUALIZANDO CLIENTE ---");

		EntityTransaction tx = em.getTransaction();
		tx.begin();
		boolean actualizado = true;

		try {
			Cliente cli = em.find(Cliente.class, nif);
			if (cli == null)
				return false;

			cli.setNombreCompleto("Luis Pérez (Actualizado)");
			cli.setEmail("luis.perez.actualizado@example.com");

			if (cli.getInformacionFiscal() == null) {
				InformacionFiscal f = new InformacionFiscal();
				f.setTelefono("+34 600 999 000");
				f.setDireccionFiscal("Av. Prueba 10, 08002 Barcelona (Act)");
				cli.setInformacionFiscal(f);
			} else {
				cli.getInformacionFiscal().setTelefono("+34 600 999 000");
				cli.getInformacionFiscal().setDireccionFiscal("Av. Prueba 10, 08002 Barcelona (Act)");
			}

			if (!cli.getCompras().isEmpty()) {
				// 'PENDIENTE', 'ENVIADO' o 'ENTREGADO'
				cli.getCompras().iterator().next().setEstado("ENVIADO");
			}

			em.merge(cli);
			em.flush();
			tx.commit();
		} catch (Exception e) {
			actualizado = false;
			if (tx.isActive())
				tx.rollback();
			throw e;
		} finally {
			em.clear();
		}

		return actualizado;
	}

	// 7. UPDATE ARTICULO
	public static boolean actualizarArticulo(EntityManager em, int id)
			throws IllegalArgumentException, IllegalStateException, TransactionRequiredException {

		EntityTransaction tx = em.getTransaction();
		tx.begin();
		boolean actualizado = true;

		try {
			Articulo articulo = em.find(Articulo.class, id);

			if (articulo == null) {
				System.out.println("Artículo no encontrado con ID: " + id);
				return false;
			}

			articulo.setNombre("Teclado Mecánico");
			articulo.setDescripcion("Teclado gaming con switches azules y verdes.");
			articulo.setPrecioActual(new BigDecimal("40.00"));
			articulo.setStock(10);

			em.merge(articulo);
			em.flush();
			tx.commit();
		} catch (Exception e) {
			actualizado = false;
			if (tx.isActive())
				tx.rollback();
			throw e;
		} finally {
			em.clear();
		}

		return actualizado;
	}

	// 8. DELETE CLIENTE
	public static boolean borrarCliente(EntityManager em, String nif)
			throws IllegalArgumentException, IllegalStateException, TransactionRequiredException {

		EntityTransaction tx = em.getTransaction();
		tx.begin();
		boolean borrado = true;

		try {
			Cliente cliente = em.find(Cliente.class, nif);
			if (cliente == null) {
				System.out.println("Cliente con NIF " + nif + " no encontrado.");
				return false;
			}

			// Desvincular las compras para que no haya restricción
			if (cliente.getCompras() != null) {
				for (Compra c : cliente.getCompras()) {
					c.setFechaCompra(null);
					c.setEstado("");
					c.setDireccionEntrega("");
					c.setPrecioTotal(new BigDecimal("0.00"));
					c.setCliente(null);
				}
			}

			// La relación 1:1 con InformacionFiscal se elimina por cascade
			em.remove(cliente);
			em.flush();
			tx.commit();
		} catch (Exception e) {
			borrado = false;
			if (tx.isActive())
				tx.rollback();
			throw e;
		} finally {
			em.clear();
		}

		return borrado;
	}

	// 9. DELETE ARTICULO
	public static boolean borrarArticulo(EntityManager em, int id)
			throws IllegalArgumentException, IllegalStateException, TransactionRequiredException {

		EntityTransaction tx = em.getTransaction();
		tx.begin();
		boolean borrado = true;

		try {
			Articulo articulo = em.find(Articulo.class, id);
			if (articulo == null) {
				System.out.println("Artículo con ID " + id + " no encontrado.");
				return false;
			}

			// Desvincular las líneas de compra
			if (articulo.getCompras() != null) {
				for (ArticuloCompra a : articulo.getCompras()) {
					a.setUnidades(0);
					a.setPrecioCompra(new BigDecimal("0.00"));
				}
			}

			// "Eliminar" el artículo
			articulo.setNombre("");
			articulo.setDescripcion("");
			articulo.setPrecioActual(new BigDecimal("0.00"));
			articulo.setStock(0);

			em.flush();
			tx.commit();
		} catch (Exception e) {
			borrado = false;
			if (tx.isActive())
				tx.rollback();
			throw e;
		} finally {
			em.clear();
		}
		return borrado;
	}

}
