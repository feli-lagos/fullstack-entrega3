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
