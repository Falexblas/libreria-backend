package com.libreria.service;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.libreria.dto.ChatResponse;
import com.libreria.model.Libro;
import com.libreria.repository.LibroRepository;
import com.libreria.repository.FavoritoRepository;
import com.libreria.repository.DetalleOrdenRepository;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@Slf4j
public class GeminiService {

    @Value("${gemini.api.key}")
    private String apiKey;

    @Value("${gemini.api.url:https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent}")
    private String apiUrl;

    @Autowired
    private LibroRepository libroRepository;

    @Autowired
    private FavoritoRepository favoritoRepository;

    @Autowired
    private DetalleOrdenRepository detalleOrdenRepository;

    private final OkHttpClient httpClient = new OkHttpClient();
    private final Gson gson = new Gson();

    /**
     * Procesa una pregunta del usuario y genera una respuesta con recomendaciones de libros
     */
    public ChatResponse procesarPregunta(String pregunta) {
        try {
            // 1. Obtener todos los libros disponibles
            List<Libro> todosLosLibros = libroRepository.findAll();
            
            // 2. Construir el contexto con el catálogo de libros
            String contextoLibros = construirContextoLibros(todosLosLibros);
            
            // 3. Crear el prompt para Gemini
            String prompt = construirPrompt(pregunta, contextoLibros);
            
            // 4. Llamar a la API de Gemini
            String respuestaGemini = llamarGeminiAPI(prompt);
            
            // 5. Extraer IDs de libros mencionados en la respuesta
            List<Long> idsLibrosRecomendados = extraerIdsLibros(respuestaGemini);
            
            // 6. Obtener los libros recomendados de la BD
            List<ChatResponse.LibroRecomendado> librosRecomendados = obtenerLibrosRecomendados(idsLibrosRecomendados);
            
            // 7. Construir y retornar la respuesta
            return ChatResponse.builder()
                    .respuesta(limpiarRespuesta(respuestaGemini))
                    .librosRecomendados(librosRecomendados)
                    .build();
                    
        } catch (Exception e) {
            log.error("Error al procesar pregunta con Gemini: {}", e.getMessage(), e);
            return ChatResponse.builder()
                    .respuesta("Lo siento, tuve un problema al procesar tu pregunta. Por favor, intenta de nuevo.")
                    .librosRecomendados(new ArrayList<>())
                    .build();
        }
    }

    /**
     * Construye el contexto con información de los libros disponibles
     */
    private String construirContextoLibros(List<Libro> libros) {
        StringBuilder contexto = new StringBuilder();
        contexto.append("CATÁLOGO DE LIBROS DISPONIBLES:\n\n");
        
        for (Libro libro : libros) {
            contexto.append(String.format(
                "[ID:%d] \"%s\" por %s %s - Categoría: %s - Precio: $%.2f - Stock: %d\n",
                libro.getId(),
                libro.getTitulo(),
                libro.getAutor().getNombre(),
                libro.getAutor().getApellido(),
                libro.getCategoria().getNombre(),
                libro.getPrecio(),
                libro.getStock()
            ));
            
            if (libro.getDescripcion() != null && !libro.getDescripcion().isEmpty()) {
                contexto.append("  Descripción: ").append(libro.getDescripcion()).append("\n");
            }
            contexto.append("\n");
        }
        
        return contexto.toString();
    }

    /**
     * Construye el prompt completo para enviar a Gemini
     */
    private String construirPrompt(String pregunta, String contextoLibros) {
        return String.format("""
            Eres un asistente virtual experto en recomendaciones de libros para una librería online.
            
            INSTRUCCIONES IMPORTANTES:
            1. Solo puedes recomendar libros que estén en el catálogo proporcionado
            2. Cuando recomiendes un libro, SIEMPRE incluye su ID entre corchetes así: [ID:123]
            3. Sé amable, conversacional y entusiasta
            4. Si preguntan por un libro o autor que no tenemos, sugiere alternativas similares de nuestro catálogo
            5. Menciona el precio y disponibilidad cuando sea relevante
            6. Limita tus recomendaciones a máximo 3 libros por respuesta
            
            %s
            
            PREGUNTA DEL CLIENTE:
            %s
            
            RESPUESTA (recuerda incluir [ID:X] para cada libro que recomiendes):
            """, contextoLibros, pregunta);
    }

