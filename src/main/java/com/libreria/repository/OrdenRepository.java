package com.libreria.repository;

import com.libreria.model.Orden;
import org.springframework.data.jpa.repository.JpaRepository;

import com.libreria.model.Usuario;
import java.util.List;

public interface OrdenRepository extends JpaRepository<Orden, Long> {
    List<Orden> findByUsuario(Usuario usuario);
}
