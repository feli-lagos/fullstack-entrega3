import { useEffect, useState } from "react";
import { Row, Col, Card, ListGroup, Form } from "react-bootstrap";
import TarjetaProducto from "../components/TarjetaProducto";
import EstadoCarga from "../components/EstadoCarga";
import { listarCategorias, listarProductos } from "../api";

function Productos({ categoria, irA, agregarAlCarrito }) {
    const [orden, setOrden] = useState("relevancia");
    const [productos, setProductos] = useState(null);
    const [categorias, setCategorias] = useState([]);
    const [error, setError] = useState("");

    useEffect(() => {
        listarProductos(true).then(setProductos).catch(e => setError(e.message));
        listarCategorias().then(setCategorias).catch(e => setError(e.message));
    }, []);

    const todos = productos ?? [];
    const listaProductos = categoria ? todos.filter(p => p.categoria.codigo === categoria) : [...todos];

    if (orden === "precio-asc") listaProductos.sort((a, b) => a.precio - b.precio);
    if (orden === "precio-desc") listaProductos.sort((a, b) => b.precio - a.precio);
    if (orden === "nombre") listaProductos.sort((a, b) => a.nombre.localeCompare(b.nombre, "es"));

    return (
        <Row className="g-4">
            <Col as="aside" md={3}>
                <Card className="mb-3">
                    <Card.Header as="h2" className="fs-6 text-success">Categorías</Card.Header>
                    <ListGroup variant="flush">
                        <ListGroup.Item action onClick={() => irA("productos")}>Todas</ListGroup.Item>
                        {categorias.map(c => (
                            <ListGroup.Item key={c.id} action onClick={() => irA("productos", c.codigo)}>{c.nombre}</ListGroup.Item>
                        ))}
                    </ListGroup>
                </Card>

                <Card body>
                    <h2 className="fs-6 text-success">Ordenar por</h2>
                    <Form.Group controlId="orden">
                        <Form.Label className="small">Criterio</Form.Label>
                        <Form.Select value={orden} onChange={e => setOrden(e.target.value)}>
                            <option value="relevancia">Relevancia</option>
                            <option value="precio-asc">Menor precio</option>
                            <option value="precio-desc">Mayor precio</option>
                            <option value="nombre">Nombre (A-Z)</option>
                        </Form.Select>
                    </Form.Group>
                </Card>
            </Col>

            <Col as="main" md={9}>
                <h1>Productos</h1>
                <p className="text-muted">Catálogo completo de la tienda. Haz clic en un producto para ver su detalle.</p>
                {(!productos || error) && <EstadoCarga error={error} />}
                <Row xs={1} sm={2} lg={3} className="g-4">
                    {listaProductos.map(producto => (
                        <Col key={producto.id}>
                            <TarjetaProducto producto={producto} irA={irA} agregarAlCarrito={agregarAlCarrito} />
                        </Col>
                    ))}
                </Row>
            </Col>
        </Row>
    );
}

export default Productos;
