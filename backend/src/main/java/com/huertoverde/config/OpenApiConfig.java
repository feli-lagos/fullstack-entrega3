package com.huertoverde.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// Título y descripción que aparecen arriba en Swagger UI (http://localhost:8080/swagger-ui.html).
// Los endpoints no hay que declararlos aquí: springdoc los lee solo desde los @RestController.
@Configuration
public class OpenApiConfig {

	@Bean
	OpenAPI huertoVerdeOpenApi() {
		return new OpenAPI().info(new Info()
				.title("API Huerto Verde")
				.version("1.0")
				.description("API REST de la tienda online Huerto Verde (DSY1104): productos, usuarios, "
						+ "pedidos del carrito, categorías, regiones/comunas, contacto y autenticación. "
						+ "Base de datos H2."));
	}
}
