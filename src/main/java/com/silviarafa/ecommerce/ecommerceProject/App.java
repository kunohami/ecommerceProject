package com.silviarafa.ecommerce.ecommerceProject;

import java.math.BigDecimal;
import java.time.LocalDateTime;

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

public class App {

    public static void main(String[] args) {
        // Configurar el EntityManagerFactory
        EntityManagerFactory emf = null;
        EntityManager em = null;

        try {
            emf = Persistence.createEntityManagerFactory("my_eshop-jpa-pu");
            em = emf.createEntityManager();

            // ----------------------------------------------------
            // LEER DATOS EXISTENTES (LAZY loading)
            // ----------------------------------------------------
            System.out.println("--- LEYENDO DATOS EXISTENTES ---");

            // A) Consultar un cliente y su información fiscal (1:1)
            System.out.println("\n[CONSULTA] Buscando Cliente ID 1...");
            Cliente cliente1 = em.find(Cliente.class, 1);

            if (cliente1 != null) {
                System.out.println("-> Cliente encontrado: " + cliente1.getNombreCompleto());
                System.out.println("-> NIF/CIF (LAZY 1:1): " + cliente1.getInformacionFiscal().getNifCif());
            } else {
                System.out.println("-> Cliente ID 1 no existe.");
            }

            // B) Consultar artículos existentes (LAZY)
            System.out.println("\n[CONSULTA] Listando artículos existentes...");
            var articulos = em.createQuery("SELECT a FROM Articulo a", Articulo.class).getResultList();
            for (Articulo a : articulos) {
                System.out.println("   - " + a.getNombre());
            }

            // ----------------------------------------------------
            // CREAR DATOS NUEVOS
            // ----------------------------------------------------
            System.out.println("\n--- CREANDO DATOS NUEVOS (usando una transacción) ---");

            EntityTransaction tx = em.getTransaction();
            tx.begin();

            try {
                // ========================================================
                // A) Crear cliente con información fiscal (1:1)
                // ========================================================
                Cliente nuevoCliente = new Cliente();
                nuevoCliente.setNifCif("00000000T");
                nuevoCliente.setNombreCompleto("Cliente Temporal");
                nuevoCliente.setEmail("cliente.temp@example.com");
                nuevoCliente.setFechaRegistro(LocalDateTime.now());
                
                InformacionFiscal info = new InformacionFiscal();
                info.setTelefono("666777888");
                info.setDireccionFiscal("Calle Temporal 123");

                // Helper 1:1
                nuevoCliente.setInformacionFiscal(info);

                System.out.println("\n[CREANDO] Cliente '" + nuevoCliente.getNombreCompleto() +
                        "' con su información fiscal asociada.");


                // ========================================================
                // B) Crear 2 artículos nuevos
                // ========================================================
                Articulo articulo1 = new Articulo();
                articulo1.setNombre("Teclado Mecánico RGB");
                articulo1.setDescripcion("Teclado gaming con switches azules.");
                articulo1.setPrecioActual(new BigDecimal(String.valueOf(39.99)));

                Articulo articulo2 = new Articulo();
                articulo2.setNombre("Ratón Inalámbrico Pro");
                articulo2.setDescripcion("Ratón ergonómico de alta precisión.");
                articulo2.setPrecioActual(new BigDecimal(String.valueOf(29.99)));
                

                System.out.println("[CREANDO] Artículos nuevos:");
                System.out.println("   - " + articulo1.getNombre());
                System.out.println("   - " + articulo2.getNombre());


                // ========================================================
                // C) Crear compra y líneas ArticuloCompra (@EmbeddedId)
                // ========================================================
                Compra compra = new Compra();
                compra.setFechaCompra(LocalDateTime.now());
                compra.setEstado("PENDIENTE");
                compra.setDireccionEntrega("Calle Temporal 1");
                compra.setPrecioTotal(new BigDecimal(String.valueOf(69.98)));
                compra.setCliente(nuevoCliente);

                System.out.println("[CREANDO] Compra nueva para el cliente: " + nuevoCliente.getNombreCompleto());
                
                em.persist(nuevoCliente);
                em.persist(articulo1);
                em.persist(articulo2);
                em.persist(compra);

                System.out.println("\n[FLUSH] Enviando INSERTs a la base de datos...");
                em.flush();

                // ---- Línea AC1
                /*
                ArticuloCompra ac1 = new ArticuloCompra();
                ArticuloCompraId id1 = new ArticuloCompraId();
                id1.setCompraId(compra.getId());
                id1.setArticuloId(articulo1.getId());

                ac1.setUnidades(2);
                ac1.setPrecioCompra(articulo1.getPrecioActual());

                // ---- Línea AC2
                ArticuloCompra ac2 = new ArticuloCompra();
                ArticuloCompraId id2 = new ArticuloCompraId();
                id2.setCompraId(compra.getId());
                id2.setArticuloId(articulo2.getId());

                ac2.setUnidades(2);
                ac2.setPrecioCompra(articulo2.getPrecioActual());
                */
                ArticuloCompra ac1 = new ArticuloCompra(articulo1, compra, 2, articulo1.getPrecioActual());
                ArticuloCompra ac2 = new ArticuloCompra(articulo2, compra, 2, articulo2.getPrecioActual());

                // Añadir líneas a la compra (helper)
                compra.addLinea(ac1);
                compra.addLinea(ac2);

                // ========================================================
                // D) Persistir (gracias a CascadeType)
                // ========================================================
                em.persist(ac1);
                em.persist(ac2);

                System.out.println("\n[FLUSH] Enviando el resto de INSERTs a la base de datos...");
                em.flush();

                System.out.println("\n[COMMIT] Guardando todos los cambios...");
                tx.commit();

            } catch (PersistenceException e) {
                System.err.println("!!! ERROR EN LA TRANSACCIÓN !!!");
                e.printStackTrace();
                if (tx.isActive()) {
                    tx.rollback();
                }
            }

        } catch (Exception e) {
            System.err.println("!!! ERROR AL INICIAR JPA !!!");
            e.printStackTrace();
        } finally {
            if (em != null) em.close();
            if (emf != null) emf.close();
            System.out.println("\n--- APLICACIÓN FINALIZADA ---");
        }
    }
}
