package com.libreria.controller;

import com.libreria.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;
import com.libreria.JWT.AuthResponse;
import com.libreria.JWT.LoginRequest;
import com.libreria.JWT.RegisterRequest;



@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UsuarioService usuarioService;

  
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> save(@RequestBody RegisterRequest usuario) {
        return ResponseEntity.ok(usuarioService.register(usuario));
    }

      @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest loginRequest) {
        return ResponseEntity.ok(usuarioService.login(loginRequest));
    }
}
