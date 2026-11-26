package com.huertoverde.service;

import com.huertoverde.entity.Comuna;
import com.huertoverde.entity.Region;
import com.huertoverde.exception.RecursoNoEncontradoException;
import com.huertoverde.repository.ComunaRepository;
import com.huertoverde.repository.RegionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RegionService {

	private final RegionRepository regionRepository;
	private final ComunaRepository comunaRepository;

	// Las regiones se devuelven de norte a sur, en el orden en que se cargaron
	@Transactional(readOnly = true)
	public List<Region> listar() {
		return regionRepository.findAll(Sort.by("id"));
	}

	@Transactional(readOnly = true)
	public List<Comuna> listarComunas(Long regionId) {
		if (!regionRepository.existsById(regionId)) {
			throw new RecursoNoEncontradoException("Región", regionId);
		}
		return comunaRepository.findByRegionIdOrderByNombreAsc(regionId);
	}
}
