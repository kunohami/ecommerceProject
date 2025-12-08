package com.silviarafa.ecommerce.ecommerceProject.model;

import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

/**
 * Representa la información fiscal asociada a un cliente en el sistemas de gestión de pedidos para un e-commerce.
 * <p>
 * Esta entidad se mapea a la tabla {@code informacion_fiscal} en la base de datos.
 * Contiene detalles fiscales del cliente, como su NIF/CIF, teléfono y dirección fiscal.
 * </p>
 * <h3>Relaciones</h3>
 * <ul>
 *     <li><b>Cliente (One-to-One):</b> Cada información fiscal está asociada a un único cliente. La relación es gestionada por la entidad {@link Cliente}.</li>
 * </ul>
 *
 * @author Silvia Balmaseda
 * @version 1.0
 * @since 2025-11-28
 */
@Entity
@Table(name = "informacion_fiscal")
public class InformacionFiscal {

	// Clave primaria compartida con 'Cliente'
	@Id
	@Column(name = "nif_cif")
	private String nifCif;

	@Column(name = "telefono")
	private String telefono;

	@Column(name = "direccion_fiscal")
	private String direccionFiscal;

	// Relación 1:1 con clave primaria compartida
	@OneToOne(fetch = FetchType.LAZY)
	@MapsId
	@JoinColumn(name = "nif_cif")
	private Cliente cliente;

	public InformacionFiscal() {
	}

	public String getNifCif() {
		return nifCif;
	}

	public void setNifCif(String id) {
		this.nifCif = id;
	}

	public String getTelefono() {
		return telefono;
	}

	public void setTelefono(String telefono) {
		this.telefono = telefono;
	}

	public String getDireccionFiscal() {
		return direccionFiscal;
	}

	public void setDireccionFiscal(String direccionFiscal) {
		this.direccionFiscal = direccionFiscal;
	}

	public Cliente getCliente() {
		return cliente;
	}

	public void setCliente(Cliente cliente) {
		this.cliente = cliente;
	}

	// --- toString, equals, hashCode ---

	@Override
	public String toString() {
		return "Informacion Fiscal: " + "NIF/CIF='" + nifCif + ", Teléfono=" + telefono + ", Dirección Fiscal=" + direccionFiscal + '.';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;

		if (o == null || getClass() != o.getClass() && !(o instanceof InformacionFiscal))
			return false;

		InformacionFiscal info = (InformacionFiscal) o;

		// Si ambos son nuevos (id null), compara por referencia
		if (nifCif == null && info.nifCif == null)
			return super.equals(o);

		return Objects.equals(nifCif, info.nifCif);
	}

	@Override
	public int hashCode() {
		return nifCif != null ? Objects.hash(nifCif) : System.identityHashCode(this);
	}
}