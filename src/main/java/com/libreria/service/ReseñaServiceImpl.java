package com.libreria.service;

import com.libreria.model.Libro;
import com.libreria.model.Reseña;
import com.libreria.model.Usuario;
import com.libreria.repository.ReseñaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReseñaServiceImpl implements ReseñaService {

    @Autowired
    private ReseñaRepository reseñaRepository;

    @Override
    public Reseña guardarReseña(Reseña reseña, Usuario usuario, Libro libro) {
        reseña.setUsuario(usuario);
        reseña.setLibro(libro);
        reseña.setFecha(LocalDateTime.now());
        reseña.setActivo(true);
        return reseñaRepository.save(reseña);
    }

    @Override
    public List<Reseña> obtenerReseñasPorLibro(Libro libro) {
        return reseñaRepository.findByLibroAndActivoTrue(libro);
    }
}
