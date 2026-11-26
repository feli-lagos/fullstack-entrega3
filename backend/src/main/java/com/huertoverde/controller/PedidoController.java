package com.huertoverde.controller;

import com.huertoverde.dto.PedidoRequest;
import com.huertoverde.entity.Pedido;
import com.huertoverde.service.PedidoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/pedidos")
@RequiredArgsConstructor
public class PedidoController {

	private final PedidoService pedidoService;

	// ?usuarioId=5 devuelve solo los pedidos de ese cliente
	@GetMapping
	public List<Pedido> listar(@RequestParam(required = false) Long usuarioId) {
		return pedidoService.listar(usuarioId);
	}

	@GetMapping("/{id}")
	public Pedido obtener(@PathVariable Long id) {
		return pedidoService.buscarPorId(id);
	}

	@PostMapping
	public ResponseEntity<Pedido> crear(@Valid @RequestBody PedidoRequest solicitud) {
		Pedido creado = pedidoService.crear(solicitud);
		return ResponseEntity.created(URI.create("/api/pedidos/" + creado.getId())).body(creado);
	}
}
