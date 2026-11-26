import { useState } from "react";
import { Container, Row, Col, Alert } from "react-bootstrap";
import Header from "./components/Header";
import Footer from "./components/Footer";
import MenuAdmin from "./components/MenuAdmin";
import Inicio from "./pages/Inicio";
import Productos from "./pages/Productos";
import DetalleProducto from "./pages/DetalleProducto";
import Nosotros from "./pages/Nosotros";
import Blogs from "./pages/Blogs";
import Blog1 from "./pages/Blog1";
import Blog2 from "./pages/Blog2";
import Contacto from "./pages/Contacto";
import Login from "./pages/Login";
import Registro from "./pages/Registro";
import Carrito from "./pages/Carrito";
import Dashboard from "./pages/admin/Dashboard";
import AdminProductos from "./pages/admin/AdminProductos";
import ProductoNuevo from "./pages/admin/ProductoNuevo";
import ProductoVer from "./pages/admin/ProductoVer";
import ProductoEditar from "./pages/admin/ProductoEditar";
import AdminUsuarios from "./pages/admin/AdminUsuarios";
import UsuarioNuevo from "./pages/admin/UsuarioNuevo";
import UsuarioVer from "./pages/admin/UsuarioVer";
import UsuarioEditar from "./pages/admin/UsuarioEditar";

// El carrito guarda { id, cantidad } con el id del producto en la base de datos.
// Las líneas de versiones anteriores (que usaban el código) se descartan.
function cargarCarrito() {
    const guardado = JSON.parse(localStorage.getItem("carrito")) || [];
    return guardado.filter(linea => Number.isInteger(linea.id));
}

function App() {
    const [pagina, setPagina] = useState("inicio");
    const [seleccionado, setSeleccionado] = useState("");
    const [carrito, setCarrito] = useState(cargarCarrito);
    const [mensaje, setMensaje] = useState("");
    const [sesion, setSesion] = useState(null);

    function irA(nuevaPagina, id = "") {
        setPagina(nuevaPagina);
        setSeleccionado(id);
        setMensaje("");
        window.scrollTo(0, 0);
    }

    function guardarCarrito(nuevoCarrito) {
        setCarrito(nuevoCarrito);
        localStorage.setItem("carrito", JSON.stringify(nuevoCarrito));
    }

    function agregarAlCarrito(producto, cantidad) {
        const linea = carrito.find(l => l.id === producto.id);
        const cantidadActual = linea ? linea.cantidad : 0;

        if (cantidadActual + cantidad > producto.stock) {
            setMensaje(`No hay más stock disponible de ${producto.nombre}. Máximo: ${producto.stock}.`);
            return;
        }

        if (linea) {
            guardarCarrito(carrito.map(l => l.id === producto.id ? { ...l, cantidad: l.cantidad + cantidad } : l));
        } else {
            guardarCarrito([...carrito, { id: producto.id, cantidad }]);
        }
        setMensaje(`${producto.nombre} fue agregado al carrito.`);
    }

    function eliminarDelCarrito(id) {
        guardarCarrito(carrito.filter(l => l.id !== id));
    }

    function cambiarCantidad(producto, cantidad) {
        if (cantidad <= 0) {
            eliminarDelCarrito(producto.id);
            return;
        }
        if (cantidad > producto.stock) {
            setMensaje(`No puedes superar el stock disponible de ${producto.stock} unidades.`);
            return;
        }
        guardarCarrito(carrito.map(l => l.id === producto.id ? { ...l, cantidad } : l));
    }

    function cerrarSesion() {
        setSesion(null);
        irA("login");
    }

    let cantidadTotal = 0;
    for (const linea of carrito) {
        cantidadTotal += linea.cantidad;
    }

    // El panel solo se muestra a administradores que iniciaron sesión
    const esAdministrador = sesion?.tipo === "administrador";
    const paginaActual = pagina.startsWith("admin") && !esAdministrador ? "login" : pagina;

    if (paginaActual.startsWith("admin")) {
        return (
            <Container fluid>
                <Row>
                    <Col md={3} lg={2} className="bg-body-tertiary border-end p-3">
                        <MenuAdmin pagina={paginaActual} irA={irA} cerrarSesion={cerrarSesion} />
                    </Col>
                    <Col md={9} lg={10} className="p-4 min-vh-100">
                        {paginaActual === "admin" && <Dashboard sesion={sesion} irA={irA} />}
                        {paginaActual === "admin-productos" && <AdminProductos irA={irA} />}
                        {paginaActual === "admin-producto-nuevo" && <ProductoNuevo irA={irA} />}
                        {paginaActual === "admin-producto-ver" && <ProductoVer key={seleccionado} id={seleccionado} irA={irA} />}
                        {paginaActual === "admin-producto-editar" && <ProductoEditar key={seleccionado} id={seleccionado} irA={irA} />}
                        {paginaActual === "admin-usuarios" && <AdminUsuarios irA={irA} />}
                        {paginaActual === "admin-usuario-nuevo" && <UsuarioNuevo irA={irA} />}
                        {paginaActual === "admin-usuario-ver" && <UsuarioVer key={seleccionado} id={seleccionado} irA={irA} />}
                        {paginaActual === "admin-usuario-editar" && <UsuarioEditar key={seleccionado} id={seleccionado} irA={irA} />}
                        <footer className="mt-5 pt-3 border-top text-muted">
                            <small>Panel de administración · Huerto Verde · Entrega 1 DSY1104</small>
                        </footer>
                    </Col>
                </Row>
            </Container>
        );
    }

    return (
        <>
            <Header pagina={paginaActual} irA={irA} cantidad={cantidadTotal} />
            <Container className="py-4">
                {mensaje && (
                    <Alert variant="info" dismissible onClose={() => setMensaje("")}>
                        {mensaje}
                    </Alert>
                )}
                {paginaActual === "inicio" && <Inicio irA={irA} />}
                {paginaActual === "productos" && <Productos categoria={seleccionado} irA={irA} agregarAlCarrito={agregarAlCarrito} />}
                {paginaActual === "detalle" && <DetalleProducto key={seleccionado} id={seleccionado} irA={irA} agregarAlCarrito={agregarAlCarrito} />}
                {paginaActual === "nosotros" && <Nosotros />}
                {paginaActual === "blogs" && <Blogs irA={irA} />}
                {paginaActual === "blog1" && <Blog1 irA={irA} />}
                {paginaActual === "blog2" && <Blog2 irA={irA} />}
                {paginaActual === "contacto" && <Contacto />}
                {paginaActual === "login" && <Login irA={irA} onIniciarSesion={setSesion} />}
                {paginaActual === "registro" && <Registro irA={irA} />}
                {paginaActual === "carrito" && (
                    <Carrito
                        carrito={carrito}
                        irA={irA}
                        cambiarCantidad={cambiarCantidad}
                        eliminarDelCarrito={eliminarDelCarrito}
                    />
                )}
            </Container>
            <Footer irA={irA} />
        </>
    );
}

export default App;
