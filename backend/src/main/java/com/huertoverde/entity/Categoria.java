package com.huertoverde.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "categorias")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Categoria {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	// Identificador corto usado en la tienda para filtrar (frutas, verduras, ...)
	@Column(nullable = false, unique = true, length = 30)
	private String codigo;

	@Column(nullable = false, length = 60)
	private String nombre;

	@OneToMany(mappedBy = "categoria")
	@JsonIgnore
	private List<Producto> productos = new ArrayList<>();
}
