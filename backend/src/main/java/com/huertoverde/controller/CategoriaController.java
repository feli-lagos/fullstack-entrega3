package com.huertoverde.controller;

import com.huertoverde.entity.Categoria;
import com.huertoverde.service.CategoriaService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categorias")
@RequiredArgsConstructor
public class CategoriaController {

	private final CategoriaService categoriaService;

	@GetMapping
	public List<Categoria> listar() {
		return categoriaService.listar();
	}

	@GetMapping("/{id}")
	public Categoria obtener(@PathVariable Long id) {
		return categoriaService.buscarPorId(id);
	}
}
