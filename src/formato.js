export function formatoPesos(numero) {
    return `$${Number(numero).toLocaleString("es-CL")}`;
}

// La API entrega fechas ISO (2026-09-04 o 2026-09-04T08:12:00)
export function formatoFecha(iso) {
    if (!iso) return "—";
    return new Date(iso.slice(0, 10) + "T00:00:00").toLocaleDateString("es-CL", { dateStyle: "long" });
}

export function formatoFechaHora(iso) {
    if (!iso) return "—";
    return new Date(iso).toLocaleString("es-CL", { dateStyle: "long", timeStyle: "short" });
}

export function enStockCritico(producto) {
    return producto.stockCritico !== null && producto.stock <= producto.stockCritico;
}

export const tiposUsuario = {
    administrador: "Administrador",
    vendedor: "Vendedor",
    cliente: "Cliente"
};

// Corta una lista en páginas. Si la página pedida ya no existe (por ejemplo, después de
// filtrar o eliminar), se queda en la última que sí existe.
export const FILAS_POR_PAGINA = 10;

export function paginar(lista, pagina) {
    const totalPaginas = Math.max(1, Math.ceil(lista.length / FILAS_POR_PAGINA));
    const paginaActual = Math.min(pagina, totalPaginas);
    const inicio = (paginaActual - 1) * FILAS_POR_PAGINA;
    return { visibles: lista.slice(inicio, inicio + FILAS_POR_PAGINA), paginaActual, totalPaginas };
}

// Para los buscadores: compara sin importar mayúsculas
export function contieneTexto(valor, texto) {
    return (valor ?? "").toString().toLowerCase().includes(texto);
}
