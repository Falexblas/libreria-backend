package com.libreria.service;

import com.libreria.dto.*;
import com.libreria.model.Carrito;

public interface CarritoService {
    // Métodos antiguos (mantener para compatibilidad)
    Carrito obtenerCarritoPorUsuarioId(Long usuarioId);
    Carrito agregarLibroAlCarrito(Long usuarioId, Long libroId, int cantidad);
    Carrito eliminarLibroDelCarrito(Long usuarioId, Long libroId);
    void limpiarCarrito(Long usuarioId);
    
    // Nuevos métodos con DTOs
    CarritoDTO obtenerCarrito(Long usuarioId);
    ItemCarritoDTO agregarItem(Long usuarioId, AgregarItemRequest request);
    ItemCarritoDTO actualizarCantidad(Long itemId, ActualizarCantidadRequest request);
    void eliminarItem(Long itemId);
    void vaciarCarrito(Long usuarioId);
}
