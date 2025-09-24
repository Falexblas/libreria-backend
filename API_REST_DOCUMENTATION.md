# API REST - Librería Backend

## Descripción
Esta API REST está diseñada para trabajar con un frontend Vue.js. Proporciona endpoints para gestionar una librería online con funcionalidades de autenticación, gestión de libros, carrito de compras, órdenes, favoritos y administración.

## Base URL
```
http://localhost:8080/api
```

## Autenticación
La API utiliza Spring Security para la autenticación. Los endpoints protegidos requieren autenticación previa.

---

## Endpoints de Autenticación

### POST /api/auth/registro
Registra un nuevo usuario.

**Request Body:**
```json
{
  "nombre": "Juan",
  "apellido": "Pérez",
  "email": "juan@example.com",
  "password": "password123",
  "telefono": "123456789",
  "direccion": "Calle 123"
}
```

**Response (201):**
```json
{
  "mensaje": "Usuario registrado exitosamente",
  "usuario": {
    "id": 1,
    "nombre": "Juan",
    "apellido": "Pérez",
    "email": "juan@example.com",
    "telefono": "123456789",
    "direccion": "Calle 123"
  }
}
```

### GET /api/auth/perfil
Obtiene el perfil del usuario autenticado.

**Response (200):**
```json
{
  "id": 1,
  "nombre": "Juan",
  "apellido": "Pérez",
  "email": "juan@example.com",
  "telefono": "123456789",
  "direccion": "Calle 123"
}
```

### PUT /api/auth/perfil
Actualiza el perfil del usuario autenticado.

**Request Body:**
```json
{
  "nombre": "Juan Carlos",
  "apellido": "Pérez",
  "telefono": "987654321",
  "direccion": "Nueva Calle 456",
  "password": "newpassword123" // Opcional
}
```

---

## Endpoints de Libros

### GET /api/libros
Obtiene todos los libros.

**Response (200):**
```json
[
  {
    "id": 1,
    "titulo": "El Quijote",
    "autor": {...},
    "categoria": {...},
    "editorial": {...},
    "precio": 25.99,
    "stock": 10,
    "descripcion": "Descripción del libro"
  }
]
```

### GET /api/libros/{id}
Obtiene un libro por ID.

### POST /api/libros (ADMIN)
Crea un nuevo libro.

### PUT /api/libros/{id} (ADMIN)
Actualiza un libro existente.

### DELETE /api/libros/{id} (ADMIN)
Elimina un libro.

---

## Endpoints de Carrito

### GET /api/carrito
Obtiene el carrito del usuario autenticado.

### POST /api/carrito/agregar
Agrega un libro al carrito.

**Request Parameters:**
- `libroId`: ID del libro
- `cantidad`: Cantidad (opcional, default: 1)

### DELETE /api/carrito/eliminar
Elimina un libro del carrito.

**Request Parameters:**
- `libroId`: ID del libro

---

## Endpoints de Órdenes

### GET /api/ordenes
Obtiene las órdenes del usuario autenticado.

### POST /api/ordenes
Crea una nueva orden.

**Request Body:**
```json
{
  "metodoPago": "tarjeta",
  "direccionEnvio": "Calle 123",
  "ciudadEnvio": "Ciudad",
  "codigoPostalEnvio": "12345",
  "telefonoContacto": "123456789"
}
```

---

## Endpoints de Favoritos

### GET /api/favoritos
Obtiene los favoritos del usuario autenticado.

### POST /api/favoritos/agregar/{libroId}
Agrega un libro a favoritos.

### DELETE /api/favoritos/eliminar/{favoritoId}
Elimina un libro de favoritos.

---

## Endpoints de Reseñas

### POST /api/reseñas/libro/{libroId}
Crea una reseña para un libro.

**Request Body:**
```json
{
  "calificacion": 5,
  "comentario": "Excelente libro"
}
```

---

## Endpoints de Categorías

### GET /api/categorias
Obtiene todas las categorías.

### GET /api/categorias/{id}
Obtiene una categoría por ID.

### POST /api/categorias (ADMIN)
Crea una nueva categoría.

### PUT /api/categorias/{id} (ADMIN)
Actualiza una categoría.

### DELETE /api/categorias/{id} (ADMIN)
Elimina una categoría.

---

## Endpoints de Autores

### GET /api/autores
Obtiene todos los autores.

### GET /api/autores/{id}
Obtiene un autor por ID.

### POST /api/autores (ADMIN)
Crea un nuevo autor.

### PUT /api/autores/{id} (ADMIN)
Actualiza un autor.

### DELETE /api/autores/{id} (ADMIN)
Elimina un autor.

---

## Endpoints de Editoriales

### GET /api/editoriales
Obtiene todas las editoriales.

### GET /api/editoriales/{id}
Obtiene una editorial por ID.

### POST /api/editoriales (ADMIN)
Crea una nueva editorial.

### PUT /api/editoriales/{id} (ADMIN)
Actualiza una editorial.

### DELETE /api/editoriales/{id} (ADMIN)
Elimina una editorial.

---

## Endpoints de Administración

### GET /api/admin/dashboard
Obtiene estadísticas del dashboard administrativo.

### GET /api/admin/usuarios
Obtiene todos los usuarios.

### GET /api/admin/usuarios/{id}
Obtiene un usuario por ID.

### PUT /api/admin/usuarios/{id}
Actualiza un usuario.

### DELETE /api/admin/usuarios/{id}
Elimina un usuario.

### GET /api/admin/ordenes
Obtiene todas las órdenes.

### GET /api/admin/ordenes/{id}
Obtiene una orden por ID.

### PUT /api/admin/ordenes/{id}/estado
Actualiza el estado de una orden.

**Request Body:**
```json
{
  "estado": "enviado"
}
```

---

## Códigos de Estado HTTP

- **200 OK**: Solicitud exitosa
- **201 Created**: Recurso creado exitosamente
- **400 Bad Request**: Error en la solicitud
- **401 Unauthorized**: No autenticado
- **403 Forbidden**: Sin permisos
- **404 Not Found**: Recurso no encontrado
- **500 Internal Server Error**: Error del servidor

---

## Configuración CORS

La API está configurada para aceptar requests desde:
- `http://localhost:*`
- `http://127.0.0.1:*`
- `https://localhost:*`
- `https://127.0.0.1:*`

---

## Notas para el Frontend Vue.js

1. **Autenticación**: Utiliza Spring Security con sesiones. Asegúrate de incluir las cookies en las requests.

2. **CORS**: Ya está configurado para desarrollo local.

3. **Manejo de Errores**: Todos los errores devuelven un formato JSON consistente con timestamp, status, error y message.

4. **Validación**: Los errores de validación incluyen detalles específicos por campo.

5. **Seguridad**: Los endpoints administrativos requieren rol ADMIN.

## Ejemplo de uso con Axios (Vue.js)

```javascript
// Configuración base de Axios
const api = axios.create({
  baseURL: 'http://localhost:8080/api',
  withCredentials: true // Importante para las cookies de sesión
});

// Ejemplo: Obtener libros
const libros = await api.get('/libros');

// Ejemplo: Agregar al carrito
await api.post('/carrito/agregar', null, {
  params: { libroId: 1, cantidad: 2 }
});

// Ejemplo: Crear orden
const orden = await api.post('/ordenes', {
  metodoPago: 'tarjeta',
  direccionEnvio: 'Mi dirección',
  ciudadEnvio: 'Mi ciudad',
  codigoPostalEnvio: '12345',
  telefonoContacto: '123456789'
});
```
