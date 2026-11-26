import { useEffect, useState } from "react";
import { Row, Col, Card, ListGroup, Image, Button, Form, InputGroup, Alert } from "react-bootstrap";
import EstadoCarga from "../components/EstadoCarga";
import ErroresApi from "../components/ErroresApi";
import { crearPedido, listarProductos } from "../api";
import { formatoPesos } from "../formato";

function Carrito({ carrito, sesion, irA, cambiarCantidad, eliminarDelCarrito, vaciarCarrito }) {
    const [productos, setProductos] = useState(null);
    const [error, setError] = useState("");
    const [pagando, setPagando] = useState(false);
    const [errorPago, setErrorPago] = useState(null);
    const [pedido, setPedido] = useState(null);

    useEffect(() => {
        listarProductos(true).then(setProductos).catch(e => setError(e.message));
    }, []);

    // Compra confirmada: se muestra el pedido tal como quedó guardado en la base de datos
    if (pedido) {
        return (
            <main>
                <Alert variant="success">
                    <Alert.Heading as="h1" className="fs-4">¡Gracias por tu compra, {pedido.usuario.nombre}!</Alert.Heading>
                    <p className="mb-0">
                        Tu pedido <strong>N° {pedido.id}</strong> quedó registrado.
                        Total pagado: <strong>{formatoPesos(pedido.total)}</strong>.
                    </p>
                </Alert>

                <ListGroup className="mb-3">
                    {pedido.detalles.map(detalle => (
                        <ListGroup.Item key={detalle.id} className="d-flex justify-content-between">
                            <span>{detalle.producto.nombre} × {detalle.cantidad}</span>
                            <span>{formatoPesos(detalle.subtotal)}</span>
                        </ListGroup.Item>
                    ))}
                    <ListGroup.Item className="d-flex justify-content-between">
                        <span>Despacho</span>
                        <span>{formatoPesos(pedido.despacho)}</span>
                    </ListGroup.Item>
                </ListGroup>

                <Button variant="success" onClick={() => irA("productos")}>Seguir comprando</Button>
            </main>
        );
    }

    if (!productos || error) return <EstadoCarga error={error} />;

    // Se ignoran las líneas de productos que ya no existen o dejaron de publicarse
    const lineas = carrito
        .map(item => ({ ...item, producto: productos.find(p => p.id === item.id) }))
        .filter(item => item.producto);

    let subtotal = 0;
    for (const item of lineas) {
        subtotal += item.producto.precio * item.cantidad;
    }
    const despacho = subtotal > 0 ? 2990 : 0;

    // Al pagar, el carrito (que hasta ahora solo vivía en el navegador) viaja al backend
    // y se guarda en H2 como un pedido. Solo se mandan ids y cantidades: el precio lo pone el backend.
    async function pagar() {
        setPagando(true);
        setErrorPago(null);
        try {
            const creado = await crearPedido({
                usuarioId: sesion.id,
                items: lineas.map(item => ({ productoId: item.id, cantidad: item.cantidad }))
            });
            vaciarCarrito();
            setPedido(creado);
        } catch (err) {
            setErrorPago(err);
            // Se recargan los productos para mostrar el stock real (por si alguien compró antes)
            listarProductos(true).then(setProductos).catch(() => {});
        } finally {
            setPagando(false);
        }
    }

    return (
        <main>
            <h1 className="mb-4">Mi carrito de compras</h1>

            <Row className="g-4">
                <Col as="section" lg={8}>
                    {lineas.length === 0 && <p className="text-muted">Tu carrito está vacío.</p>}
                    <ListGroup className="mb-3">
                        {lineas.map(item => {
                            const producto = item.producto;

                            return (
                                <ListGroup.Item key={item.id} as="article" className="py-3">
                                    <Row className="align-items-center g-3">
                                        <Col xs={3} md={2}>
                                            <Image src={producto.imagen} alt={producto.alt} rounded height="56" className="w-100 object-fit-cover" />
                                        </Col>

                                        <Col xs={9} md={4}>
                                            <h3 className="fs-6 mb-1">
                                                <a href="#" onClick={() => irA("detalle", producto.id)} className="link-success">{producto.nombre}</a>
                                            </h3>
                                            <small className="text-muted">{producto.unidad} · {producto.categoria.nombre} · Código {producto.codigo}</small>
                                        </Col>

                                        <Col xs={4} md={2} className="fw-bold">{formatoPesos(producto.precio)}</Col>

                                        <Col xs={5} md={2}>
                                            <InputGroup size="sm">
                                                <Button variant="outline-secondary" onClick={() => cambiarCantidad(producto, item.cantidad - 1)}>−</Button>
                                                <Form.Control
                                                    type="number"
                                                    value={item.cantidad}
                                                    min="1"
                                                    max={producto.stock}
                                                    className="text-center"
                                                    onChange={e => {
                                                        const nuevaCantidad = Number(e.target.value);
                                                        if (nuevaCantidad >= 1) cambiarCantidad(producto, nuevaCantidad);
                                                    }}
                                                />
                                                <Button variant="outline-secondary" onClick={() => cambiarCantidad(producto, item.cantidad + 1)}>+</Button>
                                            </InputGroup>
                                        </Col>

                                        <Col xs={3} md={2} className="text-end">
                                            <Button variant="outline-danger" size="sm" onClick={() => eliminarDelCarrito(item.id)}>Eliminar</Button>
                                        </Col>
                                    </Row>
                                </ListGroup.Item>
                            );
                        })}
                    </ListGroup>

                    <a href="#" onClick={() => irA("productos")} className="link-success">Seguir comprando</a>
                </Col>

                <Col as="aside" lg={4}>
                    <Card>
                        <Card.Body>
                            <Card.Title as="h2" className="fs-5 mb-3">Resumen</Card.Title>
                            <Row as="dl">
                                <Col as="dt" xs={6}>Subtotal</Col>
                                <Col as="dd" xs={6} className="text-end">{formatoPesos(subtotal)}</Col>
                                <Col as="dt" xs={6}>Despacho</Col>
                                <Col as="dd" xs={6} className="text-end">{formatoPesos(despacho)}</Col>
                                <Col as="dt" xs={6} className="border-top pt-2">TOTAL</Col>
                                <Col as="dd" xs={6} className="border-top pt-2 text-end fw-bold">{formatoPesos(subtotal + despacho)}</Col>
                            </Row>

                            <ErroresApi error={errorPago} />

                            {sesion ? (
                                <Button variant="success" className="w-100" disabled={lineas.length === 0 || pagando} onClick={pagar}>
                                    {pagando ? "Procesando pago..." : "Pagar"}
                                </Button>
                            ) : (
                                <Button variant="success" className="w-100" onClick={() => irA("login")}>
                                    Inicia sesión para pagar
                                </Button>
                            )}

                            <p className="mt-3 mb-0 small text-muted">
                                Mientras eliges, el carrito se guarda en <code>localStorage</code> (se
                                conserva si cierras la pestaña). Al pagar, se envía al backend y queda
                                guardado como pedido en la base de datos.
                            </p>
                        </Card.Body>
                    </Card>
                </Col>
            </Row>
        </main>
    );
}

export default Carrito;
