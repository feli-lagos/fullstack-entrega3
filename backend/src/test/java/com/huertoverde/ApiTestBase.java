package com.huertoverde;

import com.jayway.jsonpath.JsonPath;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicInteger;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
abstract class ApiTestBase {

	private static final AtomicInteger SECUENCIA = new AtomicInteger(10_000_000);

	@Autowired
	protected MockMvc mvc;

	protected int siguiente() {
		return SECUENCIA.incrementAndGet();
	}

	// RUN válido (con dígito verificador correcto) y distinto en cada llamada
	protected String nuevoRun() {
		String cuerpo = String.valueOf(siguiente());
		return cuerpo + digitoVerificador(cuerpo);
	}

	protected String nuevoCorreo() {
		return "usuario" + siguiente() + "@gmail.com";
	}

	protected String nuevoCodigo() {
		return "TS-" + siguiente();
	}

	protected ResultActions postJson(String url, String json) throws Exception {
		return mvc.perform(post(url).contentType(APPLICATION_JSON).content(json));
	}

	protected ResultActions putJson(String url, String json) throws Exception {
		return mvc.perform(put(url).contentType(APPLICATION_JSON).content(json));
	}

	protected ResultActions get(String url) throws Exception {
		return mvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get(url));
	}

	protected ResultActions delete(String url) throws Exception {
		return mvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete(url));
	}

	// Sube un archivo como lo hace el formulario del admin (multipart/form-data, campo "archivo")
	protected ResultActions subirImagen(long productoId, String tipo, byte[] datos) throws Exception {
		MockMultipartFile archivo = new MockMultipartFile("archivo", "foto", tipo, datos);
		return mvc.perform(multipart("/api/productos/" + productoId + "/imagen").file(archivo));
	}

	protected static String productoJson(String codigo, String nombre, long categoriaId, boolean publicado) {
		return """
				{"codigo":"%s","nombre":"%s","categoria":{"id":%d},"unidad":"Kilo","precio":1990,
				 "stock":20,"stockCritico":5,"imagen":"/img/productos/tomate.jpg","descripcion":"Prueba",
				 "destacado":false,"publicado":%b}
				""".formatted(codigo, nombre, categoriaId, publicado);
	}

	protected static String usuarioJson(String run, String correo, String contrasena, String tipo, long comunaId) {
		String clave = contrasena == null ? "null" : "\"" + contrasena + "\"";
		return """
				{"run":"%s","nombre":"Ana","apellidos":"Pérez Soto","correo":"%s","contrasena":%s,
				 "tipo":"%s","comuna":{"id":%d},"direccion":"Calle Falsa 123","fechaNacimiento":"1995-05-10"}
				""".formatted(run, correo, clave, tipo, comunaId);
	}

	protected long primeraCategoria() throws Exception {
		return idEn(get("/api/categorias").andReturn(), "$[0].id");
	}

	protected long primeraComuna() throws Exception {
		long region = idEn(get("/api/regiones").andReturn(), "$[0].id");
		return idEn(get("/api/regiones/" + region + "/comunas").andReturn(), "$[0].id");
	}

	protected long crearProducto() throws Exception {
		return idDe(postJson("/api/productos", productoJson(nuevoCodigo(), "Tomate", primeraCategoria(), true))
				.andExpect(status().isCreated()).andReturn());
	}

	protected long crearUsuario(String correo, String contrasena) throws Exception {
		return idDe(postJson("/api/usuarios", usuarioJson(nuevoRun(), correo, contrasena, "cliente", primeraComuna()))
				.andExpect(status().isCreated()).andReturn());
	}

	protected static long idDe(MvcResult resultado) throws Exception {
		return idEn(resultado, "$.id");
	}

	private static long idEn(MvcResult resultado, String ruta) throws Exception {
		Number id = JsonPath.read(resultado.getResponse().getContentAsString(StandardCharsets.UTF_8), ruta);
		return id.longValue();
	}

	private static String digitoVerificador(String cuerpo) {
		int suma = 0;
		int multiplo = 2;
		for (int i = cuerpo.length() - 1; i >= 0; i--) {
			suma += Character.getNumericValue(cuerpo.charAt(i)) * multiplo;
			multiplo = multiplo == 7 ? 2 : multiplo + 1;
		}
		int resto = 11 - (suma % 11);
		if (resto == 11) return "0";
		if (resto == 10) return "K";
		return String.valueOf(resto);
	}
}
