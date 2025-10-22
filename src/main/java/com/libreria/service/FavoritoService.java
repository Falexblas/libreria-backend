package com.libreria.service;

import com.libreria.model.Favorito;
import com.libreria.model.Libro;
import com.libreria.model.Usuario;

import java.util.List;

public interface FavoritoService {
    Favorito agregarAFavoritos(Usuario usuario, Libro libro);
    List<Favorito> obtenerFavoritosPorUsuario(Usuario usuario);
    void eliminarDeFavoritosPorUsuarioYLibro(Long usuarioId, Long libroId);
}
