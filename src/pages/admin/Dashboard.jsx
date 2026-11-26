import { useEffect, useState } from "react";
import { Row, Col, Card, Button, Table, Badge } from "react-bootstrap";
import EstadoCarga from "../../components/EstadoCarga";
import { listarProductos, listarUsuarios } from "../../api";
import { enStockCritico, tiposUsuario } from "../../formato";

function Dashboard({ sesion, irA }) {
    const [productos, setProductos] = useState(null);
    const [usuarios, setUsuarios] = useState(null);
    const [error, setError] = useState("");

    useEffect(() => {
        listarProductos().then(setProductos).catch(e => setError(e.message));
        listarUsuarios().then(setUsuarios).catch(e => setError(e.message));
    }, []);

    if (!productos || !usuarios || error) return <EstadoCarga error={error} />;

    const publicados = productos.filter(p => p.publicado);
    const criticos = productos.filter(enStockCritico);

    return (
        <>
            <div className="d-flex justify-content-between align-items-center flex-wrap gap-2 border-bottom border-success pb-2 mb-4">
                <h1 className="h3 m-0 text-success">¡Hola, {sesion.nombre}!</h1>
                <p className="m-0">Sesión iniciada como <strong>{sesion.correo}</strong> · Perfil: <strong>{tiposUsuario[sesion.tipo]}</strong></p>
            </div>

            <main>
                <section className="mb-4">
                    <h2 className="h5">Resumen de hoy</h2>
                    <Row xs={1} md={2} lg={4} className="g-3">
                        <Col>
                            <Card body className="h-100"><strong className="d-block fs-3 text-success">{publicados.length}</strong> Productos publicados</Card>
                        </Col>
                        <Col>
                            <Card body className="h-100"><strong className="d-block fs-3 text-success">{criticos.length}</strong> Productos en stock crítico</Card>
                        </Col>
                        <Col>
                            <Card body className="h-100"><strong className="d-block fs-3 text-success">{usuarios.length}</strong> Usuarios registrados</Card>
                        </Col>
                        <Col>
                            <Card body className="h-100"><strong className="d-block fs-3 text-success">7</strong> Órdenes del día</Card>
                        </Col>
                    </Row>
                </section>

                <section className="mb-4">
                    <h2 className="h5">Mantenedores</h2>
                    <Row xs={1} md={2} className="g-3">
                        <Col>
                            <Card className="h-100">
                                <Card.Body>
                                    <Card.Title as="h3" className="h6">Productos</Card.Title>
                                    <Card.Text>Crear, editar, ver y eliminar los productos del catálogo.</Card.Text>
                                    <Button href="#" variant="outline-success" onClick={() => irA("admin-productos")}>Abrir mantenedor</Button>
                                </Card.Body>
                            </Card>
                        </Col>
                        <Col>
                            <Card className="h-100">
                                <Card.Body>
                                    <Card.Title as="h3" className="h6">Usuarios</Card.Title>
                                    <Card.Text>Administrar cuentas y perfiles (Administrador, Vendedor, Cliente).</Card.Text>
                                    <Button href="#" variant="outline-success" onClick={() => irA("admin-usuarios")}>Abrir mantenedor</Button>
                                </Card.Body>
                            </Card>
                        </Col>
                    </Row>
                </section>

                <section>
                    <h2 className="h5">Alertas de stock crítico</h2>
                    <p>
                        Se listan los productos cuyo stock actual es igual o inferior al stock
                        crítico configurado.
                    </p>

                    <Table striped bordered hover responsive size="sm" className="caption-top">
                        <caption>Productos que requieren reposición</caption>
                        <thead>
                            <tr>
                                <th>Código</th>
                                <th>Producto</th>
                                <th>Stock</th>
                                <th>Stock crítico</th>
                                <th>Acción</th>
                            </tr>
                        </thead>
                        <tbody>
                            {criticos.map(producto => (
                                <tr key={producto.id}>
                                    <th>{producto.codigo}</th>
                                    <td>{producto.nombre}</td>
                                    <td><Badge bg="danger">{producto.stock}</Badge></td>
                                    <td>{producto.stockCritico}</td>
                                    <td>
                                        <a href="#" onClick={() => irA("admin-producto-editar", producto.id)} className="link-success">Editar</a>
                                    </td>
                                </tr>
                            ))}
                        </tbody>
                    </Table>
                </section>
            </main>
        </>
    );
}

export default Dashboard;
