package com.silviarafa.ecommerce.ecommerceProject.model;


import java.time.LocalDateTime;
import java.util.Objects;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "clientes")
public class Cliente {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "nif_cif")
	private String nif_cif;
	
	@Column(name = "nombre_completo", length = 100, nullable = false)
    private String nombreCompleto;

    @Column(name = "email", length = 150, unique = true, nullable = false)
    private String email;

    @CreationTimestamp
    @Column(name = "fecha_registro", updatable = false)
    private LocalDateTime fechaRegistro;

    // --- CONSTRUCTORES ---
    public Cliente() {
    }

    public Cliente(String nif_cif, String nombreCompleto, String email) {
        this.nif_cif = nif_cif;
        this.nombreCompleto = nombreCompleto;
        this.email = email;
    }

    // --- GETTERS Y SETTERS ---

    public String getNifCif() {
        return nif_cif;
    }

    public void setNifCif(String nif_cif) {
        this.nif_cif = nif_cif;
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

    // --- toString, equals, hashCode ---

    @Override
    public String toString() {
        return "Cliente{" +
                "nifCif='" + nif_cif + '\'' +
                ", nombreCompleto='" + nombreCompleto + '\'' +
                ", email='" + email + '\'' +
                ", fechaRegistro=" + fechaRegistro +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Cliente cliente = (Cliente) o;

        return Objects.equals(nif_cif, cliente.nif_cif);
    }

    @Override
    public int hashCode() {
        return nif_cif != null ? nif_cif.hashCode() : 0;
    }
	

}
