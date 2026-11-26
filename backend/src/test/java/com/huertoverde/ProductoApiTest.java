package com.huertoverde;

import org.junit.jupiter.api.Test;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ProductoApiTest extends ApiTestBase {

	@Test
	void crearProducto_devuelve201ConCategoriaYAltPorDefecto() throws Exception {
		String codigo = nuevoCodigo();
		postJson("/api/productos", productoJson(codigo.toLowerCase(), "Tomate cherry", primeraCategoria(), true))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.id").isNumber())
				.andExpect(jsonPath("$.codigo").value(codigo))
				.andExpect(jsonPath("$.categoria.codigo").value("frutas"))
				.andExpect(jsonPath("$.categoria.nombre").value("Frutas"))
				.andExpect(jsonPath("$.alt").value("Tomate cherry"))
				.andExpect(jsonPath("$.fechaModificacion").exists());
	}

	@Test
	void crearProducto_conDatosInvalidos_devuelve400ConErroresPorCampo() throws Exception {
		postJson("/api/productos", """
				{"codigo":"AB","nombre":"","categoria":{"id":%d},"unidad":"Kilo","precio":-5,"stock":-1}
				""".formatted(primeraCategoria()))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.errores.codigo").exists())
				.andExpect(jsonPath("$.errores.nombre").exists())
				.andExpect(jsonPath("$.errores.precio").exists())
				.andExpect(jsonPath("$.errores.stock").exists());
	}

	@Test
	void crearProducto_conCodigoRepetido_devuelve409() throws Exception {
		String codigo = nuevoCodigo();
		postJson("/api/productos", productoJson(codigo, "Uno", primeraCategoria(), true)).andExpect(status().isCreated());
		postJson("/api/productos", productoJson(codigo.toLowerCase(), "Dos", primeraCategoria(), true))
				.andExpect(status().isConflict())
				.andExpect(jsonPath("$.mensaje").value(containsString(codigo)));
	}

	@Test
	void crearProducto_conCategoriaInexistente_devuelve404() throws Exception {
		postJson("/api/productos", productoJson(nuevoCodigo(), "Sin categoría", 999999, true))
				.andExpect(status().isNotFound());
	}

	@Test
	void listarPublicados_noIncluyeBorradores() throws Exception {
		long publicado = idDe(postJson("/api/productos", productoJson(nuevoCodigo(), "Visible", primeraCategoria(), true)).andReturn());
		long borrador = idDe(postJson("/api/productos", productoJson(nuevoCodigo(), "Oculto", primeraCategoria(), false)).andReturn());

		get("/api/productos?publicado=true")
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[*].id", hasItem((int) publicado)))
				.andExpect(jsonPath("$[*].id", not(hasItem((int) borrador))));

		get("/api/productos")
				.andExpect(jsonPath("$[*].id", hasItems((int) publicado, (int) borrador)));
	}

	@Test
	void actualizarProducto_reemplazaLosDatos() throws Exception {
		long id = crearProducto();
		String codigo = nuevoCodigo();
		putJson("/api/productos/" + id, productoJson(codigo, "Tomate editado", primeraCategoria(), false))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(id));

		get("/api/productos/" + id)
				.andExpect(jsonPath("$.codigo").value(codigo))
				.andExpect(jsonPath("$.nombre").value("Tomate editado"))
				.andExpect(jsonPath("$.publicado").value(false));
	}

	@Test
	void actualizarProducto_conCodigoDeOtro_devuelve409() throws Exception {
		String ajeno = nuevoCodigo();
		postJson("/api/productos", productoJson(ajeno, "Ajeno", primeraCategoria(), true)).andExpect(status().isCreated());
		long id = crearProducto();
		putJson("/api/productos/" + id, productoJson(ajeno, "Mío", primeraCategoria(), true))
				.andExpect(status().isConflict());
	}

	@Test
	void eliminarProducto_devuelve204YDejaDeExistir() throws Exception {
		long id = crearProducto();
		delete("/api/productos/" + id).andExpect(status().isNoContent());
		get("/api/productos/" + id).andExpect(status().isNotFound());
	}

	@Test
	void obtenerProducto_conIdNoNumerico_devuelve400() throws Exception {
		get("/api/productos/abc").andExpect(status().isBadRequest());
	}

	// ---------- Imagen del producto (guardada en la BD) ----------

	private static final byte[] PNG = {(byte) 0x89, 'P', 'N', 'G', 13, 10, 26, 10, 1, 2, 3, 4};

	@Test
	void subirImagen_devuelveElProductoConLaUrlDeSuImagen() throws Exception {
		long id = crearProducto();
		subirImagen(id, "image/png", PNG)
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(id))
				.andExpect(jsonPath("$.imagen").value(startsWith("/api/productos/" + id + "/imagen")));
	}

	@Test
	void obtenerImagen_devuelveLosMismosBytesConSuTipo() throws Exception {
		long id = crearProducto();
		subirImagen(id, "image/png", PNG).andExpect(status().isOk());

		get("/api/productos/" + id + "/imagen")
				.andExpect(status().isOk())
				.andExpect(content().contentType("image/png"))
				.andExpect(content().bytes(PNG));
	}

	@Test
	void obtenerImagen_deProductoSinImagen_devuelve404() throws Exception {
		long id = crearProducto();
		get("/api/productos/" + id + "/imagen").andExpect(status().isNotFound());
	}

	@Test
	void subirImagen_conArchivoQueNoEsImagen_devuelve400() throws Exception {
		long id = crearProducto();
		subirImagen(id, "text/plain", "hola".getBytes())
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.mensaje").value(containsString("JPG")));
	}

	@Test
	void subirImagen_aProductoInexistente_devuelve404() throws Exception {
		subirImagen(999999, "image/png", PNG).andExpect(status().isNotFound());
	}

	@Test
	void crearProducto_ignoraLaRutaDeImagenEnviadaEnElJson() throws Exception {
		// productoJson trae "imagen":"/img/productos/tomate.jpg": la imagen solo se cambia subiendo el archivo
		postJson("/api/productos", productoJson(nuevoCodigo(), "Sin foto", primeraCategoria(), true))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.imagen").value(nullValue()));
	}

	@Test
	void actualizarProducto_conservaLaImagenSubida() throws Exception {
		long id = crearProducto();
		subirImagen(id, "image/png", PNG).andExpect(status().isOk());

		putJson("/api/productos/" + id, productoJson(nuevoCodigo(), "Editado", primeraCategoria(), true))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.imagen").value(startsWith("/api/productos/" + id + "/imagen")));
	}

	@Test
	void eliminarImagen_dejaElProductoSinImagen() throws Exception {
		long id = crearProducto();
		subirImagen(id, "image/png", PNG).andExpect(status().isOk());

		delete("/api/productos/" + id + "/imagen").andExpect(status().isNoContent());

		get("/api/productos/" + id).andExpect(jsonPath("$.imagen").value(nullValue()));
		get("/api/productos/" + id + "/imagen").andExpect(status().isNotFound());
	}

	@Test
	void eliminarProducto_conImagen_devuelve204() throws Exception {
		long id = crearProducto();
		subirImagen(id, "image/png", PNG).andExpect(status().isOk());

		delete("/api/productos/" + id).andExpect(status().isNoContent());
		get("/api/productos/" + id).andExpect(status().isNotFound());
	}
}