    /**
     * Llama a la API de Gemini y obtiene la respuesta
     */
    private String llamarGeminiAPI(String prompt) throws IOException {
        // Construir el JSON de la petición
        JsonObject requestBody = new JsonObject();
        JsonArray contents = new JsonArray();
        JsonObject content = new JsonObject();
        JsonArray parts = new JsonArray();
        JsonObject part = new JsonObject();
        
        part.addProperty("text", prompt);
        parts.add(part);
        content.add("parts", parts);
        contents.add(content);
        requestBody.add("contents", contents);
        
        // Configuración de generación
        JsonObject generationConfig = new JsonObject();
        generationConfig.addProperty("temperature", 0.7);
        generationConfig.addProperty("maxOutputTokens", 8192); // Aumentado para respuestas completas
        requestBody.add("generationConfig", generationConfig);
        
        // Crear la petición HTTP
        RequestBody body = RequestBody.create(
            requestBody.toString(),
            MediaType.parse("application/json")
        );
        
        String fullUrl = apiUrl + "?key=" + apiKey;
        log.info("🔍 Llamando a Gemini API: {}", apiUrl); // No mostrar la key completa
        
        Request request = new Request.Builder()
                .url(fullUrl)
                .post(body)
                .addHeader("Content-Type", "application/json")
                .build();
        
        // Ejecutar la petición
        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                String errorBody = response.body() != null ? response.body().string() : "No error body";
                log.error("❌ Error de Gemini API - Código: {} - Respuesta: {}", response.code(), errorBody);
                throw new IOException("Error en la API de Gemini: " + response.code() + " - " + errorBody);
            }
            
            String responseBody = response.body().string();
            log.debug("Respuesta de Gemini: {}", responseBody);
            
            // Parsear la respuesta
            JsonObject jsonResponse = gson.fromJson(responseBody, JsonObject.class);
            JsonArray candidates = jsonResponse.getAsJsonArray("candidates");
            
            if (candidates != null && candidates.size() > 0) {
                JsonObject candidate = candidates.get(0).getAsJsonObject();
                JsonObject contentObj = candidate.getAsJsonObject("content");
                JsonArray partsArray = contentObj.getAsJsonArray("parts");
                
                if (partsArray != null && partsArray.size() > 0) {
                    return partsArray.get(0).getAsJsonObject().get("text").getAsString();
                }
            }
            
