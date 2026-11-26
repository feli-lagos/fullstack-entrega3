import { Alert } from "react-bootstrap";

// Muestra el mensaje de error de la API y, si vienen, los errores por campo
function ErroresApi({ error }) {
    if (!error) return null;

    return (
        <Alert variant="danger">
            {error.message}
            {error.errores && (
                <ul className="mb-0 mt-2">
                    {Object.entries(error.errores).map(([campo, texto]) => (
                        <li key={campo}>{texto}</li>
                    ))}
                </ul>
            )}
        </Alert>
    );
}

export default ErroresApi;
