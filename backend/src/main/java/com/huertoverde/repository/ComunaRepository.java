package com.huertoverde.repository;

import com.huertoverde.entity.Comuna;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ComunaRepository extends JpaRepository<Comuna, Long> {

	List<Comuna> findByRegionIdOrderByNombreAsc(Long regionId);

	Optional<Comuna> findByNombreAndRegionNombre(String nombre, String regionNombre);
}
