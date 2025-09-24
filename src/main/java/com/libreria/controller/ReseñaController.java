package com.libreria.controller;

import com.libreria.model.Libro;
import com.libreria.model.Reseña;
import com.libreria.model.Usuario;
import com.libreria.service.LibroService;
import com.libreria.service.ReseñaService;
import com.libreria.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reseñas")
public class ReseñaController {

    @Autowired
    private ReseñaService reseñaService;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private LibroService libroService;

    @PostMapping("/libro/{libroId}")
    public ResponseEntity<Reseña> guardarReseña(@PathVariable Long libroId,
                                                @RequestBody Reseña reseña,
                                                @AuthenticationPrincipal UserDetails userDetails) {
        
        Usuario usuario = usuarioService.buscarPorEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Libro libro = libroService.obtenerLibroPorId(libroId)
                .orElseThrow(() -> new RuntimeException("Libro no encontrado"));

        Reseña nuevaReseña = reseñaService.guardarReseña(reseña, usuario, libro);

        return new ResponseEntity<>(nuevaReseña, HttpStatus.CREATED);
    }

}
