package com.libreria.repository;

import com.libreria.model.ItemCarrito;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ItemCarritoRepository extends JpaRepository<ItemCarrito, Long> {
    Optional<ItemCarrito> findByCarritoIdAndLibroId(Long carritoId, Long libroId);
    List<ItemCarrito> findByCarritoId(Long carritoId);
    void deleteByCarritoId(Long carritoId);
}
