import { useEffect, useState } from "react";
import { Row, Col, Card, Form, Button, Alert, Image } from "react-bootstrap";
import ErroresApi from "./ErroresApi";
import { actualizarProducto, crearProducto, eliminarProducto, listarCategorias } from "../api";

function FormularioProducto({ producto, esNuevo, irA }) {
    const [categorias, setCategorias] = useState([]);
    const [codigo, setCodigo] = useState(producto.codigo);
    const [categoria, setCategoria] = useState(producto.categoria ? String(producto.categoria.id) : "");
    const [nombre, setNombre] = useState(producto.nombre);
    const [unidad, setUnidad] = useState(producto.unidad);
    const [descripcion, setDescripcion] = useState(producto.descripcion ?? "");
    const [precio, setPrecio] = useState(String(producto.precio));
    const [stock, setStock] = useState(String(producto.stock));
    const [stockCritico, setStockCritico] = useState(String(producto.stockCritico ?? ""));
    const [imagen, setImagen] = useState(producto.imagen ?? "");
    const [destacado, setDestacado] = useState(producto.destacado);
    const [publicado, setPublicado] = useState(producto.publicado);
    const [enviado, setEnviado] = useState(false);
    const [enviando, setEnviando] = useState(false);
    const [error, setError] = useState(null);

    useEffect(() => {
        listarCategorias().then(setCategorias).catch(e => setError(e));
    }, []);

    let errorCodigo = "";
    if (codigo.trim() === "") errorCodigo = "El código del producto es obligatorio.";
    else if (codigo.trim().length < 3) errorCodigo = "El código debe tener al menos 3 caracteres.";

    const errorCategoria = categoria === "" ? "Debe seleccionar una categoría." : "";
    const errorNombre = nombre.trim() === "" ? "El nombre del producto es obligatorio." : "";
    const errorUnidad = unidad.trim() === "" ? "La unidad de venta es obligatoria." : "";

    let errorPrecio = "";
    if (precio === "") errorPrecio = "El precio es obligatorio.";
    else if (Number(precio) < 0) errorPrecio = "El precio no puede ser negativo.";

    let errorStock = "";
    if (stock === "") errorStock = "El stock es obligatorio.";
    else if (!Number.isInteger(Number(stock)) || Number(stock) < 0) errorStock = "El stock debe ser un número entero mayor o igual a 0.";

    let errorCritico = "";
    if (stockCritico !== "" && (!Number.isInteger(Number(stockCritico)) || Number(stockCritico) < 0)) {
        errorCritico = "El stock crítico debe ser un número entero igual o mayor a 0.";
    }

    const stockBajo = stock !== "" && stockCritico !== "" && Number(stock) <= Number(stockCritico);

    async function enviar(e) {
        e.preventDefault();
        setEnviado(true);
        setError(null);

        if (errorCodigo || errorCategoria || errorNombre || errorUnidad || errorPrecio || errorStock || errorCritico) {
            return;
        }

        const datos = {
            codigo: codigo.trim(),
            nombre: nombre.trim(),
            categoria: { id: Number(categoria) },
            unidad: unidad.trim(),
            precio: Number(precio),
            stock: Number(stock),
            stockCritico: stockCritico === "" ? null : Number(stockCritico),
            imagen: imagen.trim() || null,
            alt: esNuevo ? null : producto.alt,
            descripcion: descripcion.trim() || null,
            destacado,
            publicado
        };

        setEnviando(true);
        try {
            const guardado = esNuevo ? await crearProducto(datos) : await actualizarProducto(producto.id, datos);
            irA("admin-producto-ver", guardado.id);
        } catch (err) {
            setError(err);
            setEnviando(false);
        }
    }

    async function eliminar() {
        if (!window.confirm(`¿Eliminar el producto ${producto.nombre}? Esta acción no se puede deshacer.`)) return;
        try {
            await eliminarProducto(producto.id);
            irA("admin-productos");
        } catch (err) {
            setError(err);
        }
    }

    return (
        <Card className="my-4 shadow-sm">
            <Card.Header as="h2" className="h6 bg-success text-white">
                {esNuevo ? "Datos del producto" : "Datos del producto " + producto.codigo}
            </Card.Header>

            <Card.Body>
                <Form noValidate onSubmit={enviar}>
                    <Row>
                        <Col md={6}>
                            <Form.Group className="mb-3" controlId="codigo">
                                <Form.Label>Código producto</Form.Label>
                                <Form.Control
                                    type="text"
                                    placeholder="FR-006"
                                    maxLength={20}
                                    value={codigo}
                                    onChange={e => setCodigo(e.target.value)}
                                    isInvalid={(enviado || codigo !== "") && errorCodigo !== ""}
                                />
                                <Form.Text>Mínimo 3 caracteres. Debe ser único.</Form.Text>
                                <Form.Control.Feedback type="invalid">{errorCodigo}</Form.Control.Feedback>
                            </Form.Group>
                        </Col>
                        <Col md={6}>
                            <Form.Group className="mb-3" controlId="categoria">
                                <Form.Label>Categoría</Form.Label>
                                <Form.Select
                                    value={categoria}
                                    onChange={e => setCategoria(e.target.value)}
                                    isInvalid={enviado && errorCategoria !== ""}
                                >
                                    <option value="">-- Seleccione la categoría --</option>
                                    {categorias.map(c => (
                                        <option key={c.id} value={c.id}>{c.nombre}</option>
                                    ))}
                                </Form.Select>
                                <Form.Control.Feedback type="invalid">{errorCategoria}</Form.Control.Feedback>
                            </Form.Group>
                        </Col>
                    </Row>

                    <Row>
                        <Col md={8}>
                            <Form.Group className="mb-3" controlId="nombre">
                                <Form.Label>Nombre</Form.Label>
                                <Form.Control
                                    type="text"
                                    maxLength={100}
                                    value={nombre}
                                    onChange={e => setNombre(e.target.value)}
                                    isInvalid={enviado && errorNombre !== ""}
                                />
                                <Form.Control.Feedback type="invalid">{errorNombre}</Form.Control.Feedback>
                            </Form.Group>
                        </Col>
                        <Col md={4}>
                            <Form.Group className="mb-3" controlId="unidad">
                                <Form.Label>Unidad de venta</Form.Label>
                                <Form.Control
                                    type="text"
                                    maxLength={40}
                                    placeholder="Kilo, Unidad, Bandeja 500 g..."
                                    value={unidad}
                                    onChange={e => setUnidad(e.target.value)}
                                    isInvalid={enviado && errorUnidad !== ""}
                                />
                                <Form.Control.Feedback type="invalid">{errorUnidad}</Form.Control.Feedback>
                            </Form.Group>
                        </Col>
                    </Row>

                    <Form.Group className="mb-3" controlId="descripcion">
                        <Form.Label>Descripción (opcional)</Form.Label>
                        <Form.Control as="textarea" rows={5} maxLength={500} value={descripcion} onChange={e => setDescripcion(e.target.value)} />
                        <Form.Text>Máximo 500 caracteres. Quedan {500 - descripcion.length}.</Form.Text>
                    </Form.Group>

                    <Row>
                        <Col md={4}>
                            <Form.Group className="mb-3" controlId="precio">
                                <Form.Label>Precio</Form.Label>
                                <Form.Control
                                    type="number"
                                    min="0"
                                    step="0.01"
                                    value={precio}
                                    onChange={e => setPrecio(e.target.value)}
                                    isInvalid={(enviado || precio !== "") && errorPrecio !== ""}
                                />
                                <Form.Text>Mínimo 0. Un precio 0 se considera producto GRATIS. Admite decimales.</Form.Text>
                                <Form.Control.Feedback type="invalid">{errorPrecio}</Form.Control.Feedback>
                            </Form.Group>
                        </Col>
                        <Col md={4}>
                            <Form.Group className="mb-3" controlId="stock">
                                <Form.Label>Stock</Form.Label>
                                <Form.Control
                                    type="number"
                                    min="0"
                                    step="1"
                                    value={stock}
                                    onChange={e => setStock(e.target.value)}
                                    isInvalid={(enviado || stock !== "") && errorStock !== ""}
                                />
                                <Form.Text>Cantidad en inventario. Sólo números enteros.</Form.Text>
                                <Form.Control.Feedback type="invalid">{errorStock}</Form.Control.Feedback>
                            </Form.Group>
                        </Col>
                        <Col md={4}>
                            <Form.Group className="mb-3" controlId="stock-critico">
                                <Form.Label>Stock crítico (opcional)</Form.Label>
                                <Form.Control
                                    type="number"
                                    min="0"
                                    step="1"
                                    value={stockCritico}
                                    onChange={e => setStockCritico(e.target.value)}
                                    isInvalid={errorCritico !== ""}
                                />
                                <Form.Text>Se muestra una alerta cuando el stock sea igual o inferior a este valor.</Form.Text>
                                <Form.Control.Feedback type="invalid">{errorCritico}</Form.Control.Feedback>
                            </Form.Group>
                        </Col>
                    </Row>

                    {stockBajo && (
                        <Alert variant="warning">
                            Atención: el stock actual ({stock}) es igual o inferior al stock crítico configurado ({stockCritico}).
                        </Alert>
                    )}

                    <Form.Group className="mb-3" controlId="imagen">
                        <Form.Label>Imagen (opcional)</Form.Label>
                        <Form.Control type="text" maxLength={200} placeholder="/img/productos/tomate.jpg" value={imagen} onChange={e => setImagen(e.target.value)} />
                        <Form.Text>
                            Las imágenes no se guardan en la base de datos: copia el archivo en <code>public/img/productos</code> y escribe aquí su ruta.
                        </Form.Text>
                    </Form.Group>

                    {imagen.trim() !== "" && <Image src={imagen.trim()} alt={nombre} width="200" height="150" rounded thumbnail className="mb-3 object-fit-cover" />}

                    <Form.Check
                        type="checkbox"
                        id="publicado"
                        label="Publicar en la tienda"
                        className="mb-2"
                        checked={publicado}
                        onChange={e => setPublicado(e.target.checked)}
                    />

                    <Form.Check
                        type="checkbox"
                        id="destacado"
                        label="Mostrar como destacado en el inicio"
                        className="mb-3"
                        checked={destacado}
                        onChange={e => setDestacado(e.target.checked)}
                    />

                    <ErroresApi error={error} />

                    <div className="d-flex flex-wrap gap-2">
                        <Button type="submit" variant="success" disabled={enviando}>
                            {enviando ? "Guardando..." : esNuevo ? "Guardar producto" : "Guardar cambios"}
                        </Button>
                        <Button href="#" variant="outline-success" onClick={() => irA("admin-productos")}>Cancelar</Button>
                        {!esNuevo && <Button variant="outline-danger" onClick={eliminar}>Eliminar producto</Button>}
                    </div>
                </Form>
            </Card.Body>
        </Card>
    );
}

export default FormularioProducto;
