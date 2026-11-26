package com.huertoverde.service;

import com.huertoverde.entity.Categoria;
import com.huertoverde.entity.ImagenProducto;
import com.huertoverde.entity.Producto;
import com.huertoverde.exception.ConflictoException;
import com.huertoverde.exception.RecursoNoEncontradoException;
import com.huertoverde.exception.SolicitudInvalidaException;
import com.huertoverde.repository.CategoriaRepository;
import com.huertoverde.repository.DetallePedidoRepository;
import com.huertoverde.repository.ImagenProductoRepository;
import com.huertoverde.repository.ProductoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ProductoService {

	private static final Set<String> TIPOS_DE_IMAGEN = Set.of("image/jpeg", "image/png", "image/webp", "image/gif");

	private final ProductoRepository productoRepository;
	private final CategoriaRepository categoriaRepository;
	private final DetallePedidoRepository detallePedidoRepository;
	private final ImagenProductoRepository imagenProductoRepository;

	@Transactional(readOnly = true)
	public List<Producto> listar(boolean soloPublicados) {
		return soloPublicados
				? productoRepository.findByPublicadoTrueOrderByIdAsc()
				: productoRepository.findAll(Sort.by("id"));
	}

	@Transactional(readOnly = true)
	public Producto buscarPorId(Long id) {
		return productoRepository.findById(id)
				.orElseThrow(() -> new RecursoNoEncontradoException("Producto", id));
	}

	@Transactional
	public Producto crear(Producto producto) {
		producto.setId(null);
		producto.setCodigo(producto.getCodigo().trim().toUpperCase());
		if (productoRepository.existsByCodigoIgnoreCase(producto.getCodigo())) {
			throw new ConflictoException("Ya existe un producto con el código " + producto.getCodigo());
		}
		producto.setCategoria(categoriaExistente(producto.getCategoria()));
		completarAlt(producto);
		return productoRepository.save(producto);
	}

	@Transactional
	public Producto actualizar(Long id, Producto datos) {
		Producto existente = buscarPorId(id);
		String codigo = datos.getCodigo().trim().toUpperCase();
		if (productoRepository.existsByCodigoIgnoreCaseAndIdNot(codigo, id)) {
			throw new ConflictoException("Ya existe otro producto con el código " + codigo);
		}
		existente.setCodigo(codigo);
		existente.setNombre(datos.getNombre());
		existente.setCategoria(categoriaExistente(datos.getCategoria()));
		existente.setUnidad(datos.getUnidad());
		existente.setPrecio(datos.getPrecio());
		existente.setStock(datos.getStock());
		existente.setStockCritico(datos.getStockCritico());
		// La imagen no se toca aquí: se cambia con guardarImagen / eliminarImagen
		existente.setAlt(datos.getAlt());
		existente.setDescripcion(datos.getDescripcion());
		existente.setDestacado(datos.isDestacado());
		existente.setPublicado(datos.isPublicado());
		completarAlt(existente);
		return productoRepository.save(existente);
	}

	@Transactional
	public void eliminar(Long id) {
		Producto producto = buscarPorId(id);
		// Si ya se vendió, borrarlo dejaría pedidos sin producto: se oculta despublicándolo
		if (detallePedidoRepository.existsByProductoId(id)) {
			throw new ConflictoException("El producto " + producto.getCodigo()
					+ " aparece en pedidos y no se puede eliminar. Desmárcalo como publicado para ocultarlo de la tienda.");
		}
		// Primero su imagen: la tabla imagenes_producto tiene una clave foránea hacia productos
		imagenProductoRepository.findById(id).ifPresent(imagenProductoRepository::delete);
		productoRepository.delete(producto);
	}

	@Transactional(readOnly = true)
	public ImagenProducto obtenerImagen(Long id) {
		return imagenProductoRepository.findById(id)
				.orElseThrow(() -> new RecursoNoEncontradoException("Imagen del producto", id));
	}

	// Guarda (o reemplaza) la foto del producto dentro de la BD
	@Transactional
	public Producto guardarImagen(Long id, byte[] datos, String tipo) {
		Producto producto = buscarPorId(id);
		if (datos == null || datos.length == 0) {
			throw new SolicitudInvalidaException("Debe adjuntar una imagen en el campo \"archivo\"");
		}
		if (tipo == null || !TIPOS_DE_IMAGEN.contains(tipo)) {
			throw new SolicitudInvalidaException("La imagen debe ser JPG, PNG, WEBP o GIF");
		}

		ImagenProducto imagen = imagenProductoRepository.findById(id).orElseGet(ImagenProducto::new);
		imagen.setProducto(producto);
		imagen.setDatos(datos);
		imagen.setTipo(tipo);
		imagenProductoRepository.save(imagen);

		// ?v= cambia en cada subida: así el navegador no sigue mostrando la foto anterior que tenía en caché
		producto.setImagen("/api/productos/" + id + "/imagen?v=" + System.currentTimeMillis());
		return productoRepository.save(producto);
	}

	@Transactional
	public void eliminarImagen(Long id) {
		Producto producto = buscarPorId(id);
		imagenProductoRepository.findById(id).ifPresent(imagenProductoRepository::delete);
		producto.setImagen(null);
		productoRepository.save(producto);
	}

	private Categoria categoriaExistente(Categoria referencia) {
		if (referencia == null || referencia.getId() == null) {
			throw new SolicitudInvalidaException("Debe indicar la categoría del producto: \"categoria\": {\"id\": ...}");
		}
		return categoriaRepository.findById(referencia.getId())
				.orElseThrow(() -> new RecursoNoEncontradoException("Categoría", referencia.getId()));
	}

	private static void completarAlt(Producto producto) {
		if (producto.getAlt() == null || producto.getAlt().isBlank()) {
			producto.setAlt(producto.getNombre());
		}
	}
}
