package com.huertoverde.controller;

import com.huertoverde.entity.Usuario;
import com.huertoverde.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

	private final UsuarioService usuarioService;

	@GetMapping
	public List<Usuario> listar() {
		return usuarioService.listar();
	}

	@GetMapping("/{id}")
	public Usuario obtener(@PathVariable Long id) {
		return usuarioService.buscarPorId(id);
	}

	@PostMapping
	public ResponseEntity<Usuario> crear(@Valid @RequestBody Usuario usuario) {
		Usuario creado = usuarioService.crear(usuario);
		return ResponseEntity.created(URI.create("/api/usuarios/" + creado.getId())).body(creado);
	}

	@PutMapping("/{id}")
	public Usuario actualizar(@PathVariable Long id, @Valid @RequestBody Usuario usuario) {
		return usuarioService.actualizar(id, usuario);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> eliminar(@PathVariable Long id) {
		usuarioService.eliminar(id);
		return ResponseEntity.noContent().build();
	}
}
