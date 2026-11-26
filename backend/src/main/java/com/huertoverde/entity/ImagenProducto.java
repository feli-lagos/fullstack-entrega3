package com.huertoverde.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// La foto de un producto, guardada dentro de la BD (tabla imagenes_producto).
// Va en una tabla aparte para que listar productos no cargue los bytes de todas las fotos:
// la imagen solo se lee cuando el navegador la pide en GET /api/productos/{id}/imagen.
@Entity
@Table(name = "imagenes_producto")
@Getter
@Setter
@NoArgsConstructor
public class ImagenProducto {

	// Usa el mismo id del producto (@MapsId): un producto tiene como máximo una imagen
	@Id
	private Long productoId;

	@OneToOne(fetch = FetchType.LAZY, optional = false)
	@MapsId
	@JoinColumn(name = "producto_id")
	private Producto producto;

	// Los bytes del archivo (jpg, png...). @Lob = columna BLOB en H2
	@Lob
	@Column(nullable = false)
	private byte[] datos;

	// Tipo del archivo ("image/jpeg", "image/png"...), para que el navegador sepa cómo mostrarlo
	@Column(nullable = false, length = 50)
	private String tipo;
}
