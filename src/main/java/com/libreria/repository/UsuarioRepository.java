package com.libreria.repository;

import com.libreria.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    List<Usuario> findAll();
   Optional<Usuario> findById(Long id); 
    Optional<Usuario> findByEmail(String email);
}
