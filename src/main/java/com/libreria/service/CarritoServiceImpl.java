package com.libreria.service;

import com.libreria.model.*;
import com.libreria.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class CarritoServiceImpl implements CarritoService {

    @Autowired
    private CarritoRepository carritoRepository;

    @Autowired
    private ItemCarritoRepository itemCarritoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private LibroRepository libroRepository;

    @Override
    @Transactional
    public Carrito obtenerCarritoPorUsuarioId(Long usuarioId) {
        return carritoRepository.findByUsuarioId(usuarioId).orElseGet(() -> {
            Usuario usuario = usuarioRepository.findById(usuarioId)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
            Carrito nuevoCarrito = new Carrito();
            nuevoCarrito.setUsuario(usuario);
            nuevoCarrito.setFechaCreacion(LocalDateTime.now());
            nuevoCarrito.setModificado(LocalDateTime.now());
            return carritoRepository.save(nuevoCarrito);
        });
    }

    @Override
    @Transactional
    public Carrito agregarLibroAlCarrito(Long usuarioId, Long libroId, int cantidad) {
        Carrito carrito = obtenerCarritoPorUsuarioId(usuarioId);
        Libro libro = libroRepository.findById(libroId)
                .orElseThrow(() -> new RuntimeException("Libro no encontrado"));

        ItemCarrito item = itemCarritoRepository.findByCarritoIdAndLibroId(carrito.getId(), libroId)
                .orElse(new ItemCarrito());

        item.setCarrito(carrito);
        item.setLibro(libro);
        item.setCantidad(item.getCantidad() + cantidad);
        item.setPrecioUnitario(libro.getPrecio());

        itemCarritoRepository.save(item);
        carrito.setModificado(LocalDateTime.now());
        return carritoRepository.save(carrito);
    }

    @Override
    @Transactional
    public Carrito eliminarLibroDelCarrito(Long usuarioId, Long libroId) {
        Carrito carrito = obtenerCarritoPorUsuarioId(usuarioId);
        ItemCarrito item = itemCarritoRepository.findByCarritoIdAndLibroId(carrito.getId(), libroId)
                .orElseThrow(() -> new RuntimeException("Item no encontrado en el carrito"));

        itemCarritoRepository.delete(item);
        carrito.setModificado(LocalDateTime.now());
        return carritoRepository.save(carrito);
    }

    @Override
    @Transactional
    public void limpiarCarrito(Long usuarioId) {
        Carrito carrito = obtenerCarritoPorUsuarioId(usuarioId);
        itemCarritoRepository.deleteByCarritoId(carrito.getId());
        carrito.setModificado(LocalDateTime.now());
        carritoRepository.save(carrito);
    }
}
