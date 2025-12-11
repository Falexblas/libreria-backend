package com.libreria.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/envio")
@CrossOrigin(origins = "*")
public class EnvioController {

    @PostMapping("/calcular")
    public ResponseEntity<?> calcularEnvio(@RequestBody Map<String, String> data) {
        String departamento = data.get("departamento");
        
        // Tarifas por departamento
        double tarifa;
        if (departamento == null || departamento.trim().isEmpty()) {
            tarifa = 0.0;
        } else if (departamento.equalsIgnoreCase("Lima") || 
                   departamento.equalsIgnoreCase("Callao")) {
            tarifa = 8.0; // Lima y Callao
        } else {
            tarifa = 20.0; // Provincia
        }
        
        return ResponseEntity.ok(Map.of("tarifa", tarifa));
    }
}