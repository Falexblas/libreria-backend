package com.libreria.controller;

import com.libreria.dto.ChatRequest;
import com.libreria.dto.ChatResponse;
import com.libreria.model.Usuario;
import com.libreria.repository.UsuarioRepository;
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

    @Autowired
    private UsuarioRepository usuarioRepository;

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
     * Endpoint para obtener recomendaciones personalizadas basadas en el historial del usuario
     * Usado para notificaciones en tiempo real
     */
    @GetMapping("/recomendaciones/personalizadas")
    @org.springframework.security.access.prepost.PreAuthorize("isAuthenticated()")
    public ResponseEntity<ChatResponse> obtenerRecomendacionesPersonalizadas(
            @org.springframework.security.core.annotation.AuthenticationPrincipal org.springframework.security.core.userdetails.UserDetails userDetails) {
        try {
            // Obtener el usuario autenticado
            String username = userDetails.getUsername();
            log.info("Obteniendo recomendaciones personalizadas para usuario: {}", username);
            
            // Aquí necesitarás obtener el usuarioId del usuario autenticado
            // Esto depende de cómo tengas configurada tu autenticación
            // Por ahora, asumimos que tienes un método para obtener el usuario por username
            
            ChatResponse response = geminiService.obtenerRecomendacionesPersonalizadas(
                    obtenerUsuarioIdDelUsuarioAutenticado(userDetails)
            );
            
            log.info("Recomendaciones generadas con {} libros", 
                    response.getLibrosRecomendados().size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error al obtener recomendaciones personalizadas: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError()
                    .body(ChatResponse.builder()
                            .respuesta("Lo siento, no pude generar recomendaciones en este momento.")
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

    /**
     * Método auxiliar para obtener el ID del usuario autenticado
     */
    private Long obtenerUsuarioIdDelUsuarioAutenticado(org.springframework.security.core.userdetails.UserDetails userDetails) {
        Usuario usuario = usuarioRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        return usuario.getId();
    }
}
