package com.huertoverde;

import org.junit.jupiter.api.Test;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class SuscripcionApiTest extends ApiTestBase {

	private static String suscripcionJson(String correo) {
		return """
				{"correo":"%s"}
				""".formatted(correo);
	}

	@Test
	void suscribir_guardaElCorreoEnMinusculasConFecha() throws Exception {
		String correo = nuevoCorreo();
		postJson("/api/suscripciones", suscripcionJson(correo.toUpperCase()))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.id").isNumber())
				.andExpect(jsonPath("$.correo").value(correo))
				.andExpect(jsonPath("$.fechaSuscripcion").exists());

		get("/api/suscripciones").andExpect(jsonPath("$[*].correo", hasItem(correo)));
	}

	@Test
	void suscribir_elMismoCorreoDosVeces_devuelve409() throws Exception {
		String correo = nuevoCorreo();
		postJson("/api/suscripciones", suscripcionJson(correo)).andExpect(status().isCreated());
		postJson("/api/suscripciones", suscripcionJson(correo.toUpperCase()))
				.andExpect(status().isConflict())
				.andExpect(jsonPath("$.mensaje").value(containsString("ya está suscrito")));
	}

	@Test
	void suscribir_conCorreoInvalido_devuelve400() throws Exception {
		postJson("/api/suscripciones", suscripcionJson("esto-no-es-un-correo"))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.errores.correo").exists());
		postJson("/api/suscripciones", suscripcionJson(""))
				.andExpect(status().isBadRequest());
	}
}
