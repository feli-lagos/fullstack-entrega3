package com.huertoverde;

import org.junit.jupiter.api.Test;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class MensajeContactoApiTest extends ApiTestBase {

	@Test
	void enviarMensaje_devuelve201ConFecha() throws Exception {
		postJson("/api/mensajes", """
				{"nombre":"Ana Pérez","correo":null,"motivo":"reclamo","comentario":"Llegó mal el pedido"}
				""")
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.id").isNumber())
				.andExpect(jsonPath("$.motivo").value("reclamo"))
				.andExpect(jsonPath("$.fechaEnvio").exists());
	}

	@Test
	void enviarMensaje_sinComentarioYConMotivoInvalido_devuelve400() throws Exception {
		postJson("/api/mensajes", """
				{"nombre":"Ana","correo":"ana@hotmail.com","motivo":"otro","comentario":""}
				""")
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.errores.correo").exists())
				.andExpect(jsonPath("$.errores.motivo").exists())
				.andExpect(jsonPath("$.errores.comentario").exists());
	}
}
