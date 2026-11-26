import { useEffect, useState } from "react";
import { Row, Col, Form, Button, Table, Alert } from "react-bootstrap";
import EstadoCarga from "../../components/EstadoCarga";
import Paginacion from "../../components/Paginacion";
import { eliminarUsuario, listarRegiones, listarUsuarios } from "../../api";
import { contieneTexto, paginar, tiposUsuario } from "../../formato";

function AdminUsuarios({ irA }) {
    const [usuarios, setUsuarios] = useState(null);
    const [regiones, setRegiones] = useState([]);
    const [error, setError] = useState("");
    const [aviso, setAviso] = useState(null);
    const [busqueda, setBusqueda] = useState("");
    const [tipo, setTipo] = useState("");
    const [region, setRegion] = useState("");
    const [pagina, setPagina] = useState(1);

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

    // Al cambiar un filtro se vuelve a la página 1
    function cambiarFiltro(setFiltro) {
        return e => {
            setFiltro(e.target.value);
            setPagina(1);
        };
    }

    function limpiarFiltros() {
        setBusqueda("");
        setTipo("");
        setRegion("");
        setPagina(1);
    }

    if (!usuarios || error) return <EstadoCarga error={error} />;

    // Los filtros se aplican sobre los usuarios que ya llegaron de la API
    const texto = busqueda.trim().toLowerCase();
    const filtrados = usuarios.filter(usuario =>
        (texto === "" ||
            contieneTexto(usuario.run, texto) ||
            contieneTexto(`${usuario.nombre} ${usuario.apellidos}`, texto) ||
            contieneTexto(usuario.correo, texto)) &&
        (tipo === "" || usuario.tipo === tipo) &&
        (region === "" || String(usuario.comuna.region.id) === region)
    );
    const { visibles, paginaActual, totalPaginas } = paginar(filtrados, pagina);

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
                                <Form.Control type="search" placeholder="Run, nombre o correo" maxLength={100} value={busqueda} onChange={cambiarFiltro(setBusqueda)} />
                            </Form.Group>
                        </Col>
                        <Col md={3}>
                            <Form.Group controlId="perfil">
                                <Form.Label>Tipo de usuario</Form.Label>
                                <Form.Select value={tipo} onChange={cambiarFiltro(setTipo)}>
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
                                <Form.Select value={region} onChange={cambiarFiltro(setRegion)}>
                                    <option value="">Todas</option>
                                    {regiones.map(r => (
                                        <option key={r.id} value={r.id}>{r.nombre}</option>
                                    ))}
                                </Form.Select>
                            </Form.Group>
                        </Col>
                        <Col xs="auto">
                            <Button variant="outline-success" onClick={limpiarFiltros}>Limpiar filtros</Button>
                        </Col>
                    </Row>
                </Form>

                {aviso && <Alert variant={aviso.tipo} dismissible onClose={() => setAviso(null)}>{aviso.texto}</Alert>}

                <Table striped bordered hover responsive size="sm" className="caption-top">
                    <caption>Listado de usuarios ({filtrados.length} de {usuarios.length} registros)</caption>
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
                        {visibles.length === 0 && (
                            <tr>
                                <td colSpan={7} className="text-center text-muted">No hay usuarios que coincidan con los filtros.</td>
                            </tr>
                        )}
                        {visibles.map(usuario => (
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

                <Paginacion pagina={paginaActual} totalPaginas={totalPaginas} onCambiar={setPagina} />
            </main>
        </>
    );
}

export default AdminUsuarios;
