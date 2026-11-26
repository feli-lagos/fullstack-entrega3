package com.huertoverde;

import org.junit.jupiter.api.Test;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class PedidoApiTest extends ApiTestBase {

	private static String pedidoJson(long usuarioId, String items) {
		return """
				{"usuarioId":%d,"items":[%s]}
				""".formatted(usuarioId, items);
	}

	private static String item(long productoId, int cantidad) {
		return """
				{"productoId":%d,"cantidad":%d}""".formatted(productoId, cantidad);
	}

	@Test
	void crearPedido_guardaLasLineasCalculaElTotalYDescuentaStock() throws Exception {
		long usuario = crearUsuario(nuevoCorreo(), "clave123");
		long producto = crearProducto(); // precio 1990, stock 20

		postJson("/api/pedidos", pedidoJson(usuario, item(producto, 3)))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.id").isNumber())
				.andExpect(jsonPath("$.usuario.id").value((int) usuario))
				.andExpect(jsonPath("$.usuario.contrasenaHash").doesNotExist())
				.andExpect(jsonPath("$.detalles", hasSize(1)))
				.andExpect(jsonPath("$.detalles[0].producto.id").value((int) producto))
				.andExpect(jsonPath("$.detalles[0].cantidad").value(3))
				.andExpect(jsonPath("$.detalles[0].precioUnitario").value(1990.0))
				.andExpect(jsonPath("$.subtotal").value(5970.0))
				.andExpect(jsonPath("$.despacho").value(2990.0))
				.andExpect(jsonPath("$.total").value(8960.0));

		get("/api/productos/" + producto).andExpect(jsonPath("$.stock").value(17));
	}

	@Test
	void listarPedidosDeUnUsuario_devuelveSoloLosSuyos() throws Exception {
		long ana = crearUsuario(nuevoCorreo(), "clave123");
		long otro = crearUsuario(nuevoCorreo(), "clave123");
		long producto = crearProducto();
		long pedidoAna = idDe(postJson("/api/pedidos", pedidoJson(ana, item(producto, 1))).andReturn());
		long pedidoOtro = idDe(postJson("/api/pedidos", pedidoJson(otro, item(producto, 1))).andReturn());

		get("/api/pedidos?usuarioId=" + ana)
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[*].id", contains((int) pedidoAna)));
		get("/api/pedidos")
				.andExpect(jsonPath("$[*].id", hasItems((int) pedidoAna, (int) pedidoOtro)));
		get("/api/pedidos/" + pedidoAna)
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.detalles[0].cantidad").value(1));
	}

	@Test
	void crearPedido_sinStockSuficiente_devuelve409YNoDescuentaNada() throws Exception {
		long usuario = crearUsuario(nuevoCorreo(), "clave123");
		long conStock = crearProducto();
		long sinStock = crearProducto();

		postJson("/api/pedidos", pedidoJson(usuario, item(conStock, 2) + "," + item(sinStock, 50)))
				.andExpect(status().isConflict())
				.andExpect(jsonPath("$.mensaje").value(containsString("quedan 20")));

		// La transacción se deshizo completa: el primer producto conserva su stock
		get("/api/productos/" + conStock).andExpect(jsonPath("$.stock").value(20));
		get("/api/pedidos?usuarioId=" + usuario).andExpect(jsonPath("$", hasSize(0)));
	}

	@Test
	void crearPedido_conElMismoProductoDosVeces_sumaLasCantidades() throws Exception {
		long usuario = crearUsuario(nuevoCorreo(), "clave123");
		long producto = crearProducto();

		postJson("/api/pedidos", pedidoJson(usuario, item(producto, 2) + "," + item(producto, 3)))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.detalles", hasSize(1)))
				.andExpect(jsonPath("$.detalles[0].cantidad").value(5));
	}

	@Test
	void crearPedido_conCarritoVacio_devuelve400() throws Exception {
		long usuario = crearUsuario(nuevoCorreo(), "clave123");

		postJson("/api/pedidos", pedidoJson(usuario, ""))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.errores.items").value("El carrito está vacío"));
	}

	@Test
	void crearPedido_conCantidadCero_devuelve400() throws Exception {
		long usuario = crearUsuario(nuevoCorreo(), "clave123");

		postJson("/api/pedidos", pedidoJson(usuario, item(crearProducto(), 0)))
				.andExpect(status().isBadRequest());
	}

	@Test
	void crearPedido_conProductoNoPublicado_devuelve400() throws Exception {
		long usuario = crearUsuario(nuevoCorreo(), "clave123");
		long borrador = idDe(postJson("/api/productos", productoJson(nuevoCodigo(), "Oculto", primeraCategoria(), false)).andReturn());

		postJson("/api/pedidos", pedidoJson(usuario, item(borrador, 1)))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.mensaje").value(containsString("ya no está a la venta")));
	}

	@Test
	void crearPedido_conUsuarioOProductoInexistente_devuelve404() throws Exception {
		long usuario = crearUsuario(nuevoCorreo(), "clave123");

		postJson("/api/pedidos", pedidoJson(999999, item(crearProducto(), 1))).andExpect(status().isNotFound());
		postJson("/api/pedidos", pedidoJson(usuario, item(999999, 1))).andExpect(status().isNotFound());
	}

	@Test
	void eliminarProductoOUsuarioConPedidos_devuelve409() throws Exception {
		long usuario = crearUsuario(nuevoCorreo(), "clave123");
		long producto = crearProducto();
		postJson("/api/pedidos", pedidoJson(usuario, item(producto, 1))).andExpect(status().isCreated());

		delete("/api/productos/" + producto).andExpect(status().isConflict());
		delete("/api/usuarios/" + usuario).andExpect(status().isConflict());
	}
}
