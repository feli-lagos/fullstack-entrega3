package com.huertoverde.repository;

import com.huertoverde.entity.Suscripcion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SuscripcionRepository extends JpaRepository<Suscripcion, Long> {

	boolean existsByCorreoIgnoreCase(String correo);
}
