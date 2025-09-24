package com.libreria.repository;

import com.libreria.model.Favorito;
import org.springframework.data.jpa.repository.JpaRepository;

import com.libreria.model.Libro;
import com.libreria.model.Usuario;

import java.util.List;

public interface FavoritoRepository extends JpaRepository<Favorito, Long> {
    List<Favorito> findByUsuario(Usuario usuario);
    boolean existsByUsuarioAndLibro(Usuario usuario, Libro libro);
}
