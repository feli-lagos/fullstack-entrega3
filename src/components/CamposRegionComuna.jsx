import { useEffect, useState } from "react";
import { Row, Col, Form } from "react-bootstrap";
import { listarRegiones, listarComunas } from "../api";

// Selects de región y comuna cargados desde la base de datos.
// Los valores son los id de la región y de la comuna (como texto, igual que en un <select>).
function CamposRegionComuna({ regionId, comunaId, onCambiarRegion, onCambiarComuna, errorRegion = "", errorComuna = "", mostrarErrores = false }) {
    const [regiones, setRegiones] = useState([]);
    const [comunasPorRegion, setComunasPorRegion] = useState({});
    const [error, setError] = useState("");

    useEffect(() => {
        listarRegiones().then(setRegiones).catch(e => setError(e.message));
    }, []);

    useEffect(() => {
        if (!regionId || comunasPorRegion[regionId]) return;
        listarComunas(regionId)
            .then(comunas => setComunasPorRegion(actual => ({ ...actual, [regionId]: comunas })))
            .catch(e => setError(e.message));
    }, [regionId, comunasPorRegion]);

    const comunas = comunasPorRegion[regionId] ?? [];

    return (
        <Row>
            <Col md={6}>
                <Form.Group className="mb-3" controlId="region">
                    <Form.Label>Región</Form.Label>
                    <Form.Select
                        value={regionId}
                        onChange={e => {
                            onCambiarRegion(e.target.value);
                            onCambiarComuna("");
                        }}
                        isInvalid={(mostrarErrores && errorRegion !== "") || error !== ""}
                    >
                        <option value="">-- Seleccione la región --</option>
                        {regiones.map(region => (
                            <option key={region.id} value={region.id}>{region.nombre}</option>
                        ))}
                    </Form.Select>
                    <Form.Control.Feedback type="invalid">{error || errorRegion}</Form.Control.Feedback>
                </Form.Group>
            </Col>
            <Col md={6}>
                <Form.Group className="mb-3" controlId="comuna">
                    <Form.Label>Comuna</Form.Label>
                    <Form.Select value={comunaId} onChange={e => onCambiarComuna(e.target.value)} isInvalid={mostrarErrores && errorComuna !== ""}>
                        <option value="">-- Seleccione la comuna --</option>
                        {comunas.map(comuna => (
                            <option key={comuna.id} value={comuna.id}>{comuna.nombre}</option>
                        ))}
                    </Form.Select>
                    <Form.Control.Feedback type="invalid">{errorComuna}</Form.Control.Feedback>
                </Form.Group>
            </Col>
        </Row>
    );
}

export default CamposRegionComuna;
