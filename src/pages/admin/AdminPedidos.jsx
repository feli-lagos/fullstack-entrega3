import { useEffect, useState } from "react";
import { Table } from "react-bootstrap";
import EstadoCarga from "../../components/EstadoCarga";
import { listarPedidos } from "../../api";
import { formatoFechaHora, formatoPesos } from "../../formato";

// Listado de las compras hechas desde el carrito de la tienda (los más nuevos primero)
function AdminPedidos() {
    const [pedidos, setPedidos] = useState(null);
    const [error, setError] = useState("");

    useEffect(() => {
        listarPedidos().then(setPedidos).catch(e => setError(e.message));
    }, []);

    if (!pedidos || error) return <EstadoCarga error={error} />;

    return (
        <>
            <div className="border-bottom border-success pb-2 mb-4">
                <h1 className="h3 m-0 text-success">Pedidos</h1>
            </div>

            <main>
                <Table striped bordered hover responsive size="sm" className="caption-top">
                    <caption>Pedidos registrados ({pedidos.length})</caption>
                    <thead>
                        <tr>
                            <th>N°</th>
                            <th>Fecha</th>
                            <th>Cliente</th>
                            <th>Productos</th>
                            <th>Despacho</th>
                            <th>Total</th>
                        </tr>
                    </thead>
                    <tbody>
                        {pedidos.map(pedido => (
                            <tr key={pedido.id}>
                                <th>{pedido.id}</th>
                                <td>{formatoFechaHora(pedido.fecha)}</td>
                                <td>
                                    {pedido.usuario.nombre} {pedido.usuario.apellidos}
                                    <br />
                                    <small className="text-muted">{pedido.usuario.correo}</small>
                                </td>
                                <td>
                                    {pedido.detalles.map(detalle => (
                                        <div key={detalle.id}>
                                            {detalle.producto.nombre} × {detalle.cantidad}
                                            <small className="text-muted"> ({formatoPesos(detalle.precioUnitario)} c/u)</small>
                                        </div>
                                    ))}
                                </td>
                                <td>{formatoPesos(pedido.despacho)}</td>
                                <td className="fw-bold">{formatoPesos(pedido.total)}</td>
                            </tr>
                        ))}
                    </tbody>
                </Table>
            </main>
        </>
    );
}

export default AdminPedidos;
