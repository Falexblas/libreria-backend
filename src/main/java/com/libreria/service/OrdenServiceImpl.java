package com.libreria.service;

import com.libreria.model.*;
import com.libreria.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
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
    public Orden crearOrden(Usuario usuario, String metodoPago, String direccionEnvio, String ciudadEnvio, String codigoPostalEnvio, String telefonoContacto, String notas) {
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
        orden.setNotas(notas);  // Agregar notas
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
    @Transactional
    public Orden crearOrdenDesdeCheckout(Usuario usuario, List<Map<String, Object>> itemsData, String metodoPago, String direccionEnvio, String ciudadEnvio, String codigoPostalEnvio, String telefonoContacto, String notas) {
        
        if (itemsData == null || itemsData.isEmpty()) {
            throw new RuntimeException("El carrito está vacío");
        }

        // Crear orden
        Orden orden = new Orden();
        orden.setUsuario(usuario);
        orden.setFechaPedido(LocalDateTime.now());
        orden.setEstado("pendiente");
        orden.setMetodoPago(metodoPago);
        orden.setDireccionEnvio(direccionEnvio);
        orden.setCiudadEnvio(ciudadEnvio);
        orden.setCodigoPostalEnvio(codigoPostalEnvio);
        orden.setTelefonoContacto(telefonoContacto);
        orden.setNotas(notas);
        orden.setFechaActualizacion(LocalDateTime.now());

        // Calcular total
        BigDecimal total = BigDecimal.ZERO;
        for (Map<String, Object> itemData : itemsData) {
            Number cantidadNum = (Number) itemData.get("cantidad");
            Number precioNum = (Number) itemData.get("precioUnitario");
            
            BigDecimal precio = new BigDecimal(precioNum.toString());
            int cantidad = cantidadNum.intValue();
            
            total = total.add(precio.multiply(new BigDecimal(cantidad)));
        }
        orden.setTotal(total);

        // Guardar orden
        Orden ordenGuardada = ordenRepository.save(orden);

        // Crear detalles de orden y actualizar stock
        for (Map<String, Object> itemData : itemsData) {
            Number libroIdNum = (Number) itemData.get("libroId");
            Number cantidadNum = (Number) itemData.get("cantidad");
            Number precioNum = (Number) itemData.get("precioUnitario");
            
            Long libroId = libroIdNum.longValue();
            int cantidad = cantidadNum.intValue();
            BigDecimal precioUnitario = new BigDecimal(precioNum.toString());
            
            // Buscar libro
            Libro libro = libroRepository.findById(libroId)
                    .orElseThrow(() -> new RuntimeException("Libro no encontrado: " + libroId));
            
            // Crear detalle de orden
            DetalleOrden detalle = new DetalleOrden();
            detalle.setOrden(ordenGuardada);
            detalle.setLibro(libro);
            detalle.setCantidad(cantidad);
            detalle.setPrecioUnitario(precioUnitario);
            detalleOrdenRepository.save(detalle);

            // Actualizar stock
            int nuevoStock = libro.getStock() - cantidad;
            if (nuevoStock < 0) {
                throw new RuntimeException("Stock insuficiente para el libro: " + libro.getTitulo());
            }
            libro.setStock(nuevoStock);
            libroRepository.save(libro);
        }

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

    @Override
    public Object obtenerDetallesOrden(Long id, Usuario usuario) {
        Orden orden = ordenRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Orden no encontrada"));
        
        // Verificar que la orden pertenece al usuario
        if (!orden.getUsuario().getId().equals(usuario.getId())) {
            throw new RuntimeException("No tienes permiso para ver esta orden");
        }
        
        // Obtener detalles de la orden
        return detalleOrdenRepository.findByOrdenId(id);
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
