package com.huertoverde;

import org.junit.jupiter.api.Test;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class SwaggerApiTest extends ApiTestBase {

	@Test
	void documentoOpenApi_incluyeLosEndpointsCrud() throws Exception {
		get("/v3/api-docs")
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.info.title").value("API Huerto Verde"))
				.andExpect(jsonPath("$.paths['/api/productos/{id}'].put").exists())
				.andExpect(jsonPath("$.paths['/api/productos/{id}'].delete").exists())
				.andExpect(jsonPath("$.paths['/api/usuarios'].post").exists())
				.andExpect(jsonPath("$.paths['/api/pedidos'].post").exists());
	}

	@Test
	void swaggerUi_estaDisponible() throws Exception {
		get("/swagger-ui/index.html")
				.andExpect(status().isOk())
				.andExpect(content().string(containsString("Swagger UI")));
	}
}
