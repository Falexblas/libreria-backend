package com.libreria.service;

import com.libreria.model.*;
import com.libreria.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class OrdenServiceImpl implements OrdenService {

    @Autowired
    private OrdenRepository ordenRepository;

    @Autowired
    private DetalleOrdenRepository detalleOrdenRepository;

    @Autowired
    private CarritoRepository carritoRepository;

    @Autowired
    private ItemCarritoRepository itemCarritoRepository;

    @Autowired
    private LibroRepository libroRepository;

    @Override
    @Transactional
    public Orden crearOrden(Usuario usuario, String metodoPago, String direccionEnvio, String ciudadEnvio, String codigoPostalEnvio, String telefonoContacto) {
        Carrito carrito = carritoRepository.findByUsuarioId(usuario.getId())
                .orElseThrow(() -> new RuntimeException("Carrito no encontrado para el usuario"));

        List<ItemCarrito> items = itemCarritoRepository.findByCarritoId(carrito.getId());
        if (items.isEmpty()) {
            throw new RuntimeException("El carrito está vacío");
        }

        Orden orden = new Orden();
        orden.setUsuario(usuario);
        orden.setFechaPedido(LocalDateTime.now());
        orden.setEstado("pendiente");
        orden.setMetodoPago(metodoPago);
        orden.setDireccionEnvio(direccionEnvio);
        orden.setCiudadEnvio(ciudadEnvio);
        orden.setCodigoPostalEnvio(codigoPostalEnvio);
        orden.setTelefonoContacto(telefonoContacto);
        orden.setFechaActualizacion(LocalDateTime.now());

        BigDecimal total = BigDecimal.ZERO;
        for (ItemCarrito item : items) {
            total = total.add(item.getPrecioUnitario().multiply(new BigDecimal(item.getCantidad())));
        }
        orden.setTotal(total);

        Orden ordenGuardada = ordenRepository.save(orden);

        for (ItemCarrito item : items) {
            DetalleOrden detalle = new DetalleOrden();
            detalle.setOrden(ordenGuardada);
            detalle.setLibro(item.getLibro());
            detalle.setCantidad(item.getCantidad());
            detalle.setPrecioUnitario(item.getPrecioUnitario());
            detalleOrdenRepository.save(detalle);

            // Actualizar stock
            Libro libro = item.getLibro();
            int nuevoStock = libro.getStock() - item.getCantidad();
            if (nuevoStock < 0) {
                throw new RuntimeException("Stock insuficiente para el libro: " + libro.getTitulo());
            }
            libro.setStock(nuevoStock);
            libroRepository.save(libro);
        }

        // Limpiar carrito
        itemCarritoRepository.deleteAll(items);

        return ordenGuardada;
    }

    @Override
    public Optional<Orden> obtenerOrdenPorId(Long id) {
        return ordenRepository.findById(id);
    }

    @Override
    public List<Orden> obtenerOrdenesPorUsuario(Usuario usuario) {
        return ordenRepository.findByUsuario(usuario);
    }

    // Métodos para administración
    @Override
    public List<Orden> obtenerTodasLasOrdenes() {
        return ordenRepository.findAll();
    }

    @Override
    @Transactional
    public Orden actualizarEstadoOrden(Long id, String nuevoEstado) {
        Orden orden = ordenRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Orden no encontrada"));
        
        orden.setEstado(nuevoEstado);
        orden.setFechaActualizacion(LocalDateTime.now());
        
        return ordenRepository.save(orden);
    }
}
