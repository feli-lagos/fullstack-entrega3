package com.huertoverde;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AuthApiTest extends ApiTestBase {

	private static String login(String correo, String contrasena) {
		return """
				{"correo":"%s","contrasena":"%s"}
				""".formatted(correo, contrasena);
	}

	@Test
	void login_conCredencialesCorrectas_devuelveElUsuarioYRegistraElAcceso() throws Exception {
		String correo = nuevoCorreo();
		crearUsuario(correo, "clave1");
		postJson("/api/auth/login", login(correo.toUpperCase(), "clave1"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.correo").value(correo))
				.andExpect(jsonPath("$.ultimoAcceso").exists())
				.andExpect(jsonPath("$.contrasenaHash").doesNotExist());
	}

	@Test
	void login_conContrasenaIncorrecta_devuelve401() throws Exception {
		String correo = nuevoCorreo();
		crearUsuario(correo, "clave1");
		postJson("/api/auth/login", login(correo, "otra"))
				.andExpect(status().isUnauthorized())
				.andExpect(jsonPath("$.mensaje").value("Correo o contraseña incorrectos"));
	}

	@Test
	void login_conCorreoInexistente_devuelve401() throws Exception {
		postJson("/api/auth/login", login("nadie@gmail.com", "clave1"))
				.andExpect(status().isUnauthorized());
	}

	@Test
	void registro_siempreCreaClientes() throws Exception {
		postJson("/api/auth/registro", usuarioJson(nuevoRun(), nuevoCorreo(), "clave1", "administrador", primeraComuna()))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.tipo").value("cliente"));
	}

	@Test
	void registro_deMenorDeEdad_devuelve400() throws Exception {
		String json = usuarioJson(nuevoRun(), nuevoCorreo(), "clave1", "cliente", primeraComuna())
				.replace("1995-05-10", LocalDate.now().minusYears(17).toString());
		postJson("/api/auth/registro", json)
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.mensaje").value(org.hamcrest.Matchers.containsString("18")));
	}
}
