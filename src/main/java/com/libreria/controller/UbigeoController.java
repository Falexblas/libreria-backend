package com.libreria.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@RestController
@RequestMapping("/api/ubigeo")
@CrossOrigin(origins = "*")
public class UbigeoController {

    private String loadJson(String fileName) throws IOException {
        byte[] bytes = Files.readAllBytes(Paths.get("src/main/resources/" + fileName));
        return new String(bytes);
    }

    @GetMapping("/departamentos")
    public ResponseEntity<String> getDepartamentos() throws IOException {
        return ResponseEntity.ok(loadJson("ubigeo_peru_2016_departamentos.json"));
    }

    @GetMapping("/provincias")
    public ResponseEntity<String> getProvincias() throws IOException {
        return ResponseEntity.ok(loadJson("ubigeo_peru_2016_provincias.json"));
    }

    @GetMapping("/distritos")
    public ResponseEntity<String> getDistritos() throws IOException {
        return ResponseEntity.ok(loadJson("ubigeo_peru_2016_distritos.json")); // CORREGIDO
    }
}
