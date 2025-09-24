package com.libreria.service;

import com.libreria.model.Libro;
import com.libreria.model.Reseña;
import com.libreria.model.Usuario;

import java.util.List;

public interface ReseñaService {
    Reseña guardarReseña(Reseña reseña, Usuario usuario, Libro libro);
    List<Reseña> obtenerReseñasPorLibro(Libro libro);
}
