import { useEffect, useState } from "react";
import { Button } from "react-bootstrap";
import FormularioUsuario from "../../components/FormularioUsuario";
import EstadoCarga from "../../components/EstadoCarga";
import { obtenerUsuario } from "../../api";

function UsuarioEditar({ id, irA }) {
    const [usuario, setUsuario] = useState(null);
    const [error, setError] = useState("");

    useEffect(() => {
        obtenerUsuario(id).then(setUsuario).catch(e => setError(e.message));
    }, [id]);

    return (
        <>
            <div className="d-flex justify-content-between align-items-center flex-wrap gap-2 border-bottom border-success pb-2 mb-4">
                <h1 className="h3 m-0 text-success">Editar usuario</h1>
                <div className="d-flex flex-wrap gap-2">
                    <Button href="#" variant="outline-success" onClick={() => irA("admin-usuario-ver", id)}>Ver ficha</Button>
                    <Button href="#" variant="outline-success" onClick={() => irA("admin-usuarios")}>Volver al listado</Button>
                </div>
            </div>

            <main>
                {usuario && !error
                    ? <FormularioUsuario usuario={usuario} esNuevo={false} irA={irA} />
                    : <EstadoCarga error={error} />}
            </main>
        </>
    );
}

export default UsuarioEditar;
