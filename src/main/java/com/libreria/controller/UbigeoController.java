package com.libreria.controller;

import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/api/ubigeo")
@CrossOrigin(origins = "*")
public class UbigeoController {

    private String loadJson(String fileName) throws IOException {
        ClassPathResource resource = new ClassPathResource(fileName);
        try (InputStream is = resource.getInputStream()) {
            byte[] bytes = is.readAllBytes();
            return new String(bytes, StandardCharsets.UTF_8);
        }
    }

    @GetMapping("/departamentos")
    public ResponseEntity<String> getDepartamentos() {
        try {
            return ResponseEntity.ok(loadJson("ubigeo_peru_2016_departamentos.json"));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\":\"No se pudieron cargar los departamentos\"}");
        }
    }

    @GetMapping("/provincias")
    public ResponseEntity<String> getProvincias() {
        try {
            return ResponseEntity.ok(loadJson("ubigeo_peru_2016_provincias.json"));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\":\"No se pudieron cargar las provincias\"}");
        }
    }

    @GetMapping("/distritos")
    public ResponseEntity<String> getDistritos() {
        try {
            return ResponseEntity.ok(loadJson("ubigeo_peru_2016_distritos.json"));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\":\"No se pudieron cargar los distritos\"}");
        }
    }
}
