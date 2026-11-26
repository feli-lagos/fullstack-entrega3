package com.huertoverde;

import org.junit.jupiter.api.Test;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class UsuarioApiTest extends ApiTestBase {

	@Test
	void crearUsuario_devuelve201SinExponerLaContrasena() throws Exception {
		String run = nuevoRun();
		String correo = nuevoCorreo();
		postJson("/api/usuarios", usuarioJson(run, correo.toUpperCase(), "clave1", "vendedor", primeraComuna()))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.run").value(run))
				.andExpect(jsonPath("$.correo").value(correo))
				.andExpect(jsonPath("$.tipo").value("vendedor"))
				.andExpect(jsonPath("$.comuna.region.nombre").value("Arica y Parinacota"))
				.andExpect(jsonPath("$.fechaRegistro").exists())
				.andExpect(jsonPath("$.contrasena").doesNotExist())
				.andExpect(jsonPath("$.contrasenaHash").doesNotExist());
	}

	@Test
	void crearUsuario_conDigitoVerificadorIncorrecto_devuelve400() throws Exception {
		postJson("/api/usuarios", usuarioJson("123456780", nuevoCorreo(), "clave1", "cliente", primeraComuna()))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.mensaje").value(containsString("debería ser 5")));
	}

	@Test
	void crearUsuario_conDatosInvalidos_devuelve400ConErroresPorCampo() throws Exception {
		postJson("/api/usuarios", usuarioJson("12.345.678-5", "ana@hotmail.com", "123", "jefe", primeraComuna()))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.errores.run").exists())
				.andExpect(jsonPath("$.errores.correo").exists())
				.andExpect(jsonPath("$.errores.contrasena").exists())
				.andExpect(jsonPath("$.errores.tipo").exists());
	}

	@Test
	void crearUsuario_sinContrasena_devuelve400() throws Exception {
		postJson("/api/usuarios", usuarioJson(nuevoRun(), nuevoCorreo(), null, "cliente", primeraComuna()))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.mensaje").value(containsString("contraseña")));
	}

	@Test
	void crearUsuario_conRunOCorreoRepetido_devuelve409() throws Exception {
		String run = nuevoRun();
		String correo = nuevoCorreo();
		postJson("/api/usuarios", usuarioJson(run, correo, "clave1", "cliente", primeraComuna())).andExpect(status().isCreated());

		postJson("/api/usuarios", usuarioJson(run, nuevoCorreo(), "clave1", "cliente", primeraComuna()))
				.andExpect(status().isConflict())
				.andExpect(jsonPath("$.mensaje").value(containsString(run)));
		postJson("/api/usuarios", usuarioJson(nuevoRun(), correo, "clave1", "cliente", primeraComuna()))
				.andExpect(status().isConflict())
				.andExpect(jsonPath("$.mensaje").value(containsString(correo)));
	}

	@Test
	void actualizarUsuario_sinContrasena_conservaLaActual() throws Exception {
		String run = nuevoRun();
		String correo = nuevoCorreo();
		long id = idDe(postJson("/api/usuarios", usuarioJson(run, correo, "clave1", "cliente", primeraComuna())).andReturn());

		putJson("/api/usuarios/" + id, usuarioJson(run, correo, null, "administrador", primeraComuna()))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.tipo").value("administrador"));

		postJson("/api/auth/login", """
				{"correo":"%s","contrasena":"clave1"}
				""".formatted(correo))
				.andExpect(status().isOk());
	}

	@Test
	void actualizarUsuario_cambiandoElRun_devuelve400() throws Exception {
		String correo = nuevoCorreo();
		long id = crearUsuario(correo, "clave1");
		putJson("/api/usuarios/" + id, usuarioJson(nuevoRun(), correo, null, "cliente", primeraComuna()))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.mensaje").value(containsString("RUN")));
	}

	@Test
	void eliminarUsuario_devuelve204YDejaDeExistir() throws Exception {
		long id = crearUsuario(nuevoCorreo(), "clave1");
		delete("/api/usuarios/" + id).andExpect(status().isNoContent());
		get("/api/usuarios/" + id).andExpect(status().isNotFound());
	}
}
