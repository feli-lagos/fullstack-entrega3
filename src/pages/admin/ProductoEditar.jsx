import { useEffect, useState } from "react";
import { Button } from "react-bootstrap";
import FormularioProducto from "../../components/FormularioProducto";
import EstadoCarga from "../../components/EstadoCarga";
import { obtenerProducto } from "../../api";

function ProductoEditar({ id, irA }) {
    const [producto, setProducto] = useState(null);
    const [error, setError] = useState("");

    useEffect(() => {
        obtenerProducto(id).then(setProducto).catch(e => setError(e.message));
    }, [id]);

    return (
        <>
            <div className="d-flex justify-content-between align-items-center flex-wrap gap-2 border-bottom border-success pb-2 mb-4">
                <h1 className="h3 m-0 text-success">Editar producto</h1>
                <div className="d-flex flex-wrap gap-2">
                    <Button href="#" variant="outline-success" onClick={() => irA("admin-producto-ver", id)}>Ver ficha</Button>
                    <Button href="#" variant="outline-success" onClick={() => irA("admin-productos")}>Volver al listado</Button>
                </div>
            </div>

            <main>
                {producto && !error
                    ? <FormularioProducto producto={producto} esNuevo={false} irA={irA} />
                    : <EstadoCarga error={error} />}
            </main>
        </>
    );
}

export default ProductoEditar;
