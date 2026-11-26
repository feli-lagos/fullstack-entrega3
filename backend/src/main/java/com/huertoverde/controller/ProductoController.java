package com.huertoverde.controller;

import com.huertoverde.entity.ImagenProducto;
import com.huertoverde.entity.Producto;
import com.huertoverde.service.ProductoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/productos")
@RequiredArgsConstructor
public class ProductoController {

	private final ProductoService productoService;

	// ?publicado=true devuelve solo los productos visibles en la tienda
	@GetMapping
	public List<Producto> listar(@RequestParam(defaultValue = "false") boolean publicado) {
		return productoService.listar(publicado);
	}

	@GetMapping("/{id}")
	public Producto obtener(@PathVariable Long id) {
		return productoService.buscarPorId(id);
	}

	@PostMapping
	public ResponseEntity<Producto> crear(@Valid @RequestBody Producto producto) {
		Producto creado = productoService.crear(producto);
		return ResponseEntity.created(URI.create("/api/productos/" + creado.getId())).body(creado);
	}

	@PutMapping("/{id}")
	public Producto actualizar(@PathVariable Long id, @Valid @RequestBody Producto producto) {
		return productoService.actualizar(id, producto);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> eliminar(@PathVariable Long id) {
		productoService.eliminar(id);
		return ResponseEntity.noContent().build();
	}

	// ---------- Imagen del producto (guardada en la BD) ----------

	// Devuelve el archivo tal cual: el frontend lo usa directo en <img src="http://localhost:8080/api/productos/5/imagen">
	@GetMapping("/{id}/imagen")
	public ResponseEntity<byte[]> obtenerImagen(@PathVariable Long id) {
		ImagenProducto imagen = productoService.obtenerImagen(id);
		return ResponseEntity.ok()
				.contentType(MediaType.parseMediaType(imagen.getTipo()))
				.body(imagen.getDatos());
	}

	// Sube o reemplaza la imagen. Se envía como formulario (multipart/form-data) con el archivo en el campo "archivo"
	@PostMapping(value = "/{id}/imagen", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public Producto subirImagen(@PathVariable Long id, @RequestParam("archivo") MultipartFile archivo) throws IOException {
		return productoService.guardarImagen(id, archivo.getBytes(), archivo.getContentType());
	}

	@DeleteMapping("/{id}/imagen")
	public ResponseEntity<Void> eliminarImagen(@PathVariable Long id) {
		productoService.eliminarImagen(id);
		return ResponseEntity.noContent().build();
	}
}
