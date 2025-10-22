package com.libreria.controller;

import com.libreria.model.Libro;
import com.libreria.model.Usuario;
import com.libreria.service.FavoritoService;
import com.libreria.service.LibroService;
import com.libreria.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import com.libreria.model.Favorito;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/favoritos")
public class FavoritoController {

    @Autowired
    private FavoritoService favoritoService;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private LibroService libroService;

    @GetMapping
    public ResponseEntity<List<Favorito>> verFavoritos(@AuthenticationPrincipal UserDetails userDetails) {
        Usuario usuario = usuarioService.buscarPorEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        List<Favorito> favoritos = favoritoService.obtenerFavoritosPorUsuario(usuario);
        return ResponseEntity.ok(favoritos);
    }

    @PostMapping("/agregar/{libroId}")
    public ResponseEntity<Favorito> agregarAFavoritos(@PathVariable Long libroId, @AuthenticationPrincipal UserDetails userDetails) {
        Usuario usuario = usuarioService.buscarPorEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        Libro libro = libroService.obtenerLibroPorId(libroId)
                .orElseThrow(() -> new RuntimeException("Libro no encontrado"));

        Favorito nuevoFavorito = favoritoService.agregarAFavoritos(usuario, libro);
        return ResponseEntity.ok(nuevoFavorito);
    }

    @DeleteMapping("/eliminar-libro/{libroId}")
    public ResponseEntity<Void> eliminarDeFavoritosPorLibro(
            @PathVariable Long libroId,
            @AuthenticationPrincipal UserDetails userDetails) {
        Usuario usuario = usuarioService.buscarPorEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        favoritoService.eliminarDeFavoritosPorUsuarioYLibro(usuario.getId(), libroId);
        return ResponseEntity.noContent().build();
    }
}
