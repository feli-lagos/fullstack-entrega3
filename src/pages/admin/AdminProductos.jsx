import { useEffect, useState } from "react";
import { Row, Col, Form, Button, Table, Badge, Pagination, Alert } from "react-bootstrap";
import EstadoCarga from "../../components/EstadoCarga";
import { eliminarProducto, listarCategorias, listarProductos } from "../../api";
import { enStockCritico, formatoPesos } from "../../formato";

function AdminProductos({ irA }) {
    const [productos, setProductos] = useState(null);
    const [categorias, setCategorias] = useState([]);
    const [error, setError] = useState("");
    const [aviso, setAviso] = useState(null);

    useEffect(() => {
        listarProductos().then(setProductos).catch(e => setError(e.message));
        listarCategorias().then(setCategorias).catch(e => setError(e.message));
    }, []);

    async function eliminar(producto) {
        if (!window.confirm(`¿Eliminar el producto ${producto.nombre}? Esta acción no se puede deshacer.`)) return;
        try {
            await eliminarProducto(producto.id);
            setProductos(actual => actual.filter(p => p.id !== producto.id));
            setAviso({ tipo: "success", texto: `Producto ${producto.codigo} eliminado.` });
        } catch (err) {
            setAviso({ tipo: "danger", texto: err.message });
        }
    }

    if (!productos || error) return <EstadoCarga error={error} />;

    return (
        <>
            <div className="d-flex justify-content-between align-items-center flex-wrap gap-2 border-bottom border-success pb-2 mb-4">
                <h1 className="h3 m-0 text-success">Productos</h1>
                <Button href="#" variant="success" onClick={() => irA("admin-producto-nuevo")}>Nuevo producto</Button>
            </div>

            <main>
                <Form onSubmit={e => e.preventDefault()} className="mb-3">
                    <Row className="g-2 align-items-end">
                        <Col md={4}>
                            <Form.Group controlId="buscar">
                                <Form.Label>Buscar</Form.Label>
                                <Form.Control type="search" placeholder="Código o nombre" maxLength={100} />
                            </Form.Group>
                        </Col>
                        <Col md={3}>
                            <Form.Group controlId="categoria">
                                <Form.Label>Categoría</Form.Label>
                                <Form.Select>
                                    <option value="">Todas</option>
                                    {categorias.map(c => (
                                        <option key={c.id} value={c.codigo}>{c.nombre}</option>
                                    ))}
                                </Form.Select>
                            </Form.Group>
                        </Col>
                        <Col md={3}>
                            <Form.Group controlId="estado">
                                <Form.Label>Estado</Form.Label>
                                <Form.Select>
                                    <option value="">Todos</option>
                                    <option value="publicado">Publicado</option>
                                    <option value="borrador">Borrador</option>
                                    <option value="critico">Stock crítico</option>
                                </Form.Select>
                            </Form.Group>
                        </Col>
                        <Col xs="auto">
                            <Button type="submit" variant="outline-success">Filtrar</Button>
                        </Col>
                    </Row>
                </Form>

                {aviso && <Alert variant={aviso.tipo} dismissible onClose={() => setAviso(null)}>{aviso.texto}</Alert>}

                <Table striped bordered hover responsive size="sm" className="caption-top">
                    <caption>Listado de productos ({productos.length} registros)</caption>
                    <thead>
                        <tr>
                            <th>Código</th>
                            <th>Nombre</th>
                            <th>Categoría</th>
                            <th>Precio</th>
                            <th>Stock</th>
                            <th>Stock crítico</th>
                            <th>Acciones</th>
                        </tr>
                    </thead>
                    <tbody>
                        {productos.map(producto => (
                            <tr key={producto.id}>
                                <th>{producto.codigo}</th>
                                <td>{producto.nombre}</td>
                                <td>{producto.categoria.nombre}</td>
                                <td>{formatoPesos(producto.precio)}</td>
                                <td>
                                    {enStockCritico(producto)
                                        ? <Badge bg="danger">{producto.stock}</Badge>
                                        : producto.stock}
                                </td>
                                <td>{producto.stockCritico ?? "—"}</td>
                                <td>
                                    <a href="#" onClick={() => irA("admin-producto-ver", producto.id)} className="link-success">Ver</a>
                                    {" · "}
                                    <a href="#" onClick={() => irA("admin-producto-editar", producto.id)} className="link-success">Editar</a>
                                    {" · "}
                                    <Button variant="outline-danger" size="sm" onClick={() => eliminar(producto)}>Eliminar</Button>
                                </td>
                            </tr>
                        ))}
                    </tbody>
                </Table>

                <Pagination size="sm" className="mt-3">
                    <Pagination.Prev>&laquo;</Pagination.Prev>
                    <Pagination.Item active>1</Pagination.Item>
                    <Pagination.Item>2</Pagination.Item>
                    <Pagination.Item>3</Pagination.Item>
                    <Pagination.Next>&raquo;</Pagination.Next>
                </Pagination>
            </main>
        </>
    );
}

export default AdminProductos;
