package com.libreria.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "usuarios")
public class Usuario implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ========================================
    // CAMPOS OBLIGATORIOS PARA REGISTRO
    // ========================================
    
    @NotEmpty(message = "El nombre no puede estar vacío")
    @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres")
    @Column(length = 100, nullable = false)
    private String nombre;

    @Column(length = 100)  // Opcional
    private String apellido;

    @NotEmpty(message = "El email no puede estar vacío")
    @Email(message = "El formato del email no es válido")
    @Column(length = 150, nullable = false, unique = true)
    private String email;

    @NotEmpty(message = "La contraseña no puede estar vacía")
    @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres")
    @Column(name = "password_hash", nullable = false)
    private String password;

    @ManyToOne
    @JoinColumn(name = "rol_id", nullable = false)
    private Rol rol;

    // ========================================
    // CAMPOS OPCIONALES (Se completan después)
    // ========================================
    
    @Column(length = 20)
    private String telefono;  // Opcional - Se completa en perfil o checkout

    @Column(length = 20)
    private String documento;  // Opcional - DNI, CE, Pasaporte

    private String direccion;  // Opcional - Se completa en checkout

    @Column(name = "referencia_direccion")
    private String referenciaDireccion;  // Opcional - Referencia adicional

    @Column(length = 100)
    private String departamento;  // Opcional - Se completa en checkout

    @Column(length = 100)
    private String provincia;  // Opcional - Se completa en checkout

    @Column(length = 100)
    private String distrito;  // Opcional - Se completa en checkout

    @Column(name = "codigo_postal", length = 10)
    private String codigoPostal;  // Opcional - Se completa en checkout

    private String notas;  // Opcional - Notas adicionales

    @Column(name = "fecha_registro", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime fechaRegistro;

    @Builder.Default
    private boolean activo = true;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(rol.getNombre()));
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}