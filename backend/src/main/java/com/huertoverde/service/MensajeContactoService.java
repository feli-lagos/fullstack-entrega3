package com.huertoverde.service;

import com.huertoverde.entity.MensajeContacto;
import com.huertoverde.repository.MensajeContactoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MensajeContactoService {

	private final MensajeContactoRepository mensajeRepository;

	@Transactional(readOnly = true)
	public List<MensajeContacto> listar() {
		return mensajeRepository.findAll(Sort.by(Sort.Direction.DESC, "fechaEnvio"));
	}

	@Transactional
	public MensajeContacto crear(MensajeContacto mensaje) {
		mensaje.setId(null);
		mensaje.setFechaEnvio(LocalDateTime.now());
		return mensajeRepository.save(mensaje);
	}
}
