package com.huertoverde.service;

import com.huertoverde.dto.PedidoRequest;
import com.huertoverde.entity.DetallePedido;
import com.huertoverde.entity.Pedido;
import com.huertoverde.entity.Producto;
import com.huertoverde.entity.Usuario;
import com.huertoverde.exception.ConflictoException;
import com.huertoverde.exception.RecursoNoEncontradoException;
import com.huertoverde.exception.SolicitudInvalidaException;
import com.huertoverde.repository.PedidoRepository;
import com.huertoverde.repository.ProductoRepository;
import com.huertoverde.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PedidoService {

	// Costo fijo de despacho (el mismo que muestra el carrito en la tienda)
	static final BigDecimal COSTO_DESPACHO = BigDecimal.valueOf(2990);

	private final PedidoRepository pedidoRepository;
	private final ProductoRepository productoRepository;
	private final UsuarioRepository usuarioRepository;

	@Transactional(readOnly = true)
	public List<Pedido> listar(Long usuarioId) {
		return usuarioId == null
				? pedidoRepository.findAll(Sort.by(Sort.Direction.DESC, "id"))
				: pedidoRepository.findByUsuarioIdOrderByIdDesc(usuarioId);
	}

	@Transactional(readOnly = true)
	public Pedido buscarPorId(Long id) {
		return pedidoRepository.findById(id)
				.orElseThrow(() -> new RecursoNoEncontradoException("Pedido", id));
	}

	// Todo pasa en una sola transacción: si un producto falla (sin stock, no publicado...),
	// no se guarda el pedido y no se descuenta el stock de ningún producto.
	@Transactional
	public Pedido crear(PedidoRequest solicitud) {
		Usuario usuario = usuarioRepository.findById(solicitud.usuarioId())
				.orElseThrow(() -> new RecursoNoEncontradoException("Usuario", solicitud.usuarioId()));

		// Si el mismo producto viene dos veces, se suman las cantidades
		Map<Long, Integer> cantidades = new LinkedHashMap<>();
		for (PedidoRequest.Item item : solicitud.items()) {
			cantidades.merge(item.productoId(), item.cantidad(), Integer::sum);
		}

		Pedido pedido = new Pedido();
		pedido.setUsuario(usuario);
		pedido.setFecha(LocalDateTime.now());
		BigDecimal subtotal = BigDecimal.ZERO;

		for (Map.Entry<Long, Integer> linea : cantidades.entrySet()) {
			Producto producto = productoRepository.findById(linea.getKey())
					.orElseThrow(() -> new RecursoNoEncontradoException("Producto", linea.getKey()));
			int cantidad = linea.getValue();

			if (!producto.isPublicado()) {
				throw new SolicitudInvalidaException("El producto " + producto.getNombre() + " ya no está a la venta");
			}
			if (cantidad > producto.getStock()) {
				throw new ConflictoException("No hay stock suficiente de " + producto.getNombre()
						+ ": pediste " + cantidad + " y quedan " + producto.getStock());
			}

			// Se descuenta el stock. No hace falta llamar a save(): el producto viene de la BD
			// dentro de esta transacción, y JPA guarda el cambio (UPDATE) al terminar.
			producto.setStock(producto.getStock() - cantidad);

			DetallePedido detalle = new DetallePedido();
			detalle.setProducto(producto);
			detalle.setCantidad(cantidad);
			detalle.setPrecioUnitario(producto.getPrecio());
			detalle.setSubtotal(producto.getPrecio().multiply(BigDecimal.valueOf(cantidad)));
			pedido.agregarDetalle(detalle);
			subtotal = subtotal.add(detalle.getSubtotal());
		}

		BigDecimal despacho = subtotal.signum() > 0 ? COSTO_DESPACHO : BigDecimal.ZERO;
		pedido.setSubtotal(subtotal);
		pedido.setDespacho(despacho);
		pedido.setTotal(subtotal.add(despacho));
		// Un solo save: por el cascade también se insertan todas las líneas del pedido
		return pedidoRepository.save(pedido);
	}
}
