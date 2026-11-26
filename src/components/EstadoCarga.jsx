import { Alert, Spinner } from "react-bootstrap";

// Se muestra mientras llegan los datos de la API, o si la API respondió con error
function EstadoCarga({ error }) {
    if (error) return <Alert variant="danger">{error}</Alert>;

    return (
        <div className="d-flex align-items-center gap-2 text-muted my-4">
            <Spinner animation="border" size="sm" variant="success" />
            Cargando...
        </div>
    );
}

export default EstadoCarga;