            throw new IOException("No se pudo extraer la respuesta de Gemini");
        }
    }

    /**
     * Extrae los IDs de libros mencionados en la respuesta de Gemini
     */
    private List<Long> extraerIdsLibros(String respuesta) {
        List<Long> ids = new ArrayList<>();
        Pattern pattern = Pattern.compile("\\[ID:(\\d+)\\]");
        Matcher matcher = pattern.matcher(respuesta);
        
        while (matcher.find()) {
            try {
                ids.add(Long.parseLong(matcher.group(1)));
            } catch (NumberFormatException e) {
                log.warn("No se pudo parsear el ID: {}", matcher.group(1));
            }
        }
        
        return ids;
    }

    /**
     * Obtiene los libros recomendados de la base de datos
     */
    private List<ChatResponse.LibroRecomendado> obtenerLibrosRecomendados(List<Long> ids) {
        if (ids.isEmpty()) {
            return new ArrayList<>();
        }
        
        List<Libro> libros = libroRepository.findAllById(ids);
        
        return libros.stream()
                .map(libro -> ChatResponse.LibroRecomendado.builder()
                        .id(libro.getId())
                        .titulo(libro.getTitulo())
                        .autor(libro.getAutor().getNombre() + " " + libro.getAutor().getApellido())
                        .portadaUrl(libro.getPortadaUrl())
                        .precio(libro.getPrecio())
                        .categoria(libro.getCategoria().getNombre())
                        .build())
                .collect(Collectors.toList());
    }

    /**
     * Limpia la respuesta eliminando los marcadores de ID
     */
    private String limpiarRespuesta(String respuesta) {
        return respuesta.replaceAll("\\[ID:\\d+\\]", "").trim();
    }

    /**
     * Obtiene recomendaciones personalizadas basadas en el historial del usuario
     * Usado para notificaciones y sugerencias en tiempo real
     */
    public ChatResponse obtenerRecomendacionesPersonalizadas(Long usuarioId) {
        try {
            // 1. Obtener libros comprados por el usuario
            List<Libro> librosComprados = detalleOrdenRepository.findLibrosCompradosPorUsuario(usuarioId);
            
            // 2. Obtener libros favoritos del usuario
            List<Libro> librosFavoritos = favoritoRepository.findLibrosFavoritosPorUsuario(usuarioId);
            
            // 3. Si no tiene historial, retornar recomendaciones generales
            if (librosComprados.isEmpty() && librosFavoritos.isEmpty()) {
                return obtenerRecomendacionesGenerales();
            }
            
            // 4. Obtener todos los libros disponibles
            List<Libro> todosLosLibros = libroRepository.findAll();
            
            // 5. Construir contexto personalizado
            String contextoPersonalizado = construirContextoPersonalizado(
                librosComprados, 
                librosFavoritos, 
                todosLosLibros
            );
            
            // 6. Crear prompt personalizado para Gemini
            String prompt = construirPromptRecomendacionesPersonalizadas(contextoPersonalizado);
            
            // 7. Llamar a Gemini
            String respuestaGemini = llamarGeminiAPI(prompt);
            
            // 8. Extraer IDs de libros
            List<Long> idsLibrosRecomendados = extraerIdsLibros(respuestaGemini);
            
            // 9. Obtener libros recomendados
            List<ChatResponse.LibroRecomendado> librosRecomendados = obtenerLibrosRecomendados(idsLibrosRecomendados);
            
            // 10. Retornar respuesta
            return ChatResponse.builder()
                    .respuesta(limpiarRespuesta(respuestaGemini))
                    .librosRecomendados(librosRecomendados)
                    .build();
                    
        } catch (Exception e) {
            log.error("Error al obtener recomendaciones personalizadas: {}", e.getMessage(), e);
            return obtenerRecomendacionesGenerales();
        }
    }

    /**
     * Obtiene recomendaciones generales cuando el usuario no tiene historial
     */
    private ChatResponse obtenerRecomendacionesGenerales() {
        try {
            List<Libro> todosLosLibros = libroRepository.findAll();
            String contextoLibros = construirContextoLibros(todosLosLibros);
            String prompt = """
                Eres un asistente virtual experto en recomendaciones de libros.
                
                INSTRUCCIONES:
                1. Solo puedes recomendar libros que estén en el catálogo
                2. Cuando recomiendes un libro, SIEMPRE incluye su ID así: [ID:123]
                3. Recomienda 3 libros populares y variados
                4. Sé amable y entusiasta
                
                %s
                
                Recomiéndame 3 libros interesantes para empezar a explorar nuestra librería.
                RESPUESTA (recuerda incluir [ID:X] para cada libro):
                """.formatted(contextoLibros);
            
            String respuestaGemini = llamarGeminiAPI(prompt);
            List<Long> idsLibrosRecomendados = extraerIdsLibros(respuestaGemini);
            List<ChatResponse.LibroRecomendado> librosRecomendados = obtenerLibrosRecomendados(idsLibrosRecomendados);
            
            return ChatResponse.builder()
                    .respuesta(limpiarRespuesta(respuestaGemini))
                    .librosRecomendados(librosRecomendados)
                    .build();
        } catch (Exception e) {
            log.error("Error al obtener recomendaciones generales: {}", e.getMessage(), e);
            return ChatResponse.builder()
                    .respuesta("Bienvenido a nuestra librería. Explora nuestro catálogo de libros.")
                    .librosRecomendados(new ArrayList<>())
                    .build();
        }
    }

    /**
     * Construye el contexto personalizado basado en el historial del usuario
     */
    private String construirContextoPersonalizado(
            List<Libro> librosComprados,
            List<Libro> librosFavoritos,
            List<Libro> todosLosLibros) {
        
        StringBuilder contexto = new StringBuilder();
        
        // Información del usuario
        contexto.append("=== PERFIL DEL USUARIO ===\n");
        contexto.append("Libros comprados: ").append(librosComprados.size()).append("\n");
        contexto.append("Libros en favoritos: ").append(librosFavoritos.size()).append("\n\n");
        
        // Libros comprados
        if (!librosComprados.isEmpty()) {
            contexto.append("LIBROS QUE HA COMPRADO:\n");
            librosComprados.stream().limit(5).forEach(libro ->
                contexto.append(String.format("- \"%s\" por %s %s (Categoría: %s)\n",
                    libro.getTitulo(),
                    libro.getAutor().getNombre(),
                    libro.getAutor().getApellido(),
                    libro.getCategoria().getNombre()))
            );
            contexto.append("\n");
        }
        
        // Libros favoritos
        if (!librosFavoritos.isEmpty()) {
            contexto.append("LIBROS EN FAVORITOS:\n");
            librosFavoritos.stream().limit(5).forEach(libro ->
                contexto.append(String.format("- \"%s\" por %s %s (Categoría: %s)\n",
                    libro.getTitulo(),
                    libro.getAutor().getNombre(),
                    libro.getAutor().getApellido(),
                    libro.getCategoria().getNombre()))
            );
            contexto.append("\n");
        }
        
        // Catálogo disponible
        contexto.append("=== CATÁLOGO DISPONIBLE ===\n");
        contexto.append(construirContextoLibros(todosLosLibros));
        
        return contexto.toString();
    }

    /**
     * Construye el prompt para obtener recomendaciones personalizadas
     */
    private String construirPromptRecomendacionesPersonalizadas(String contextoPersonalizado) {
        return String.format("""
            Eres un asistente virtual experto en recomendaciones de libros para una librería online.
            
            INSTRUCCIONES IMPORTANTES:
            1. Analiza el historial de compras y favoritos del usuario
            2. Recomienda libros que NO estén en su historial de compras
            3. SIEMPRE incluye el ID del libro así: [ID:123]
            4. Recomienda máximo 3 libros
            5. Explica por qué cada libro es perfecto para este usuario
            6. Sé amable, conversacional y entusiasta
            7. Menciona el precio cuando sea relevante
            
            %s
            
            Basándome en el perfil del usuario anterior, recomiéndame 3 libros que creas que le encantarán.
            RESPUESTA (recuerda incluir [ID:X] para cada libro):
            """, contextoPersonalizado);
    }
}
