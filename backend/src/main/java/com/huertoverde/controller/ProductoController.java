package com.huertoverde.controller;

import com.huertoverde.entity.Producto;
import com.huertoverde.service.ProductoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
}
