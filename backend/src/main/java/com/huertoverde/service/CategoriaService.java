package com.huertoverde.service;

import com.huertoverde.entity.Categoria;
import com.huertoverde.exception.RecursoNoEncontradoException;
import com.huertoverde.repository.CategoriaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoriaService {

	private final CategoriaRepository categoriaRepository;

	@Transactional(readOnly = true)
	public List<Categoria> listar() {
		return categoriaRepository.findAll(Sort.by("id"));
	}

	@Transactional(readOnly = true)
	public Categoria buscarPorId(Long id) {
		return categoriaRepository.findById(id)
				.orElseThrow(() -> new RecursoNoEncontradoException("Categoría", id));
	}
}
