package com.libreria.service;

import com.libreria.model.Editorial;
import com.libreria.repository.EditorialRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EditorialServiceImpl implements EditorialService {

    @Autowired
    private EditorialRepository editorialRepository;

    @Override
    public List<Editorial> obtenerTodasLasEditoriales() {
        return editorialRepository.findAll();
    }

    @Override
    public Optional<Editorial> obtenerEditorialPorId(Long id) {
        return editorialRepository.findById(id);
    }

    @Override
    public Editorial guardarEditorial(Editorial editorial) {
        return editorialRepository.save(editorial);
    }

    @Override
    public void eliminarEditorial(Long id) {
        editorialRepository.deleteById(id);
    }
}
