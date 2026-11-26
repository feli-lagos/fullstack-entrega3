package com.huertoverde.controller;

import com.huertoverde.entity.MensajeContacto;
import com.huertoverde.service.MensajeContactoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/mensajes")
@RequiredArgsConstructor
public class MensajeContactoController {

	private final MensajeContactoService mensajeService;

	@GetMapping
	public List<MensajeContacto> listar() {
		return mensajeService.listar();
	}

	@PostMapping
	public ResponseEntity<MensajeContacto> crear(@Valid @RequestBody MensajeContacto mensaje) {
		MensajeContacto creado = mensajeService.crear(mensaje);
		return ResponseEntity.created(URI.create("/api/mensajes/" + creado.getId())).body(creado);
	}
}
