package com.libreria.service;

import com.libreria.model.Carrito;

public interface CarritoService {
    Carrito obtenerCarritoPorUsuarioId(Long usuarioId);
    Carrito agregarLibroAlCarrito(Long usuarioId, Long libroId, int cantidad);
    Carrito eliminarLibroDelCarrito(Long usuarioId, Long libroId);
    void limpiarCarrito(Long usuarioId);
}
