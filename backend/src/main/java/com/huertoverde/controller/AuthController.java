package com.huertoverde.controller;

import com.huertoverde.dto.LoginRequest;
import com.huertoverde.entity.Usuario;
import com.huertoverde.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

	private final UsuarioService usuarioService;

	@PostMapping("/login")
	public Usuario login(@Valid @RequestBody LoginRequest solicitud) {
		return usuarioService.autenticar(solicitud.correo(), solicitud.contrasena());
	}

	@PostMapping("/registro")
	public ResponseEntity<Usuario> registro(@Valid @RequestBody Usuario usuario) {
		Usuario creado = usuarioService.registrar(usuario);
		return ResponseEntity.created(URI.create("/api/usuarios/" + creado.getId())).body(creado);
	}
}
