package com.libreria.service;

import com.libreria.model.Libro;
import java.util.List;
import java.util.Optional;

public interface LibroService {
    List<Libro> obtenerTodosLosLibros();
    Optional<Libro> obtenerLibroPorId(Long id);
    Libro guardarLibro(Libro libro);
    void eliminarLibro(Long id);
}
