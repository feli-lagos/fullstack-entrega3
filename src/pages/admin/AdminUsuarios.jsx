import { useEffect, useState } from "react";
import { Row, Col, Form, Button, Table, Pagination, Alert } from "react-bootstrap";
import EstadoCarga from "../../components/EstadoCarga";
import { eliminarUsuario, listarRegiones, listarUsuarios } from "../../api";
import { tiposUsuario } from "../../formato";

function AdminUsuarios({ irA }) {
    const [usuarios, setUsuarios] = useState(null);
    const [regiones, setRegiones] = useState([]);
    const [error, setError] = useState("");
    const [aviso, setAviso] = useState(null);

    useEffect(() => {
        listarUsuarios().then(setUsuarios).catch(e => setError(e.message));
        listarRegiones().then(setRegiones).catch(e => setError(e.message));
    }, []);

    async function eliminar(usuario) {
        if (!window.confirm(`¿Eliminar a ${usuario.nombre} ${usuario.apellidos}? Esta acción no se puede deshacer.`)) return;
        try {
            await eliminarUsuario(usuario.id);
            setUsuarios(actual => actual.filter(u => u.id !== usuario.id));
            setAviso({ tipo: "success", texto: `Usuario ${usuario.run} eliminado.` });
        } catch (err) {
            setAviso({ tipo: "danger", texto: err.message });
        }
    }

    if (!usuarios || error) return <EstadoCarga error={error} />;

    return (
        <>
            <div className="d-flex justify-content-between align-items-center flex-wrap gap-2 border-bottom border-success pb-2 mb-4">
                <h1 className="h3 m-0 text-success">Usuarios</h1>
                <Button href="#" variant="success" onClick={() => irA("admin-usuario-nuevo")}>Nuevo usuario</Button>
            </div>

            <main>
                <Form onSubmit={e => e.preventDefault()} className="mb-3">
                    <Row className="g-2 align-items-end">
                        <Col md={4}>
                            <Form.Group controlId="buscar">
                                <Form.Label>Buscar</Form.Label>
                                <Form.Control type="search" placeholder="Run, nombre o correo" maxLength={100} />
                            </Form.Group>
                        </Col>
                        <Col md={3}>
                            <Form.Group controlId="perfil">
                                <Form.Label>Tipo de usuario</Form.Label>
                                <Form.Select>
                                    <option value="">Todos</option>
                                    <option value="administrador">Administrador</option>
                                    <option value="vendedor">Vendedor</option>
                                    <option value="cliente">Cliente</option>
                                </Form.Select>
                            </Form.Group>
                        </Col>
                        <Col md={3}>
                            <Form.Group controlId="region">
                                <Form.Label>Región</Form.Label>
                                <Form.Select>
                                    <option value="">Todas</option>
                                    {regiones.map(region => (
                                        <option key={region.id} value={region.id}>{region.nombre}</option>
                                    ))}
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
                    <caption>Listado de usuarios ({usuarios.length} registros)</caption>
                    <thead>
                        <tr>
                            <th>Run</th>
                            <th>Nombre</th>
                            <th>Apellidos</th>
                            <th>Correo</th>
                            <th>Tipo de usuario</th>
                            <th>Región</th>
                            <th>Acciones</th>
                        </tr>
                    </thead>
                    <tbody>
                        {usuarios.map(usuario => (
                            <tr key={usuario.id}>
                                <th>{usuario.run}</th>
                                <td>{usuario.nombre}</td>
                                <td>{usuario.apellidos}</td>
                                <td>{usuario.correo}</td>
                                <td>{tiposUsuario[usuario.tipo]}</td>
                                <td>{usuario.comuna.region.nombre}</td>
                                <td>
                                    <a href="#" onClick={() => irA("admin-usuario-ver", usuario.id)} className="link-success">Ver</a>
                                    {" · "}
                                    <a href="#" onClick={() => irA("admin-usuario-editar", usuario.id)} className="link-success">Editar</a>
                                    {" · "}
                                    <Button variant="outline-danger" size="sm" onClick={() => eliminar(usuario)}>Eliminar</Button>
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
                    <Pagination.Item>4</Pagination.Item>
                    <Pagination.Item>5</Pagination.Item>
                    <Pagination.Next>&raquo;</Pagination.Next>
                </Pagination>
            </main>
        </>
    );
}

export default AdminUsuarios;
