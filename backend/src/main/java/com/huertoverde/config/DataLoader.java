package com.huertoverde.config;

import com.huertoverde.entity.Categoria;
import com.huertoverde.entity.Comuna;
import com.huertoverde.entity.Producto;
import com.huertoverde.entity.Usuario;
import com.huertoverde.repository.CategoriaRepository;
import com.huertoverde.repository.ComunaRepository;
import com.huertoverde.repository.ProductoRepository;
import com.huertoverde.repository.UsuarioRepository;
import com.huertoverde.service.ProductoService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

// Productos y usuarios de ejemplo. Las fotos de los productos se guardan dentro de la BD (ver subirFotosDeEjemplo).
@Configuration
@Profile("!test")
public class DataLoader {

	// Contraseñas de demostración (se guardan con hash BCrypt)
	static final String CLAVE_ADMIN = "admin123";
	static final String CLAVE_DEMO = "clave123";

	@Bean
	@Order(2)
	CommandLineRunner cargarDatosDeEjemplo(CategoriaRepository categorias, ProductoRepository productos,
			UsuarioRepository usuarios, ComunaRepository comunas, PasswordEncoder encoder, ProductoService productoService) {
		return args -> {
			if (productos.count() == 0) {
				List<Categoria> lista = categorias.findAll();
				Categoria frutas = categoria(lista, "frutas");
				Categoria verduras = categoria(lista, "verduras");
				Categoria organicos = categoria(lista, "organicos");
				productos.save(producto("FR-001", "Manzana Fuji", frutas, "Kilo", 1290, 48, 10,
						"/img/productos/manzana-fuji.jpg", "Manzanas Fuji apiladas en un cajón de madera",
						"Manzanas Fuji frescas, crujientes y dulces, seleccionadas para consumo diario.", true));
				productos.save(producto("FR-002", "Plátano de Ecuador", frutas, "Kilo", 1490, 60, 15,
						"/img/productos/platano.jpg", "Plátanos de Ecuador",
						"Plátanos de Ecuador con maduración gradual, ideales para consumir durante la semana.", true));
				productos.save(producto("FR-003", "Naranja Valencia", frutas, "Malla 3 kilos", 3590, 25, 8,
						"/img/productos/naranja.jpg", "Naranjas Valencia",
						"Naranjas jugosas de temporada, perfectas para jugos y preparaciones caseras.", true));
				productos.save(producto("FR-004", "Frutilla", frutas, "Bandeja 500 g", 2490, 6, 6,
						"/img/productos/frutilla.jpg", "Frutillas frescas en una bandeja",
						"Frutillas de temporada, seleccionadas y listas para servir.", true));
				productos.save(producto("FR-005", "Muestra de temporada", frutas, "Muestra", 0, 100, 0,
						"/img/productos/muestra-gratis.jpg", "Muestra gratuita de frutas de temporada",
						"Muestra gratuita de productos de temporada. Precio GRATIS.", false));
				productos.save(producto("VE-001", "Tomate Limachino", verduras, "Kilo", 1990, 32, 10,
						"/img/productos/tomate.jpg", "Tomates Limachino",
						"Tomates Limachino de sabor intenso, cultivados y seleccionados en la zona central.", true));
				productos.save(producto("VE-002", "Palta Hass", verduras, "Kilo", 4990, 4, 10,
						"/img/productos/palta.jpg", "Paltas Hass cortadas por la mitad",
						"Palta Hass cremosa, ideal para untar. Se despacha con maduración de 2 a 3 días.", true));
				productos.save(producto("VE-003", "Zanahoria", verduras, "Kilo", 990, 75, 20,
						"/img/productos/zanahoria.jpg", "Zanahorias frescas",
						"Zanahorias frescas y crocantes, buenas para ensaladas, sopas y jugos.", true));
				productos.save(producto("VE-004", "Lechuga escarola", verduras, "Unidad", 1190, 40, 12,
						"/img/productos/lechuga.jpg", "Lechuga escarola fresca",
						"Lechuga escarola fresca, lavada y lista para preparar ensaladas.", false));
				productos.save(producto("OR-001", "Mix orgánico de temporada", organicos, "Caja 5 kilos", 8990, 18, 5,
						"/img/productos/mix-organico.jpg", "Caja con mix orgánico de temporada",
						"Selección de frutas y verduras orgánicas de la temporada.", true));
				productos.save(producto("OR-002", "Quinoa orgánica", organicos, "Bolsa 500 g", 5490, 22, 6,
						"/img/productos/quinoa.jpg", "Quinoa orgánica",
						"Quinoa orgánica seleccionada, rica en proteínas y fácil de preparar.", false));
				productos.save(producto("OR-003", "Miel de ulmo", organicos, "Frasco 500 g", 6990, 2, 5,
						"/img/productos/miel.jpg", "Frasco de miel de ulmo",
						"Miel de ulmo de productores locales, envasada en origen.", false));
			}
			subirFotosDeEjemplo(productos, productoService);
			if (usuarios.count() > 0) {
				return;
			}
			String clave = encoder.encode(CLAVE_DEMO);
			// Cuenta usada por la pantalla de login para entrar al panel de administración
			usuarios.save(usuario("123456785", "Administrador", "Huerto Verde", "admin@duoc.cl", "administrador",
					comuna(comunas, "Santiago", "Metropolitana de Santiago"), "Av. Siempre Fresca 1234", null,
					LocalDateTime.of(2024, 3, 1, 0, 0), null, encoder.encode(CLAVE_ADMIN)));
			usuarios.save(usuario("19011029K", "Camila", "Rojas Fuentes", "camila.rojas@duoc.cl", "administrador",
					comuna(comunas, "Santiago", "Metropolitana de Santiago"), "Moneda 970, oficina 415", LocalDate.of(1996, 4, 18),
					LocalDateTime.of(2024, 3, 11, 0, 0), LocalDateTime.of(2026, 9, 4, 8, 12), clave));
			usuarios.save(usuario("152348762", "Diego", "Pérez Soto", "diego.perez@profesor.duoc.cl", "vendedor",
					comuna(comunas, "Providencia", "Metropolitana de Santiago"), "Av. Providencia 1650, depto 82", LocalDate.of(1988, 11, 2),
					LocalDateTime.of(2024, 5, 20, 0, 0), LocalDateTime.of(2026, 9, 3, 17, 45), clave));
			usuarios.save(usuario("20887435", "Valentina", "Muñoz Araya", "vale.munoz@gmail.com", "cliente",
					comuna(comunas, "Viña del Mar", "Valparaíso"), "Calle Valparaíso 455", LocalDate.of(2001, 7, 9),
					LocalDateTime.of(2025, 1, 14, 0, 0), LocalDateTime.of(2026, 8, 30, 21, 10), clave));
			usuarios.save(usuario("17654322", "Matías", "González Lillo", "matias.gonzalez@duoc.cl", "cliente",
					comuna(comunas, "Maipú", "Metropolitana de Santiago"), "Av. Pajaritos 2280", LocalDate.of(1991, 2, 25),
					LocalDateTime.of(2024, 9, 2, 0, 0), LocalDateTime.of(2026, 9, 1, 12, 3), clave));
			usuarios.save(usuario("21334553", "Antonia", "Silva Bravo", "antonia.silva@gmail.com", "cliente",
					comuna(comunas, "Temuco", "La Araucanía"), "Av. Alemania 0671", LocalDate.of(2003, 5, 30),
					LocalDateTime.of(2025, 3, 8, 0, 0), LocalDateTime.of(2026, 8, 27, 9, 40), clave));
			usuarios.save(usuario("1699877K", "Ignacio", "Cortés Vera", "ignacio.cortes@duoc.cl", "vendedor",
					comuna(comunas, "Chillán", "Ñuble"), "Arauco 540", LocalDate.of(1987, 10, 14),
					LocalDateTime.of(2024, 6, 17, 0, 0), LocalDateTime.of(2026, 9, 4, 7, 55), clave));
			usuarios.save(usuario("18445123", "Fernanda", "Torres Riquelme", "fernanda.torres@gmail.com", "cliente",
					comuna(comunas, "Talca", "Maule"), "1 Sur 1120", LocalDate.of(1993, 12, 1),
					LocalDateTime.of(2025, 2, 22, 0, 0), LocalDateTime.of(2026, 8, 25, 19, 20), clave));
			usuarios.save(usuario("14229809", "Rodrigo", "Herrera Pinto", "rodrigo.herrera@profesor.duoc.cl", "administrador",
					comuna(comunas, "Ñuñoa", "Metropolitana de Santiago"), "Irarrázaval 3450, depto 1203", LocalDate.of(1980, 8, 19),
					LocalDateTime.of(2024, 3, 11, 0, 0), LocalDateTime.of(2026, 9, 2, 10, 35), clave));
			usuarios.save(usuario("22110985", "Josefa", "Navarro Díaz", "josefa.navarro@gmail.com", "cliente",
					comuna(comunas, "Valparaíso", "Valparaíso"), "Lautaro Rosas 380, Cerro Alegre", LocalDate.of(2004, 3, 16),
					LocalDateTime.of(2025, 6, 30, 0, 0), LocalDateTime.of(2026, 8, 29, 15, 5), clave));
			usuarios.save(usuario("19776557", "Tomás", "Espinoza Reyes", "tomas.espinoza@duoc.cl", "cliente",
					comuna(comunas, "Puente Alto", "Metropolitana de Santiago"), "Av. Concha y Toro 1890", LocalDate.of(1997, 9, 23),
					LocalDateTime.of(2024, 11, 5, 0, 0), LocalDateTime.of(2026, 9, 3, 22, 18), clave));
		};
	}

