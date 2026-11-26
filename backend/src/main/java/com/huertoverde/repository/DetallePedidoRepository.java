package com.huertoverde.repository;

import com.huertoverde.entity.DetallePedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DetallePedidoRepository extends JpaRepository<DetallePedido, Long> {

	boolean existsByProductoId(Long productoId);
}
