import { useEffect, useState } from "react";
import { listarProductos } from "../api";

// Enlace a un producto desde un texto (por ejemplo, un blog). El texto nombra el producto por su
// código (FR-001), y aquí se busca en la API cuál es su id real en la base de datos.
// Si el producto ya no existe o no está publicado, queda como texto normal (sin link roto).
function EnlaceProducto({ codigo, irA, children }) {
    const [producto, setProducto] = useState(null);

    useEffect(() => {
        listarProductos(true)
            .then(lista => setProducto(lista.find(p => p.codigo === codigo) ?? null))
            .catch(() => setProducto(null));
    }, [codigo]);

    if (!producto) return children;

    return (
        <a href="#" onClick={() => irA("detalle", producto.id)} className="link-success">{children}</a>
    );
}

export default EnlaceProducto;