	// Los productos de ejemplo indican su foto con una ruta /img/productos/x.jpg (igual que las BD creadas
	// antes de que las imágenes se guardaran en la BD). Aquí esa foto se lee desde
	// src/main/resources/imagenes-ejemplo, se guarda en la tabla imagenes_producto y la ruta se reemplaza
	// por la URL de la API. Solo trabaja la primera vez: después ninguna imagen empieza con /img/.
	private static void subirFotosDeEjemplo(ProductoRepository productos, ProductoService servicio) throws IOException {
		for (Producto producto : productos.findAll()) {
			String ruta = producto.getImagen();
			if (ruta != null && ruta.startsWith("/img/")) {
				String nombre = ruta.substring(ruta.lastIndexOf('/') + 1);
				ClassPathResource foto = new ClassPathResource("imagenes-ejemplo/" + nombre);
				if (foto.exists()) {
					String tipo = MediaTypeFactory.getMediaType(nombre).map(MediaType::toString).orElse("image/jpeg");
					servicio.guardarImagen(producto.getId(), foto.getContentAsByteArray(), tipo);
				} else {
					// La ruta apuntaba a un archivo que no existe: el producto queda sin foto
					servicio.eliminarImagen(producto.getId());
				}
			}
		}
	}

	private static Categoria categoria(List<Categoria> categorias, String codigo) {
		return categorias.stream().filter(c -> c.getCodigo().equals(codigo)).findFirst().orElseThrow();
	}

