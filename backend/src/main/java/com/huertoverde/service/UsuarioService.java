package com.huertoverde.service;

import com.huertoverde.entity.Comuna;
import com.huertoverde.entity.Usuario;
import com.huertoverde.exception.ConflictoException;
import com.huertoverde.exception.NoAutorizadoException;
import com.huertoverde.exception.RecursoNoEncontradoException;
import com.huertoverde.exception.SolicitudInvalidaException;
import com.huertoverde.repository.ComunaRepository;
import com.huertoverde.repository.PedidoRepository;
import com.huertoverde.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UsuarioService {

	private static final String CREDENCIALES_INVALIDAS = "Correo o contraseña incorrectos";

	private final UsuarioRepository usuarioRepository;
	private final ComunaRepository comunaRepository;
	private final PedidoRepository pedidoRepository;
	private final PasswordEncoder passwordEncoder;

	@Transactional(readOnly = true)
	public List<Usuario> listar() {
		return usuarioRepository.findAll(Sort.by("id"));
	}

	@Transactional(readOnly = true)
	public Usuario buscarPorId(Long id) {
		return usuarioRepository.findById(id)
				.orElseThrow(() -> new RecursoNoEncontradoException("Usuario", id));
	}

	@Transactional
	public Usuario crear(Usuario usuario) {
		usuario.setId(null);
		usuario.setRun(usuario.getRun().trim().toUpperCase());
		usuario.setCorreo(usuario.getCorreo().trim().toLowerCase());
		validarDigitoVerificador(usuario.getRun());
		if (usuario.getContrasena() == null || usuario.getContrasena().isEmpty()) {
			throw new SolicitudInvalidaException("La contraseña es obligatoria");
		}
		if (usuarioRepository.existsByRun(usuario.getRun())) {
			throw new ConflictoException("Ya existe un usuario registrado con el RUN " + usuario.getRun());
		}
		if (usuarioRepository.existsByCorreoIgnoreCase(usuario.getCorreo())) {
			throw new ConflictoException("Ya existe un usuario registrado con el correo " + usuario.getCorreo());
		}
		usuario.setComuna(comunaExistente(usuario.getComuna()));
		usuario.setContrasenaHash(passwordEncoder.encode(usuario.getContrasena()));
		usuario.setFechaRegistro(LocalDateTime.now());
		usuario.setUltimoAcceso(null);
		return usuarioRepository.save(usuario);
	}

	// Registro público desde la tienda: siempre crea clientes mayores de edad
	@Transactional
	public Usuario registrar(Usuario usuario) {
		LocalDate nacimiento = usuario.getFechaNacimiento();
		if (nacimiento != null && Period.between(nacimiento, LocalDate.now()).getYears() < 18) {
			throw new SolicitudInvalidaException("Debes ser mayor de 18 años para registrarte");
		}
		usuario.setTipo("cliente");
		return crear(usuario);
	}

	@Transactional
	public Usuario actualizar(Long id, Usuario datos) {
		Usuario existente = buscarPorId(id);
		if (!existente.getRun().equalsIgnoreCase(datos.getRun().trim())) {
			throw new SolicitudInvalidaException("El RUN no se puede modificar, es la clave del registro");
		}
		String correo = datos.getCorreo().trim().toLowerCase();
		if (usuarioRepository.existsByCorreoIgnoreCaseAndIdNot(correo, id)) {
			throw new ConflictoException("Ya existe otro usuario registrado con el correo " + correo);
		}
		existente.setNombre(datos.getNombre());
		existente.setApellidos(datos.getApellidos());
		existente.setCorreo(correo);
		existente.setTipo(datos.getTipo());
		existente.setComuna(comunaExistente(datos.getComuna()));
		existente.setDireccion(datos.getDireccion());
		existente.setTelefono(datos.getTelefono());
		existente.setFechaNacimiento(datos.getFechaNacimiento());
		// La contraseña es opcional al editar: si viene vacía se conserva la actual
		if (datos.getContrasena() != null && !datos.getContrasena().isEmpty()) {
			existente.setContrasenaHash(passwordEncoder.encode(datos.getContrasena()));
		}
		return usuarioRepository.save(existente);
	}

	@Transactional
	public void eliminar(Long id) {
		Usuario usuario = buscarPorId(id);
		// Los pedidos guardan quién compró: no se puede borrar a un cliente con compras
		if (pedidoRepository.existsByUsuarioId(id)) {
			throw new ConflictoException("El usuario " + usuario.getCorreo() + " tiene pedidos registrados y no se puede eliminar");
		}
		usuarioRepository.delete(usuario);
	}

	@Transactional
	public Usuario autenticar(String correo, String contrasena) {
		Usuario usuario = usuarioRepository.findByCorreoIgnoreCase(correo.trim())
				.orElseThrow(() -> new NoAutorizadoException(CREDENCIALES_INVALIDAS));
		if (!passwordEncoder.matches(contrasena, usuario.getContrasenaHash())) {
			throw new NoAutorizadoException(CREDENCIALES_INVALIDAS);
		}
		usuario.setUltimoAcceso(LocalDateTime.now());
		return usuarioRepository.save(usuario);
	}

	private Comuna comunaExistente(Comuna referencia) {
		if (referencia == null || referencia.getId() == null) {
			throw new SolicitudInvalidaException("Debe indicar la comuna del usuario: \"comuna\": {\"id\": ...}");
		}
		return comunaRepository.findById(referencia.getId())
				.orElseThrow(() -> new RecursoNoEncontradoException("Comuna", referencia.getId()));
	}

	private static void validarDigitoVerificador(String run) {
		String cuerpo = run.substring(0, run.length() - 1);
		String esperado = digitoVerificador(cuerpo);
		if (!run.endsWith(esperado)) {
			throw new SolicitudInvalidaException("El RUN no es válido, el dígito verificador debería ser " + esperado);
		}
	}

	static String digitoVerificador(String cuerpo) {
		int suma = 0;
		int multiplo = 2;
		for (int i = cuerpo.length() - 1; i >= 0; i--) {
			suma += Character.getNumericValue(cuerpo.charAt(i)) * multiplo;
			multiplo = multiplo == 7 ? 2 : multiplo + 1;
		}
		int resto = 11 - (suma % 11);
		if (resto == 11) return "0";
		if (resto == 10) return "K";
		return String.valueOf(resto);
	}
}
