package com.huertoverde.service;

import com.huertoverde.entity.Suscripcion;
import com.huertoverde.exception.ConflictoException;
import com.huertoverde.repository.SuscripcionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SuscripcionService {

	private final SuscripcionRepository suscripcionRepository;

	@Transactional(readOnly = true)
	public List<Suscripcion> listar() {
		return suscripcionRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));
	}

	@Transactional
	public Suscripcion suscribir(Suscripcion suscripcion) {
		suscripcion.setId(null);
		suscripcion.setCorreo(suscripcion.getCorreo().trim().toLowerCase());
		// Un mismo correo se guarda una sola vez (sin importar mayúsculas)
		if (suscripcionRepository.existsByCorreoIgnoreCase(suscripcion.getCorreo())) {
			throw new ConflictoException("El correo " + suscripcion.getCorreo() + " ya está suscrito");
		}
		suscripcion.setFechaSuscripcion(LocalDateTime.now());
		return suscripcionRepository.save(suscripcion);
	}
}
