package com.huertoverde.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "productos")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Getter
@Setter
@NoArgsConstructor
public class Producto {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotBlank(message = "El código del producto es obligatorio")
	@Size(min = 3, max = 20, message = "El código debe tener entre 3 y 20 caracteres")
	@Column(nullable = false, unique = true, length = 20)
	private String codigo;

	@NotBlank(message = "El nombre del producto es obligatorio")
	@Size(max = 100, message = "El nombre no puede superar los 100 caracteres")
	@Column(nullable = false, length = 100)
	private String nombre;

	@NotNull(message = "Debe seleccionar una categoría: \"categoria\": {\"id\": ...}")
	@ManyToOne(fetch = FetchType.EAGER, optional = false)
	@JoinColumn(name = "categoria_id", nullable = false)
	private Categoria categoria;

	@NotBlank(message = "La unidad de venta es obligatoria")
	@Size(max = 40, message = "La unidad no puede superar los 40 caracteres")
	@Column(nullable = false, length = 40)
	private String unidad;

	@NotNull(message = "El precio es obligatorio")
	@DecimalMin(value = "0", message = "El precio no puede ser negativo")
	@Digits(integer = 10, fraction = 2, message = "El precio admite hasta 2 decimales")
	@Column(nullable = false, precision = 12, scale = 2)
	private BigDecimal precio;

	@NotNull(message = "El stock es obligatorio")
	@Min(value = 0, message = "El stock debe ser un número entero mayor o igual a 0")
	@Column(nullable = false)
	private Integer stock;

	@Min(value = 0, message = "El stock crítico debe ser un número entero igual o mayor a 0")
	private Integer stockCritico;

	// Ruta pública de la imagen (los archivos quedan en el frontend, fuera de la BD)
	@Size(max = 200, message = "La ruta de la imagen no puede superar los 200 caracteres")
	@Column(length = 200)
	private String imagen;

	@Size(max = 200, message = "El texto alternativo no puede superar los 200 caracteres")
	@Column(length = 200)
	private String alt;

	@Size(max = 500, message = "La descripción no puede superar los 500 caracteres")
	@Column(length = 500)
	private String descripcion;

	@Column(nullable = false)
	private boolean destacado;

	@Column(nullable = false)
	private boolean publicado = true;

	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	private LocalDateTime fechaModificacion;

	@PrePersist
	@PreUpdate
	void marcarModificacion() {
		fechaModificacion = LocalDateTime.now();
	}
}
