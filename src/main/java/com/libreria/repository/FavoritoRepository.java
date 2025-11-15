package com.libreria.repository;

import com.libreria.model.Favorito;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.libreria.model.Libro;
import com.libreria.model.Usuario;

import java.util.List;

public interface FavoritoRepository extends JpaRepository<Favorito, Long> {
    List<Favorito> findByUsuario(Usuario usuario);
    boolean existsByUsuarioAndLibro(Usuario usuario, Libro libro);
    void deleteByUsuarioIdAndLibroId(Long usuarioId, Long libroId);

    /**
     * Obtiene los libros favoritos de un usuario
     */
    @Query("SELECT f.libro FROM Favorito f " +
           "WHERE f.usuario.id = :usuarioId " +
           "ORDER BY f.fechaAgregado DESC")
    List<Libro> findLibrosFavoritosPorUsuario(@Param("usuarioId") Long usuarioId);
}
