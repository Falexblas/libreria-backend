package com.libreria.service;

import com.libreria.JWT.AuthResponse;
import com.libreria.JWT.LoginRequest;
import com.libreria.JWT.RegisterRequest;
import com.libreria.model.Usuario;

import java.util.List;
import java.util.Optional;

public interface UsuarioService {
    
    // Métodos CRUD básicos
    Usuario registrarUsuario(Usuario usuario);
    Optional<Usuario> buscarPorEmail(String email);
    Optional<Usuario> buscarPorId(Long id);
    List<Usuario> obtenerTodosLosUsuarios();
    Optional<Usuario> obtenerUsuarioPorId(Long id);
    Usuario actualizarUsuario(Usuario usuario);
    void eliminarUsuario(Long id);

    // Métodos de autenticación
    AuthResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
    AuthResponse refreshToken(String token);
}