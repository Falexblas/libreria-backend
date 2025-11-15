package com.libreria.repository;

import com.libreria.model.DetalleOrden;
import com.libreria.model.Libro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DetalleOrdenRepository extends JpaRepository<DetalleOrden, Long> {
    List<DetalleOrden> findByOrdenId(Long ordenId);

    /**
     * Obtiene los libros comprados por un usuario
     */
    @Query("SELECT DISTINCT d.libro FROM DetalleOrden d " +
           "JOIN d.orden o " +
           "WHERE o.usuario.id = :usuarioId " +
           "ORDER BY o.fechaPedido DESC")
    List<Libro> findLibrosCompradosPorUsuario(@Param("usuarioId") Long usuarioId);
}
