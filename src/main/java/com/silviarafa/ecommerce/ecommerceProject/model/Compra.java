package com.silviarafa.ecommerce.ecommerceProject.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

/**
 * Entidad que representa la cabecera de una compra, incluyendo detalles como dirección de entrega,
 * estado, precio total y las relaciones con el cliente y las líneas de artículos ({@link ArticuloCompra}).
 * <p>
 * Se mapea a la tabla {@code compra}.
 * </p>
 * <h3>Campos</h3>
 * <ul>
 *   <li><b>id</b>: Identificador único de la compra.</li>
 *   <li><b>fechaCompra</b>: Fecha y hora en que se realizó la compra.</li>
 *   <li><b>estado</b>: Estado actual de la compra (e.g., pendiente, completada).</li>
 *   <li><b>direccionEntrega</b>: Dirección donde se entregarán los productos.</li>
 *   <li><b>precioTotal</b>: Precio total de la compra.</li>
 *   <li><b>cliente</b>: Referencia al cliente que realizó la compra.</li>
 *   <li><b>lineas</b>: Conjunto de líneas de artículos asociadas a esta compra.</li>
 * </ul>
 * <h3>Observaciones</h3>
 * <ul>
 *   <li>Carga LAZY en las asociaciones para optimizar rendimiento.</li>
 *   <li>No se aplican cascadas hacia el cliente para evitar modificaciones accidentales.</li>
 *   <li>equals y hashCode basados en el identificador único.</li>
 * </ul>
 * @author Rafael Robles
 * @version 1.0
 * @since 2025-11-30
 */
@Entity
@Table(name = "compra")
public class Compra {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Integer id;

	@Column(name = "fecha_compra")
	private LocalDateTime fechaCompra;

	@Column(name = "estado")
	private String estado;

	@Column(name = "direccion_entrega")
	private String direccionEntrega;

	@Column(name = "precio_total", precision = 10, scale = 2, nullable = false)
	private BigDecimal precioTotal;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "cliente_nif_cif", nullable = true) // aquí nullable permite asignar null a compras antes de borrar al cliente
	private Cliente cliente;

	@OneToMany(mappedBy = "compra", fetch = FetchType.LAZY)
	private Set<ArticuloCompra> lineas = new HashSet<>();

	public Compra() {
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public LocalDateTime getFechaCompra() {
		return fechaCompra;
	}

	public void setFechaCompra(LocalDateTime fechaCompra) {
		this.fechaCompra = fechaCompra;
	}

	public String getEstado() {
		return estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}

	public String getDireccionEntrega() {
		return direccionEntrega;
	}

	public void setDireccionEntrega(String direccionEntrega) {
		this.direccionEntrega = direccionEntrega;
	}

	public BigDecimal getPrecioTotal() {
		return precioTotal;
	}

	public void setPrecioTotal(BigDecimal precioTotal) {
		this.precioTotal = precioTotal;
	}

	public Cliente getCliente() {
		return cliente;
	}

	public void setCliente(Cliente cliente) {
		this.cliente = cliente;
	}

	public Set<ArticuloCompra> getLineas() {
		return lineas;
	}

	public void setLineas(Set<ArticuloCompra> lineas) {
		this.lineas = lineas;
	}

	public void addLinea(ArticuloCompra linea) {
		lineas.add(linea);
		linea.setCompra(this);
	}

	public void removeLinea(ArticuloCompra linea) {
		lineas.remove(linea);
		linea.setCompra(null);
	}

	@Override
	public String toString() {
		return "Compra{" + "id=" + id + ", fechaCompra=" + fechaCompra + ", estado=" + estado
				+ ", direccionEntrega=" + direccionEntrega + ", precioTotal=" + precioTotal + '}';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass() && !(o instanceof Compra))
			return false;
		Compra other = (Compra) o;
		return Objects.equals(id, other.id);
	}

	@Override
	public int hashCode() {
		return id != null ? Objects.hash(id) : System.identityHashCode(this);
	}
}