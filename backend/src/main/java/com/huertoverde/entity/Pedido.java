package com.huertoverde.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

// Una compra confirmada desde el carrito de la tienda
@Entity
@Table(name = "pedidos")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Getter
@Setter
@NoArgsConstructor
public class Pedido {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	// Cliente que hizo la compra (el usuario con sesión iniciada en la tienda)
	@ManyToOne(fetch = FetchType.EAGER, optional = false)
	@JoinColumn(name = "usuario_id", nullable = false)
	private Usuario usuario;

	@Column(nullable = false)
	private LocalDateTime fecha;

	@Column(nullable = false, precision = 12, scale = 2)
	private BigDecimal subtotal;

	@Column(nullable = false, precision = 12, scale = 2)
	private BigDecimal despacho;

	@Column(nullable = false, precision = 12, scale = 2)
	private BigDecimal total;

	// Las líneas del pedido: se guardan junto con el pedido (cascade) y se leen junto con él (EAGER)
	@OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
	@OrderBy("id")
	private List<DetallePedido> detalles = new ArrayList<>();

	public void agregarDetalle(DetallePedido detalle) {
		detalle.setPedido(this);
		detalles.add(detalle);
	}
}
