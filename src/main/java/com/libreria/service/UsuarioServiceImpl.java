package com.libreria.service;

import com.libreria.JWT.AuthResponse;
import com.libreria.JWT.JwtService;
import com.libreria.JWT.LoginRequest;
import com.libreria.JWT.RegisterRequest;
import com.libreria.model.Rol;
import com.libreria.model.Usuario;
import com.libreria.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Usuario registrarUsuario(Usuario usuario) {
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        return usuarioRepository.save(usuario);
    }

    @Override
    public Optional<Usuario> buscarPorEmail(String email) {
        return usuarioRepository.findByEmail(email);
    }

    @Override
    public Optional<Usuario> buscarPorId(Long id) {
        return usuarioRepository.findById(id);
    }

    @Override
    public List<Usuario> obtenerTodosLosUsuarios() {
        return usuarioRepository.findAll();
    }

    @Override
    public Optional<Usuario> obtenerUsuarioPorId(Long id) {
        return usuarioRepository.findById(id);
    }

    @Override
    public Usuario actualizarUsuario(Usuario usuario) {
        if (usuario.getPassword() != null && !usuario.getPassword().isEmpty()) {
            usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        }
        return usuarioRepository.save(usuario);
    }

    @Override
    public void eliminarUsuario(Long id) {
        usuarioRepository.deleteById(id);
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        Usuario usuario = usuarioRepository.findByEmail(request.getEmail())
            .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
        
        String token = jwtService.getToken(usuario);
        
        // Crear DTO con solo la información segura necesaria
        com.libreria.JWT.UserDTO userDTO = com.libreria.JWT.UserDTO.builder()
                .id(usuario.getId())
                .nombre(usuario.getNombre())
                .email(usuario.getEmail())
                .rol(usuario.getRol() != null ? usuario.getRol().getNombre() : "USER")
                .build();
        
        return AuthResponse.builder()
                .token(token)
                .user(userDTO)
                .build();
    }

    @Override
    public AuthResponse register(RegisterRequest request) {
        if (usuarioRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalArgumentException("El email ya está registrado");
        }
  Rol rol = new Rol(1L, "USER");
        Usuario usuario = Usuario.builder()
                .nombre(request.getNombre())
        .apellido(request.getApellido())
        .email(request.getEmail())
        .password(passwordEncoder.encode(request.getPassword()))
        .rol(rol)  // ← Asignar rol aquí
        .telefono(request.getTelefono())
        .direccion(request.getDireccion())
        .codigoPostal(request.getCodigoPostal())
        .fechaRegistro(LocalDateTime.now())
        .activo(true)
        .build();
    

        usuarioRepository.save(usuario);
        
        String token = jwtService.getToken(usuario);
        
        // Crear DTO con solo la información segura necesaria
        com.libreria.JWT.UserDTO userDTO = com.libreria.JWT.UserDTO.builder()
                .id(usuario.getId())
                .nombre(usuario.getNombre())
                .email(usuario.getEmail())
                .rol(usuario.getRol() != null ? usuario.getRol().getNombre() : "USER")
                .build();
        
        return AuthResponse.builder()
                .token(token)
                .user(userDTO)
                .build();
    }

    @Override
    public AuthResponse refreshToken(String token) {
        try {
            // Extraer el email del token
            String email = jwtService.getUsernameFromToken(token);
            
            // Buscar al usuario
            UserDetails user = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
            
            // Generar nuevo token
            String newToken = jwtService.getToken(user);
            
            return AuthResponse.builder()
                    .token(newToken)
                    .build();
        } catch (Exception e) {
            throw new IllegalArgumentException("Token inválido o expirado", e);
        }
    }

    @Override
    public boolean verificarPassword(Usuario usuario, String passwordActual) {
        return passwordEncoder.matches(passwordActual, usuario.getPassword());
    }

    @Override
    public void cambiarPassword(Usuario usuario, String passwordNueva) {
        usuario.setPassword(passwordEncoder.encode(passwordNueva));
        usuarioRepository.save(usuario);
    }
}