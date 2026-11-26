package com.huertoverde.controller;

import com.huertoverde.entity.Comuna;
import com.huertoverde.entity.Region;
import com.huertoverde.service.RegionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/regiones")
@RequiredArgsConstructor
public class RegionController {

	private final RegionService regionService;

	@GetMapping
	public List<Region> listar() {
		return regionService.listar();
	}

	@GetMapping("/{id}/comunas")
	public List<Comuna> comunas(@PathVariable Long id) {
		return regionService.listarComunas(id);
	}
}
