package com.huertoverde;

import org.junit.jupiter.api.Test;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class CatalogoApiTest extends ApiTestBase {

	@Test
	void listarCategorias_devuelveLasCuatroCategorias() throws Exception {
		get("/api/categorias")
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.length()").value(4))
				.andExpect(jsonPath("$[*].codigo", contains("frutas", "verduras", "organicos", "despensa")))
				.andExpect(jsonPath("$[2].nombre").value("Orgánicos"));
	}

	@Test
	void listarRegiones_devuelveLas16DeNorteASur() throws Exception {
		get("/api/regiones")
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.length()").value(16))
				.andExpect(jsonPath("$[0].nombre").value("Arica y Parinacota"))
				.andExpect(jsonPath("$[15].nombre").value("Magallanes y de la Antártica Chilena"))
				.andExpect(jsonPath("$[0].comunas").doesNotExist());
	}

	@Test
	void comunasDeUnaRegion_devuelveSoloLasSuyas() throws Exception {
		get("/api/regiones/1/comunas")
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[*].nombre", contains("Arica", "Camarones", "General Lagos", "Putre")))
				.andExpect(jsonPath("$[0].region.nombre").value("Arica y Parinacota"));
	}

	@Test
	void comunasDeRegionInexistente_devuelve404() throws Exception {
		get("/api/regiones/999999/comunas")
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.mensaje").value(containsString("999999")));
	}
}
