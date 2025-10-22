package com.libreria.service;

import com.libreria.model.Favorito;
import com.libreria.model.Libro;
import com.libreria.model.Usuario;
import com.libreria.repository.FavoritoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class FavoritoServiceImpl implements FavoritoService {

    @Autowired
    private FavoritoRepository favoritoRepository;

    @Override
    public Favorito agregarAFavoritos(Usuario usuario, Libro libro) {
        if (favoritoRepository.existsByUsuarioAndLibro(usuario, libro)) {
            throw new RuntimeException("El libro ya está en favoritos");
        }
        Favorito favorito = new Favorito();
        favorito.setUsuario(usuario);
        favorito.setLibro(libro);
        favorito.setFechaAgregado(LocalDateTime.now());
        return favoritoRepository.save(favorito);
    }

    @Override
    public List<Favorito> obtenerFavoritosPorUsuario(Usuario usuario) {
        return favoritoRepository.findByUsuario(usuario);
    }

    @Override
    @Transactional
    public void eliminarDeFavoritosPorUsuarioYLibro(Long usuarioId, Long libroId) {
        favoritoRepository.deleteByUsuarioIdAndLibroId(usuarioId, libroId);
    }
}
