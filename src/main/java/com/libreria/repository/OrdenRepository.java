package com.libreria.repository;

import com.libreria.model.Orden;
import com.libreria.dto.VentasPorMesDTO;
import com.libreria.dto.TopLibroDTO;
import com.libreria.dto.CategoriaPopularDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.libreria.model.Usuario;
import java.util.List;

public interface OrdenRepository extends JpaRepository<Orden, Long> {
    List<Orden> findByUsuario(Usuario usuario);
    
    // Ventas por mes del año actual
    @Query("SELECT new com.libreria.dto.VentasPorMesDTO(MONTH(o.fechaPedido), SUM(o.total)) " +
           "FROM Orden o " +
           "WHERE YEAR(o.fechaPedido) = :anio " +
           "GROUP BY MONTH(o.fechaPedido) " +
           "ORDER BY MONTH(o.fechaPedido)")
    List<VentasPorMesDTO> findVentasPorMes(@Param("anio") int anio);
    
    // Contar órdenes por estado
    @Query("SELECT o.estado, COUNT(o) FROM Orden o GROUP BY o.estado")
    List<Object[]> countByEstado();
    
    // Top 5 libros más vendidos
    @Query("SELECT new com.libreria.dto.TopLibroDTO(l.titulo, SUM(d.cantidad)) " +
           "FROM DetalleOrden d " +
           "JOIN d.libro l " +
           "GROUP BY l.id, l.titulo " +
           "ORDER BY SUM(d.cantidad) DESC")
    List<TopLibroDTO> findTopLibros();
    
    // Categorías más populares
    @Query("SELECT new com.libreria.dto.CategoriaPopularDTO(c.nombre, SUM(d.cantidad)) " +
           "FROM DetalleOrden d " +
           "JOIN d.libro l " +
           "JOIN l.categoria c " +
           "GROUP BY c.id, c.nombre " +
           "ORDER BY SUM(d.cantidad) DESC")
    List<CategoriaPopularDTO> findCategoriasPopulares();
}
