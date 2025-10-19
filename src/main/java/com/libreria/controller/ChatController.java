package com.libreria.controller;

import com.libreria.dto.ChatRequest;
import com.libreria.dto.ChatResponse;
import com.libreria.service.GeminiService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chat")
@CrossOrigin(origins = "*")
@Slf4j
public class ChatController {

    @Autowired
    private GeminiService geminiService;

    /**
     * Endpoint para procesar preguntas del chat
     */
    @PostMapping("/preguntar")
    public ResponseEntity<ChatResponse> preguntar(@RequestBody ChatRequest request) {
        log.info("Pregunta recibida: {}", request.getMensaje());
        
        if (request.getMensaje() == null || request.getMensaje().trim().isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(ChatResponse.builder()
                            .respuesta("Por favor, escribe una pregunta.")
                            .build());
        }
        
        try {
            ChatResponse response = geminiService.procesarPregunta(request.getMensaje());
            log.info("Respuesta generada con {} libros recomendados", 
                    response.getLibrosRecomendados().size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error al procesar pregunta: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError()
                    .body(ChatResponse.builder()
                            .respuesta("Lo siento, ocurrió un error al procesar tu pregunta. Por favor, intenta de nuevo.")
                            .build());
        }
    }

    /**
     * Endpoint de prueba para verificar que el servicio está funcionando
     */
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Chat service is running");
    }
}
