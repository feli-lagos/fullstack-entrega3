package com.huertoverde.repository;

import com.huertoverde.entity.ImagenProducto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

// El id de cada imagen es el id de su producto
@Repository
public interface ImagenProductoRepository extends JpaRepository<ImagenProducto, Long> {
}
