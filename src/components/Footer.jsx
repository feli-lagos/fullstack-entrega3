import { useEffect, useState } from "react";
import { Container, Row, Col, Form, Button, InputGroup } from "react-bootstrap";
import { listarCategorias, suscribirse } from "../api";

function Footer({ irA }) {
    const [categorias, setCategorias] = useState([]);
    const [correo, setCorreo] = useState("");
    const [resultado, setResultado] = useState(null);
    const [enviando, setEnviando] = useState(false);

    // El correo se guarda en la tabla "suscripciones" de la base de datos
    async function suscribir(e) {
        e.preventDefault();
        setEnviando(true);
        setResultado(null);
        try {
            await suscribirse(correo.trim());
            setResultado({ ok: true, texto: "¡Listo! Te enviaremos las ofertas de la semana." });
            setCorreo("");
        } catch (err) {
            setResultado({ ok: false, texto: err.errores?.correo ?? err.message });
        } finally {
            setEnviando(false);
        }
    }

    // Si la API no responde, el pie de página simplemente queda sin categorías
    useEffect(() => {
        listarCategorias().then(setCategorias).catch(() => setCategorias([]));
    }, []);

    return (
        <footer className="bg-body-tertiary border-top mt-5 py-4 small">
            <Container>
                <Row className="g-4">
                    <Col md={4}>
                        <p className="fw-bold mb-2">Huerto Verde</p>
                        <p>
                            Av. Siempre Fresca 1234, Santiago de Chile<br />
                            +56 2 2345 6789 · contacto@huertoverde.cl
                        </p>
                        <div className="d-flex gap-2">
                            <img src="/img/pagos/pago-visa.svg" alt="VISA" width="72" height="36" className="bg-white border rounded" />
                            <img src="/img/pagos/pago-mastercard.svg" alt="MASTERCARD" width="72" height="36" className="bg-white border rounded" />
                            <img src="/img/pagos/pago-redcompra.svg" alt="REDCOMPRA" width="72" height="36" className="bg-white border rounded" />
                        </div>
                    </Col>

                    <Col md={4}>
                        <h2 className="h6">Categorías</h2>
                        <ul className="list-unstyled">
                            {categorias.map(c => (
                                <li key={c.id}><a href="#" onClick={() => irA("productos", c.codigo)} className="link-success">{c.nombre}</a></li>
                            ))}
                        </ul>
                    </Col>

                    <Col md={4}>
                        <h2 className="h6">Mantente en contacto</h2>
                        <p>Suscríbete y recibe las ofertas de la semana.</p>
                        <Form onSubmit={suscribir}>
                            <InputGroup size="sm">
                                <Form.Control
                                    type="email"
                                    placeholder="Ingresa tu correo"
                                    aria-label="Correo para suscribirse"
                                    maxLength={100}
                                    required
                                    value={correo}
                                    onChange={e => setCorreo(e.target.value)}
                                />
                                <Button type="submit" variant="outline-success" disabled={enviando}>
                                    {enviando ? "Enviando..." : "Suscribirse"}
                                </Button>
                            </InputGroup>
                        </Form>
                        {resultado && (
                            <p className={`mt-2 mb-0 ${resultado.ok ? "text-success" : "text-danger"}`}>{resultado.texto}</p>
                        )}
                        <p className="mt-3 mb-0 text-muted">&copy; 2026 Huerto Verde · Entrega 1 DSY1104</p>
                    </Col>
                </Row>
            </Container>
        </footer>
    );
}

export default Footer;
