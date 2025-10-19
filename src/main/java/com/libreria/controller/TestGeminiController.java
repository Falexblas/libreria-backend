package com.libreria.controller;

import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/api/test")
@CrossOrigin(origins = "*")
@Slf4j
public class TestGeminiController {

    @Value("${gemini.api.key}")
    private String apiKey;

    private final OkHttpClient httpClient = new OkHttpClient();

    /**
     * Endpoint para listar todos los modelos disponibles con tu API key
     */
    @GetMapping("/gemini-models")
    public String listarModelos() {
        try {
            // URL para listar modelos
            String url = "https://generativelanguage.googleapis.com/v1beta/models?key=" + apiKey;
            
            Request request = new Request.Builder()
                    .url(url)
                    .get()
                    .build();
            
            try (Response response = httpClient.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    return "Error: " + response.code() + " - " + response.body().string();
                }
                
                String responseBody = response.body().string();
                log.info("Modelos disponibles: {}", responseBody);
                return responseBody;
            }
            
        } catch (IOException e) {
            log.error("Error al listar modelos: {}", e.getMessage());
            return "Error: " + e.getMessage();
        }
    }
}