	private static Comuna comuna(ComunaRepository comunas, String nombre, String region) {
		return comunas.findByNombreAndRegionNombre(nombre, region)
				.orElseThrow(() -> new IllegalStateException("No existe la comuna " + nombre + " en " + region));
	}

	private static Producto producto(String codigo, String nombre, Categoria categoria, String unidad, int precio,
			int stock, int stockCritico, String imagen, String alt, String descripcion, boolean destacado) {
		Producto p = new Producto();
		p.setCodigo(codigo);
		p.setNombre(nombre);
		p.setCategoria(categoria);
		p.setUnidad(unidad);
		p.setPrecio(BigDecimal.valueOf(precio));
		p.setStock(stock);
		p.setStockCritico(stockCritico);
		p.setImagen(imagen);
		p.setAlt(alt);
		p.setDescripcion(descripcion);
		p.setDestacado(destacado);
		p.setPublicado(true);
		return p;
	}

	private static Usuario usuario(String run, String nombre, String apellidos, String correo, String tipo,
			Comuna comuna, String direccion, LocalDate fechaNacimiento, LocalDateTime fechaRegistro,
			LocalDateTime ultimoAcceso, String contrasenaHash) {
		Usuario u = new Usuario();
		u.setRun(run);
		u.setNombre(nombre);
		u.setApellidos(apellidos);
		u.setCorreo(correo);
		u.setTipo(tipo);
		u.setComuna(comuna);
		u.setDireccion(direccion);
		u.setFechaNacimiento(fechaNacimiento);
		u.setFechaRegistro(fechaRegistro);
		u.setUltimoAcceso(ultimoAcceso);
		u.setContrasenaHash(contrasenaHash);
		return u;
	}
}
