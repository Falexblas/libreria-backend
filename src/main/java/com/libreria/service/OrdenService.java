package com.libreria.service;

import com.libreria.dto.FacturaDTO;
import com.libreria.model.Orden;
import com.libreria.model.Usuario;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface OrdenService {
    Orden crearOrden(Usuario usuario, String metodoPago, String direccionEnvio, String ciudadEnvio, String codigoPostalEnvio, String telefonoContacto, String notas);
    Orden crearOrdenDesdeCheckout(Usuario usuario, List<Map<String, Object>> items, String metodoPago, String direccionEnvio, String ciudadEnvio, String codigoPostalEnvio, String telefonoContacto, String notas, BigDecimal tarifaEnvio);
    Optional<Orden> obtenerOrdenPorId(Long id);
    List<Orden> obtenerOrdenesPorUsuario(Usuario usuario);
    Object obtenerDetallesOrden(Long id, Usuario usuario);
    
    // Métodos para administración
    List<Orden> obtenerTodasLasOrdenes();
    Orden actualizarEstadoOrden(Long id, String nuevoEstado);
    Object obtenerDetallesOrdenAdmin(Long id);  // Sin validación de usuario
    
    // Método para generar datos de factura
    FacturaDTO generarDatosFactura(Long ordenId, Usuario usuario);
}
