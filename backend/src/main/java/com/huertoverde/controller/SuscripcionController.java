package com.huertoverde.controller;

import com.huertoverde.entity.Suscripcion;
import com.huertoverde.service.SuscripcionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/suscripciones")
@RequiredArgsConstructor
public class SuscripcionController {

	private final SuscripcionService suscripcionService;

	@GetMapping
	public List<Suscripcion> listar() {
		return suscripcionService.listar();
	}

	@PostMapping
	public ResponseEntity<Suscripcion> suscribir(@Valid @RequestBody Suscripcion suscripcion) {
		Suscripcion creada = suscripcionService.suscribir(suscripcion);
		return ResponseEntity.created(URI.create("/api/suscripciones/" + creada.getId())).body(creada);
	}
}
