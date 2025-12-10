package com.silviarafa.ecommerce.ecommerceProject.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;

/**
 * Entidad que representa una línea dentro de una compra (relación N:N entre
 * {@link Articulo} y {@link Compra}) con unidades y precio unitario aplicado.
 * <p>
 * Se mapea a la tabla {@code articulo_compra}. La clave primaria compuesta
 * (articulo_id, compra_id) se gestiona mediante la clase embebida estática {@code ArticuloCompraId}
 * y se sincroniza usando {@code @MapsId} en ambas asociaciones foráneas.
 * </p>
 * <h3>Campos</h3>
 * <ul>
 *   <li><b>articulo</b>: Referencia al artículo.</li>
 *   <li><b>compra</b>: Referencia a la cabecera de la compra.</li>
 *   <li><b>unidades</b>: Número de unidades en esta línea.</li>
 *   <li><b>precioCompra</b>: Precio unitario congelado en el momento de la compra.</li>
 * </ul>
 * <h3>Observaciones</h3>
 * <ul>
 *   <li>Carga LAZY en las asociaciones para optimizar rendimiento.</li>
 *   <li>No se aplican cascadas hacia las entidades padre para evitar borrados accidentales.</li>
 *   <li>equals y hashCode basados en el identificador compuesto cuando está completo.</li>
 * </ul>
 * @author Rafael Robles
 * @version 1.0
 * @since 2025-11-30
 */
@Entity
@Table(name = "articulo_compra")
public class ArticuloCompra {

	@EmbeddedId
	private ArticuloCompraId id = new ArticuloCompraId();

	@ManyToOne(fetch = FetchType.LAZY)
	@MapsId("articuloId")
	@JoinColumn(name = "articulo_id", nullable = false)
	private Articulo articulo;

	@ManyToOne(fetch = FetchType.LAZY)
	@MapsId("compraId")
	@JoinColumn(name = "compra_id", nullable = false)
	private Compra compra;

	@Column(name = "unidades")
	private Integer unidades;

	@Column(name = "precio_compra", precision = 10, scale = 2)
	private BigDecimal precioCompra;

	public ArticuloCompra() {
	}

	public ArticuloCompra(Articulo articulo, Compra compra, Integer unidades, BigDecimal precioCompra) {
		this.articulo = articulo;
		this.compra = compra;
		this.unidades = unidades;
		this.precioCompra = precioCompra;
		if (articulo != null) {
			this.id.setArticuloId(articulo.getId());
		}
		// Compra id will be set once compra has its id (after persist) if null now.
		if (compra != null && compra.getId() != null) {
			this.id.setCompraId(compra.getId());
		}
	}

	public ArticuloCompraId getId() {
		return id;
	}

	public Articulo getArticulo() {
		return articulo;
	}

	public void setArticulo(Articulo articulo) {
		this.articulo = articulo;
		if (articulo != null) {
			this.id.setArticuloId(articulo.getId());
		} else {
			this.id.setArticuloId(null);
		}
	}

	public Compra getCompra() {
		return compra;
	}

	public void setCompra(Compra compra) {
		this.compra = compra;
		if (compra != null) {
			this.id.setCompraId(compra.getId());
		} else {
			this.id.setCompraId(null);
		}
	}

	public Integer getUnidades() {
		return unidades;
	}

	public void setUnidades(Integer unidades) {
		this.unidades = unidades;
	}

	public BigDecimal getPrecioCompra() {
		return precioCompra;
	}

	public void setPrecioCompra(BigDecimal precioCompra) {
		this.precioCompra = precioCompra;
	}

	@Override
	public String toString() {
		return "ArticuloCompra{" + "articuloId=" + id.getArticuloId() + ", compraId=" + id.getCompraId()
				+ ", unidades=" + unidades + ", precioCompra=" + precioCompra + '}';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass() && !(o instanceof ArticuloCompra))
			return false;
		ArticuloCompra other = (ArticuloCompra) o;
		// If both IDs missing treat as different (unsaved instances)
		if (this.id == null || other.id == null || this.id.getArticuloId() == null || this.id.getCompraId() == null
				|| other.id.getArticuloId() == null || other.id.getCompraId() == null) {
			return false;
		}
		return Objects.equals(this.id, other.id);
	}

	@Override
	public int hashCode() {
		return (id != null && id.getArticuloId() != null && id.getCompraId() != null) ? Objects.hash(id)
				: System.identityHashCode(this);
	}

	// --- Composite Key class ---
	/**
	 * Clase embebible que representa la clave primaria compuesta de {@link ArticuloCompra}.
	 * <p>
	 * Esta clase es necesaria porque en la base de datos la tabla "articulo_compra" tiene una clave primaria
	 * compuesta por dos columnas: "articulo_id" y "compra_id". Para mapear correctamente esta estructura
	 * en JPA, usamos una clase embebida que encapsula ambas claves. Esto permite que JPA gestione
	 * automáticamente las relaciones y facilite el uso de estas claves en el código.
	 * </p>
	 * <p>
	 * Usar una clase embebida hace que el código sea más limpio y reutilizable, ya que encapsula la lógica
	 * de la clave compuesta en un solo lugar.
	 * </p>
	 * <p>
	 * Si no se utilizara esta clase embebida, sería necesario manejar las claves primarias compuestas
	 * manualmente en cada entidad que las utilice. Esto implicaría definir múltiples campos para las claves
	 * en la entidad principal, escribir métodos personalizados para gestionar estas claves y realizar
	 * consultas más complejas. Además, el código sería más propenso a errores y menos mantenible.
	 * </p>
	 */
	@Embeddable
	public static class ArticuloCompraId implements Serializable {
		private static final long serialVersionUID = 1L;

		@Column(name = "articulo_id")
		private Integer articuloId;

		@Column(name = "compra_id")
		private Integer compraId;

		public ArticuloCompraId() {
		}

		public ArticuloCompraId(Integer articuloId, Integer compraId) {
			this.articuloId = articuloId;
			this.compraId = compraId;
		}

		public Integer getArticuloId() {
			return articuloId;
		}

		public void setArticuloId(Integer articuloId) {
			this.articuloId = articuloId;
		}

		public Integer getCompraId() {
			return compraId;
		}

		public void setCompraId(Integer compraId) {
			this.compraId = compraId;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o)
				return true;
			if (o == null || getClass() != o.getClass() && !(o instanceof ArticuloCompraId))
				return false;
			ArticuloCompraId other = (ArticuloCompraId) o;
			return Objects.equals(articuloId, other.articuloId) && Objects.equals(compraId, other.compraId);
		}

		@Override
		public int hashCode() {
			return (articuloId != null && compraId != null) ? Objects.hash(articuloId, compraId)
				: System.identityHashCode(this);
		}
	}
}