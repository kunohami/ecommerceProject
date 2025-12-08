package com.silviarafa.ecommerce.ecommerceProject.model;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

/**
 * Representa un cliente en el sistemas de gestión de pedidos para un e-commerce.
 * <p>
 * Esta entidad se mapea a la tabla {@code cliente} en la base de datos.
 * Contiene información básica sobre el cliente, como su NIF/CIF, nombre completo,
 * correo electrónico y fecha de registro.
 * </p>
 * <h3>Relaciones</h3>
 * <ul>
 *     <li><b>InformacionFiscal (One-to-One):</b> Cada cliente tiene una única información fiscal asociada. La relación es gestionada por la entidad {@link InformacionFiscal}.</li>
 *     <li><b>Compra (One-to-Many):</b> Un cliente puede tener múltiples compras asociadas. La relación es gestionada por la entidad {@link Compra}.</li>
 * </ul>
 *
 * @author Silvia Balmaseda
 * @version 1.0
 * @since 2025-11-28
 */
@Entity
@Table(name = "cliente")
public class Cliente {

	@Id
	@Column(name = "nif_cif")
	private String nifCif;

	@Column(name = "nombre_completo", nullable = false)
	private String nombreCompleto;

	@Column(name = "email", nullable = true, unique = false)
	private String email;

	@Column(name = "fecha_registro", nullable = true)
	private LocalDateTime fechaRegistro;

	// --- RELACIÓN 1:1 ---
	@OneToOne(mappedBy = "cliente", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	private InformacionFiscal informacionFiscal;

	// --- RELACIÓN 1:N ---
	// @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	// private Set<Compra> compras = new HashSet<>();

	//Elimina orphanRemoval = true y cambia CascadeType.ALL por operaciones específicas que no incluyan el borrado (PERSIST y MERGE).
	@OneToMany(mappedBy = "cliente", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
	private Set<Compra> compras = new HashSet<>();

	public Cliente() {
	}

	public String getNifCif() {
		return nifCif;
	}

	public void setNifCif(String nifCif) {
		this.nifCif = nifCif;
	}

	public String getNombreCompleto() {
		return nombreCompleto;
	}

	public void setNombreCompleto(String nombreCompleto) {
		this.nombreCompleto = nombreCompleto;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public LocalDateTime getFechaRegistro() {
		return fechaRegistro;
	}

	public void setFechaRegistro(LocalDateTime fechaRegistro) {
		this.fechaRegistro = fechaRegistro;
	}

	public InformacionFiscal getInformacionFiscal() {
		return informacionFiscal;
	}

	/* Sincronizar relación 1:1 bidireccional. */
	public void setInformacionFiscal(InformacionFiscal info) {
		if (info == null) {
			if (this.informacionFiscal != null) {
				this.informacionFiscal.setCliente(null);
			}
		} else {
			info.setCliente(this);
		}
		this.informacionFiscal = info;
	}

	/* Métodos para la relación 1:N */
	public Set<Compra> getCompras() {
		return compras;
	}

	public void setCompras(Set<Compra> compras) {
		this.compras = compras;
	}

	/**
	 * Añadir una compra y sincronizar la relación 1:N bidireccional.
	 */
	public void addCompra(Compra compra) {
		compras.add(compra);
		compra.setCliente(this);
	}

	/**
	 * Eliminar una compra y sincronizar la relación 1:N bidireccional.
	 */
	public void removeCompra(Compra compra) {
		compras.remove(compra);
		compra.setCliente(null);
	}

	public int getNumCompras() {
		return compras != null ? compras.size() : 0;
	}

	// --- toString, equals, hashCode ---

	@Override
	public String toString() {
		return "Cliente: " + "NIF/CIF=" + nifCif + ", Nombre Completo=" + nombreCompleto + ", Email="
				+ email + ", Fecha Registro=" + fechaRegistro + ".\n"
				+ (informacionFiscal != null ? informacionFiscal.toString() : "Sin información fiscal") 
				+ "\nNúmero de compras=" + getNumCompras() + '.';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;

		if (o == null || getClass() != o.getClass() && !(o instanceof Cliente))
			return false;

		Cliente cliente = (Cliente) o;

		return Objects.equals(nifCif, cliente.nifCif);
	}

	@Override
	public int hashCode() {
		return nifCif != null ? Objects.hash(nifCif) : System.identityHashCode(this);
	}
}
