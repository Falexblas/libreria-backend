package com.libreria.service;

import com.libreria.dto.*;
import com.libreria.model.*;
import com.libreria.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

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

    // ========================================
    // NUEVOS MÉTODOS CON DTOs
    // ========================================

    @Override
    @Transactional
    public CarritoDTO obtenerCarrito(Long usuarioId) {
        Carrito carrito = obtenerCarritoPorUsuarioId(usuarioId);
        return convertirADTO(carrito);
    }

    @Override
    @Transactional
    public ItemCarritoDTO agregarItem(Long usuarioId, AgregarItemRequest request) {
        Carrito carrito = obtenerCarritoPorUsuarioId(usuarioId);
        Libro libro = libroRepository.findById(request.getLibroId())
                .orElseThrow(() -> new RuntimeException("Libro no encontrado"));

        // Verificar stock
        if (libro.getStock() < request.getCantidad()) {
            throw new RuntimeException("Stock insuficiente");
        }

        // Buscar si el item ya existe
        ItemCarrito item = itemCarritoRepository.findByCarritoIdAndLibroId(carrito.getId(), libro.getId())
                .orElse(null);

        if (item != null) {
            // Actualizar cantidad
            int nuevaCantidad = item.getCantidad() + request.getCantidad();
            if (libro.getStock() < nuevaCantidad) {
                throw new RuntimeException("Stock insuficiente para la cantidad solicitada");
            }
            item.setCantidad(nuevaCantidad);
        } else {
            // Crear nuevo item
            item = new ItemCarrito();
            item.setCarrito(carrito);
            item.setLibro(libro);
            item.setCantidad(request.getCantidad());
            item.setPrecioUnitario(libro.getPrecio());
        }

        itemCarritoRepository.save(item);
        carrito.setModificado(LocalDateTime.now());
        carritoRepository.save(carrito);

        return convertirItemADTO(item);
    }

    @Override
    @Transactional
    public ItemCarritoDTO actualizarCantidad(Long itemId, ActualizarCantidadRequest request) {
        ItemCarrito item = itemCarritoRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Item no encontrado"));

        // Verificar stock
        if (item.getLibro().getStock() < request.getCantidad()) {
            throw new RuntimeException("Stock insuficiente");
        }

        item.setCantidad(request.getCantidad());
        itemCarritoRepository.save(item);

        // Actualizar fecha de modificación del carrito
        Carrito carrito = item.getCarrito();
        carrito.setModificado(LocalDateTime.now());
        carritoRepository.save(carrito);

        return convertirItemADTO(item);
    }

    @Override
    @Transactional
    public void eliminarItem(Long itemId) {
        ItemCarrito item = itemCarritoRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Item no encontrado"));

        Carrito carrito = item.getCarrito();
        itemCarritoRepository.delete(item);
        
        carrito.setModificado(LocalDateTime.now());
        carritoRepository.save(carrito);
    }

    @Override
    @Transactional
    public void vaciarCarrito(Long usuarioId) {
        limpiarCarrito(usuarioId);
    }

    // ========================================
    // MÉTODOS AUXILIARES
    // ========================================

    private CarritoDTO convertirADTO(Carrito carrito) {
        CarritoDTO dto = new CarritoDTO();
        dto.setId(carrito.getId());
        dto.setUsuarioId(carrito.getUsuario().getId());
        dto.setFechaCreacion(carrito.getFechaCreacion());
        dto.setModificado(carrito.getModificado());

        // Obtener items del carrito
        List<ItemCarrito> items = itemCarritoRepository.findByCarritoId(carrito.getId());
        List<ItemCarritoDTO> itemsDTO = items.stream()
                .map(this::convertirItemADTO)
                .collect(Collectors.toList());
        dto.setItems(itemsDTO);

        // Calcular totales
        BigDecimal subtotal = itemsDTO.stream()
                .map(ItemCarritoDTO::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        dto.setSubtotal(subtotal);

        int cantidadTotal = itemsDTO.stream()
                .mapToInt(ItemCarritoDTO::getCantidad)
                .sum();
        dto.setCantidadTotal(cantidadTotal);

        return dto;
    }

    private ItemCarritoDTO convertirItemADTO(ItemCarrito item) {
        ItemCarritoDTO dto = new ItemCarritoDTO();
        dto.setId(item.getId());
        dto.setCarritoId(item.getCarrito().getId());
        dto.setLibro(item.getLibro());
        dto.setCantidad(item.getCantidad());
        dto.setPrecioUnitario(item.getPrecioUnitario());
        
        // Calcular subtotal
        BigDecimal subtotal = item.getPrecioUnitario().multiply(BigDecimal.valueOf(item.getCantidad()));
        dto.setSubtotal(subtotal);

        return dto;
    }
}
