import { Pagination } from "react-bootstrap";

// Botones de página según cuántas páginas hay de verdad (si hay una sola, no se muestran)
function Paginacion({ pagina, totalPaginas, onCambiar }) {
    if (totalPaginas <= 1) return null;

    const numeros = [];
    for (let i = 1; i <= totalPaginas; i++) {
        numeros.push(i);
    }

    return (
        <Pagination size="sm" className="mt-3">
            <Pagination.Prev disabled={pagina === 1} onClick={() => onCambiar(pagina - 1)}>&laquo;</Pagination.Prev>
            {numeros.map(numero => (
                <Pagination.Item key={numero} active={numero === pagina} onClick={() => onCambiar(numero)}>
                    {numero}
                </Pagination.Item>
            ))}
            <Pagination.Next disabled={pagina === totalPaginas} onClick={() => onCambiar(pagina + 1)}>&raquo;</Pagination.Next>
        </Pagination>
    );
}

export default Paginacion;
