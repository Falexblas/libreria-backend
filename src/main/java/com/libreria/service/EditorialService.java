package com.libreria.service;

import com.libreria.model.Editorial;
import java.util.List;
import java.util.Optional;

public interface EditorialService {
    List<Editorial> obtenerTodasLasEditoriales();
    Optional<Editorial> obtenerEditorialPorId(Long id);
    Editorial guardarEditorial(Editorial editorial);
    void eliminarEditorial(Long id);
}
