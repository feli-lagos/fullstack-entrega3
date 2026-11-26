const API_URL = import.meta.env.VITE_API_URL ?? "http://localhost:8080";

export class ApiError extends Error {
    constructor(mensaje, status, errores = null) {
        super(mensaje);
        this.name = "ApiError";
        this.status = status;
        this.errores = errores;
    }
}

async function pedir(ruta, opciones = {}) {
    let respuesta;
    try {
        respuesta = await fetch(`${API_URL}${ruta}`, opciones);
    } catch {
        throw new ApiError("No se pudo conectar con la API. Revisa que el backend esté corriendo.", 0);
    }

    if (respuesta.status === 204) return null;

    const datos = await respuesta.json().catch(() => null);
    if (!respuesta.ok) {
        throw new ApiError(datos?.mensaje ?? `Error ${respuesta.status}`, respuesta.status, datos?.errores ?? null);
    }
    return datos;
}

const enviarJson = (metodo, ruta, cuerpo) =>
    pedir(ruta, { method: metodo, headers: { "Content-Type": "application/json" }, body: JSON.stringify(cuerpo) });

// Catálogos
export const listarCategorias = () => pedir("/api/categorias");
export const listarRegiones = () => pedir("/api/regiones");
export const listarComunas = (regionId) => pedir(`/api/regiones/${regionId}/comunas`);

// Productos
export const listarProductos = (soloPublicados = false) =>
    pedir(soloPublicados ? "/api/productos?publicado=true" : "/api/productos");
export const obtenerProducto = (id) => pedir(`/api/productos/${id}`);
export const crearProducto = (datos) => enviarJson("POST", "/api/productos", datos);
export const actualizarProducto = (id, datos) => enviarJson("PUT", `/api/productos/${id}`, datos);
export const eliminarProducto = (id) => pedir(`/api/productos/${id}`, { method: "DELETE" });

// Imagen del producto: el archivo se guarda en la base de datos del backend.
// La API entrega solo la ruta (/api/productos/5/imagen?v=...); aquí se le agrega la dirección del servidor.
// Un producto sin imagen muestra un recuadro gris dibujado aquí mismo (no depende de ningún archivo).
const SIN_IMAGEN = "data:image/svg+xml," + encodeURIComponent(
    '<svg xmlns="http://www.w3.org/2000/svg" width="400" height="300">' +
    '<rect width="100%" height="100%" fill="#e9ecef"/>' +
    '<text x="50%" y="50%" fill="#6c757d" font-family="sans-serif" font-size="24" text-anchor="middle" dominant-baseline="middle">Sin imagen</text>' +
    '</svg>'
);

export const urlImagen = (producto) => (producto.imagen ? `${API_URL}${producto.imagen}` : SIN_IMAGEN);

export const subirImagenProducto = (id, archivo) => {
    const formulario = new FormData();
    formulario.append("archivo", archivo);
    // Sin "Content-Type": con FormData el navegador arma solo el multipart/form-data
    return pedir(`/api/productos/${id}/imagen`, { method: "POST", body: formulario });
};

export const eliminarImagenProducto = (id) => pedir(`/api/productos/${id}/imagen`, { method: "DELETE" });

// Usuarios
export const listarUsuarios = () => pedir("/api/usuarios");
export const obtenerUsuario = (id) => pedir(`/api/usuarios/${id}`);
export const crearUsuario = (datos) => enviarJson("POST", "/api/usuarios", datos);
export const actualizarUsuario = (id, datos) => enviarJson("PUT", `/api/usuarios/${id}`, datos);
export const eliminarUsuario = (id) => pedir(`/api/usuarios/${id}`, { method: "DELETE" });

// Sesión y registro
export const iniciarSesion = (correo, contrasena) => enviarJson("POST", "/api/auth/login", { correo, contrasena });
export const registrarUsuario = (datos) => enviarJson("POST", "/api/auth/registro", datos);

// Contacto
export const enviarMensaje = (datos) => enviarJson("POST", "/api/mensajes", datos);

// Suscripción a las ofertas (formulario del footer)
export const suscribirse = (correo) => enviarJson("POST", "/api/suscripciones", { correo });

// Pedidos (el carrito se convierte en pedido al pagar)
export const listarPedidos = (usuarioId) => pedir(usuarioId ? `/api/pedidos?usuarioId=${usuarioId}` : "/api/pedidos");
export const crearPedido = (datos) => enviarJson("POST", "/api/pedidos", datos);
