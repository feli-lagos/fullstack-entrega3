package com.huertoverde.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

// Lo que manda el carrito al pagar: quién compra y qué productos lleva.
// No trae precios: el backend los saca de la base de datos para que nadie pueda cambiarlos desde el navegador.
public record PedidoRequest(
		@NotNull(message = "Debe indicar el usuario que compra") Long usuarioId,
		@NotEmpty(message = "El carrito está vacío") List<@Valid @NotNull Item> items) {

	public record Item(
			@NotNull(message = "Falta el id del producto") Long productoId,
			@NotNull(message = "Falta la cantidad")
			@Min(value = 1, message = "La cantidad debe ser al menos 1") Integer cantidad) {
	}
}
