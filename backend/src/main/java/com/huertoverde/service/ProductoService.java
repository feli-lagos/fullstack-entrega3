package com.huertoverde.service;

import com.huertoverde.entity.Categoria;
import com.huertoverde.entity.Producto;
import com.huertoverde.exception.ConflictoException;
import com.huertoverde.exception.RecursoNoEncontradoException;
import com.huertoverde.exception.SolicitudInvalidaException;
import com.huertoverde.repository.CategoriaRepository;
import com.huertoverde.repository.ProductoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductoService {

	private final ProductoRepository productoRepository;
	private final CategoriaRepository categoriaRepository;

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
		existente.setImagen(datos.getImagen());
		existente.setAlt(datos.getAlt());
		existente.setDescripcion(datos.getDescripcion());
		existente.setDestacado(datos.isDestacado());
		existente.setPublicado(datos.isPublicado());
		completarAlt(existente);
		return productoRepository.save(existente);
	}

	@Transactional
	public void eliminar(Long id) {
		productoRepository.delete(buscarPorId(id));
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
