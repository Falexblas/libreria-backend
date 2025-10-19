# 🤖 Configuración del ChatBot con Google Gemini

## 📋 Resumen
Se ha implementado un chatbot inteligente que utiliza Google Gemini AI para recomendar libros basándose en el catálogo disponible en tu base de datos.

## 🔑 Paso 1: Obtener API Key de Google Gemini

### Opción A: Google AI Studio (Recomendado - Más Fácil)

1. **Ir a Google AI Studio**
   - Visita: https://makersuite.google.com/app/apikey
   - O busca "Google AI Studio" en Google

2. **Iniciar sesión**
   - Usa tu cuenta de Google (Gmail)

3. **Crear API Key**
   - Haz clic en "Get API Key" o "Create API Key"
   - Selecciona "Create API key in new project" o usa un proyecto existente
   - Copia la API key generada (empieza con `AIza...`)

4. **Importante**
   - ⚠️ Guarda la API key en un lugar seguro
   - ⚠️ No la compartas públicamente
   - ⚠️ No la subas a GitHub

### Opción B: Google Cloud Console (Más Complejo)

1. Ir a: https://console.cloud.google.com/
2. Crear un nuevo proyecto o seleccionar uno existente
3. Habilitar la API de Generative Language
4. Crear credenciales (API Key)

## ⚙️ Paso 2: Configurar el Backend

1. **Abrir el archivo de configuración**
   ```
   src/main/resources/application.properties
   ```

2. **Reemplazar la API key**
   ```properties
   # Reemplaza YOUR_GEMINI_API_KEY_HERE con tu API key real
   gemini.api.key=AIzaSy...tu_api_key_aqui
   ```

3. **Guardar el archivo**

## 🚀 Paso 3: Ejecutar la Aplicación

### Backend (Spring Boot)

1. **Recargar dependencias de Maven**
   ```bash
   mvn clean install
   ```

2. **Ejecutar la aplicación**
   ```bash
   mvn spring-boot:run
   ```
   O ejecutar desde tu IDE (IntelliJ IDEA, Eclipse, etc.)

3. **Verificar que está funcionando**
   - El backend debería iniciar en: http://localhost:8080
   - Probar endpoint: http://localhost:8080/api/chat/health
   - Deberías ver: "Chat service is running"

### Frontend (Vue.js)

1. **Instalar dependencias (si no lo has hecho)**
   ```bash
   npm install
   ```

2. **Ejecutar el servidor de desarrollo**
   ```bash
   npm run dev
   ```

3. **Abrir en el navegador**
   - Normalmente: http://localhost:5173

## 🎯 Paso 4: Probar el ChatBot

1. **Abrir tu aplicación en el navegador**

2. **Buscar el botón flotante del chat**
   - Aparecerá en la esquina inferior derecha
   - Es un botón circular morado con un ícono de chat

3. **Hacer clic para abrir el chat**

4. **Probar con preguntas como:**
   - "¿Qué libro de terror me recomiendas?"
   - "Busco un libro de ciencia ficción"
   - "¿Tienes libros de [nombre de autor]?"
   - "Recomiéndame un bestseller"
   - "Quiero un libro de romance"

## 🎨 Características del ChatBot

### ✅ Lo que puede hacer:
- Recomendar libros basándose en categorías
- Buscar libros por autor
- Sugerir libros según el género
- Mostrar información de precio y disponibilidad
- Mostrar tarjetas visuales de los libros recomendados
- Permitir hacer clic en los libros para ver detalles

### ⚠️ Limitaciones:
- Solo recomienda libros que estén en tu base de datos
- Requiere conexión a internet para funcionar
- Tiene un límite de uso gratuito (ver abajo)

## 💰 Límites del Tier Gratuito de Gemini

Google Gemini ofrece un tier gratuito generoso:

- **60 solicitudes por minuto**
- **1,500 solicitudes por día**
- **1 millón de tokens por mes**

Para un proyecto académico, esto es más que suficiente.

## 🔧 Solución de Problemas

### Error: "API key not valid"
- Verifica que copiaste la API key completa
- Asegúrate de que no haya espacios al inicio o final
- Verifica que la API key esté activa en Google AI Studio

### Error: "Connection refused"
- Verifica que el backend esté ejecutándose
- Confirma que el puerto 8080 esté disponible
- Revisa los logs del backend para más detalles

### El chat no aparece
- Verifica que el frontend esté ejecutándose
- Abre la consola del navegador (F12) para ver errores
- Asegúrate de que el componente ChatBot esté importado en App.vue

### El bot no responde
- Verifica que tengas libros en tu base de datos
- Revisa los logs del backend
- Confirma que la API key esté configurada correctamente

## 📚 Estructura de Archivos Creados

### Backend:
```
src/main/java/com/libreria/
├── controller/
│   └── ChatController.java          # Endpoint del chat
├── service/
│   └── GeminiService.java           # Lógica de IA
└── dto/
    ├── ChatRequest.java             # Request del chat
    └── ChatResponse.java            # Response del chat
```

### Frontend:
```
src/
├── components/
│   └── ChatBot.vue                  # Componente visual del chat
└── stores/
    └── chat.js                      # Estado del chat (Pinia)
```

## 🎓 Para tu Proyecto Académico

### Puntos a destacar:
1. **Integración con IA**: Uso de Google Gemini para procesamiento de lenguaje natural
2. **Arquitectura REST**: Comunicación entre frontend y backend
3. **Estado reactivo**: Uso de Pinia para gestión de estado
4. **UX moderna**: Interfaz de chat flotante y responsive
5. **Recomendaciones inteligentes**: Basadas en el catálogo real de la BD

### Posibles mejoras futuras:
- Historial de conversaciones persistente
- Análisis de sentimientos del usuario
- Recomendaciones basadas en compras previas
- Soporte multiidioma
- Integración con sistema de favoritos

## 📞 Soporte

Si tienes problemas:
1. Revisa los logs del backend y frontend
2. Verifica que todas las dependencias estén instaladas
3. Confirma que la API key esté configurada correctamente
4. Asegúrate de tener libros en tu base de datos

---

¡Listo! Tu chatbot con IA está configurado y funcionando. 🎉
