package com.silviarafa.ecommerce.ecommerceProject.model;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

/**
 * Representa un artículo en la tienda online.
 * <p>
 * Esta entidad se mapea a la tabla {@code articulo} en la base de datos.
 * Contiene información básica sobre el producto, como su nombre, descripción,
 * precio y el stock disponible.
 * </p>
 * <h3>Relaciones</h3>
 * <ul>
 *     <li><b>ArticuloCompra (One-to-Many):</b> Un artículo puede estar presente en muchas líneas de compra. La relación es gestionada por la entidad {@link ArticuloCompra}.</li>
 * </ul>
 *
 * @author Rafael Robles
 * @version 1.0
 * @since 2025-11-30
 */
@Entity
@Table(name = "articulo")
public class Articulo {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Integer id;

	@Column(name = "nombre")
	private String nombre;

	@Column(name = "descripcion", columnDefinition = "text")
	@Basic(fetch = FetchType.LAZY)
	private String descripcion;

	@Column(name = "precio_actual", precision = 10, scale = 2)
	private BigDecimal precioActual;

	@Column(name = "stock")
	private Integer stock;

	@OneToMany(mappedBy = "articulo", fetch = FetchType.LAZY) // Para que JPA no borre ArticuloCompra cuando se borre Articulo.
	private Set<ArticuloCompra> compras = new HashSet<>();

	public Articulo() {
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public BigDecimal getPrecioActual() {
		return precioActual;
	}

	public void setPrecioActual(BigDecimal precioActual) {
		this.precioActual = precioActual;
	}

	public Integer getStock() {
		return stock;
	}

	public void setStock(Integer stock) {
		this.stock = stock;
	}

	public Set<ArticuloCompra> getCompras() {
		return compras;
	}

	public void setCompras(Set<ArticuloCompra> compras) {
		this.compras = compras;
	}

	public void addCompra(ArticuloCompra compra) {
		compras.add(compra);
		compra.setArticulo(this);
	}

	public void removeCompra(ArticuloCompra compra) {
		compras.remove(compra);
		compra.setArticulo(null);
	}

	@Override
	public String toString() {
		return nombre + ", Descripción=" + descripcion + ", Precio Actual=" + precioActual + ", Stock=" + stock + ".";
	}
	/*
	public String toString() {
		return "Articulo [id=" + id + ", nombre=" + nombre + ", descripcion=" + descripcion + ", precioActual="
				+ precioActual + ", stock=" + stock + "]";
	}*/

	@Override
	public int hashCode() {
		return id != null ? Objects.hash(id) : System.identityHashCode(this);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass() && !(obj instanceof Articulo))
			return false;
		Articulo other = (Articulo) obj;
		return Objects.equals(id, other.id);
	}

}
