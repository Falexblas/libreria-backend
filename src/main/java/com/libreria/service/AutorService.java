package com.libreria.service;

import com.libreria.model.Autor;
import java.util.List;
import java.util.Optional;

public interface AutorService {
    List<Autor> obtenerTodosLosAutores();
    Optional<Autor> obtenerAutorPorId(Long id);
    Autor guardarAutor(Autor autor);
    void eliminarAutor(Long id);
}
