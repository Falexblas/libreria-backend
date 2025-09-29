package com.libreria.service;

import com.libreria.model.Libro;
import com.libreria.repository.LibroRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class LibroServiceImpl implements LibroService {

    @Autowired
    private LibroRepository libroRepository;

    @Override
    public List<Libro> obtenerTodosLosLibros() {
        return libroRepository.findByActivoTrue();
    }

    @Override
    public Optional<Libro> obtenerLibroPorId(Long id) {
        return libroRepository.findById(id);
    }

    @Override
    public Libro guardarLibro(Libro libro) {
        return libroRepository.save(libro);
    }

    @Override
    public void eliminarLibro(Long id) {
        libroRepository.deleteById(id);
    }

    @Override
    public List<Libro> obtenerLibrosPorCategoria(Long categoriaId) {
        return libroRepository.findByCategoriaIdAndActivoTrue(categoriaId);
    }

    @Override
    public List<Libro> obtenerLibrosDestacados() {
        return libroRepository.findByDestacadoTrueAndActivoTrue();
    }
}
