package com.libreria.service;

import com.libreria.model.Orden;
import com.libreria.model.Usuario;

import java.util.List;
import java.util.Optional;

public interface OrdenService {
    Orden crearOrden(Usuario usuario, String metodoPago, String direccionEnvio, String ciudadEnvio, String codigoPostalEnvio, String telefonoContacto);
    Optional<Orden> obtenerOrdenPorId(Long id);
    List<Orden> obtenerOrdenesPorUsuario(Usuario usuario);
    
    // Métodos para administración
    List<Orden> obtenerTodasLasOrdenes();
    Orden actualizarEstadoOrden(Long id, String nuevoEstado);
}
