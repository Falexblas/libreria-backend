package com.libreria.repository;

import com.libreria.model.Reseña;
import org.springframework.data.jpa.repository.JpaRepository;

import com.libreria.model.Libro;

import java.util.List;

public interface ReseñaRepository extends JpaRepository<Reseña, Long> {
    List<Reseña> findByLibroAndActivoTrue(Libro libro);
}
