/* ============================================================
   app.js — CRUD de Inventario SPA
   ============================================================ */

const API_URL = "/productos";

let productos = [];
let idEditando = null;

/* ============================================================
   NAVEGACION SPA
   ============================================================ */

function mostrarSeccion(seccion) {
    document.querySelectorAll('.seccion-vista').forEach(s => s.classList.remove('activa'));
    document.getElementById('seccion-' + seccion).classList.add('activa');

    document.querySelectorAll('.nav-link').forEach(n => n.classList.remove('active'));
    document.getElementById('nav-' + seccion).classList.add('active');

    if (seccion === 'productos') {
        mostrarProductosEnTabla(productos);
    }
}

/* ============================================================
   CARGAR PRODUCTOS
   ============================================================ */

async function cargarProductos() {
    try {
        const respuesta = await fetch(API_URL, { credentials: 'include' });
        if (respuesta.status === 401) { window.location.href = '/login.html'; return; }
        productos = await respuesta.json();
        mostrarProductosEnTabla(productos);
        actualizarResumen();
    } catch (error) {
        console.error("Error al cargar productos:", error);
    }
}

function mostrarProductosEnTabla(listaProductos) {
    const tabla = document.getElementById("tablaProductos");
    if (!tabla) return;

    tabla.innerHTML = "";

    if (listaProductos.length === 0) {
        tabla.innerHTML = '<tr><td colspan="9" class="text-center py-4 text-muted">No se encontraron productos</td></tr>';
        const tg = document.getElementById("totalGeneral");
        if (tg) tg.textContent = "$0";
        return;
    }

    let totalGeneral = 0;

    listaProductos.forEach(producto => {
        const valorTotal = producto.precio * producto.cantidad;
        totalGeneral += valorTotal;
        const estado = producto.cantidad > 0
            ? '<span class="badge bg-success">En stock</span>'
            : '<span class="badge bg-danger">Agotado</span>';

        tabla.innerHTML += `
            <tr>
                <td class="fw-semibold">${producto.codigo}</td>
                <td>${producto.nombre}</td>
                <td><span class="badge bg-info text-dark">${producto.categoria}</span></td>
                <td>${producto.proveedor || 'N/A'}</td>
                <td>$${producto.precio.toLocaleString()}</td>
                <td>${producto.cantidad}</td>
                <td class="fw-bold">$${valorTotal.toLocaleString()}</td>
                <td>${estado}</td>
                <td class="text-center">
                    <button class="btn btn-warning btn-sm me-1" onclick="editarProducto(${producto.id})" title="Editar">
                        <i class="bi bi-pencil"></i>
                    </button>
                    <button class="btn btn-danger btn-sm" onclick="eliminarProducto(${producto.id})" title="Eliminar">
                        <i class="bi bi-trash"></i>
                    </button>
                </td>
            </tr>
        `;
    });

    const tg = document.getElementById("totalGeneral");
    if (tg) tg.textContent = "$" + totalGeneral.toLocaleString();
}

/* ============================================================
   RESUMEN DEL INVENTARIO
   ============================================================ */

function actualizarResumen() {
    const totalProductos = document.getElementById("totalProductos");
    const productosStock = document.getElementById("productosStock");
    const productosAgotados = document.getElementById("productosAgotados");
    const valorInventario = document.getElementById("valorInventario");

    if (!totalProductos) return;

    const agotados = productos.filter(p => p.cantidad === 0).length;
    const disponibles = productos.length - agotados;
    const total = productos.reduce((sum, p) => sum + (p.precio * p.cantidad), 0);

    totalProductos.textContent = productos.length;
    productosStock.textContent = disponibles;
    productosAgotados.textContent = agotados;
    valorInventario.textContent = "$" + total.toLocaleString();
}

/* ============================================================
   BUSCAR EN TIEMPO REAL
   ============================================================ */

function buscarEnTiempoReal() {
    const texto = document.getElementById("buscarProducto").value.toLowerCase();
    const categoria = document.getElementById("filtroCategoria").value;

    let filtrados = productos;

    if (texto) {
        filtrados = filtrados.filter(p =>
            String(p.id).includes(texto) ||
            p.codigo.toLowerCase().includes(texto) ||
            p.nombre.toLowerCase().includes(texto) ||
            (p.proveedor && p.proveedor.toLowerCase().includes(texto))
        );
    }

    if (categoria) {
        filtrados = filtrados.filter(p => p.categoria === categoria);
    }

    mostrarProductosEnTabla(filtrados);
}

/* ============================================================
   REGISTRAR / EDITAR PRODUCTO
   ============================================================ */

const formProducto = document.getElementById("formProducto");

if (formProducto) {
    formProducto.addEventListener("submit", async function(event) {
        event.preventDefault();

        const producto = {
            codigo: document.getElementById("codigo").value,
            nombre: document.getElementById("nombre").value,
            categoria: document.getElementById("categoria").value,
            proveedor: document.getElementById("proveedor").value,
            precio: parseFloat(document.getElementById("precio").value),
            cantidad: parseInt(document.getElementById("cantidad").value)
        };

        try {
            let respuesta;

            if (idEditando === null) {
                respuesta = await fetch(API_URL, {
                    method: "POST",
                    headers: { "Content-Type": "application/json" },
                    credentials: 'include',
                    body: JSON.stringify(producto)
                });
            } else {
                respuesta = await fetch(API_URL + "/" + idEditando, {
                    method: "PUT",
                    headers: { "Content-Type": "application/json" },
                    credentials: 'include',
                    body: JSON.stringify(producto)
                });
            }

            if (respuesta.ok) {
                mostrarMensaje(idEditando === null
                    ? "Producto registrado correctamente"
                    : "Producto actualizado correctamente", "success");
                formProducto.reset();
                idEditando = null;
                restablecerFormulario();
                await cargarProductos();
                setTimeout(() => mostrarSeccion('productos'), 1000);
            } else {
                mostrarMensaje("No fue posible guardar el producto", "danger");
            }
        } catch (error) {
            console.error("Error:", error);
            mostrarMensaje("No se pudo conectar con el servidor", "danger");
        }
    });
}

function editarProducto(id) {
    fetch(API_URL + "/" + id, { credentials: 'include' })
        .then(r => {
            if (r.status === 401) { window.location.href = '/login.html'; return null; }
            return r.json();
        })
        .then(producto => {
            if (!producto) return;
            document.getElementById("codigo").value = producto.codigo;
            document.getElementById("nombre").value = producto.nombre;
            document.getElementById("categoria").value = producto.categoria;
            document.getElementById("proveedor").value = producto.proveedor || '';
            document.getElementById("precio").value = producto.precio;
            document.getElementById("cantidad").value = producto.cantidad;

            idEditando = id;

            document.getElementById("tituloFormulario").innerHTML = '<i class="bi bi-pencil me-2"></i>Editar Producto';
            document.getElementById("btnGuardar").innerHTML = '<i class="bi bi-check-circle me-1"></i> Actualizar producto';
            document.getElementById("btnCancelar").style.display = "inline-block";

            mostrarSeccion('registrar');
        });
}

function cancelarEdicion() {
    idEditando = null;
    formProducto.reset();
    restablecerFormulario();
}

function restablecerFormulario() {
    document.getElementById("tituloFormulario").innerHTML = '<i class="bi bi-plus-circle me-2"></i>Registrar Nuevo Producto';
    document.getElementById("btnGuardar").innerHTML = '<i class="bi bi-check-circle me-1"></i> Guardar producto';
    document.getElementById("btnCancelar").style.display = "none";
}

/* ============================================================
   ELIMINAR PRODUCTO
   ============================================================ */

async function eliminarProducto(id) {
    if (!confirm("Esta seguro de eliminar este producto?")) return;

    try {
        const respuesta = await fetch(API_URL + "/" + id, { method: "DELETE", credentials: 'include' });
        if (respuesta.ok) {
            mostrarMensaje("Producto eliminado correctamente", "success");
            await cargarProductos();
        } else {
            mostrarMensaje("No fue posible eliminar el producto", "danger");
        }
    } catch (error) {
        console.error("Error:", error);
        mostrarMensaje("No se pudo conectar con el servidor", "danger");
    }
}

/* ============================================================
   EXPORTAR PDF / EXCEL (con cookie de sesion)
   ============================================================ */

async function exportarPDF() {
    try {
        const resp = await fetch('/exportar-pdf', { credentials: 'include' });
        if (resp.status === 401) { window.location.href = '/login.html'; return; }
        const blob = await resp.blob();
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = 'reporte_productos.pdf';
        document.body.appendChild(a);
        a.click();
        document.body.removeChild(a);
        window.URL.revokeObjectURL(url);
    } catch (e) {
        alert('Error al descargar PDF');
    }
}

async function exportarExcel() {
    try {
        const resp = await fetch('/exportar-excel', { credentials: 'include' });
        if (resp.status === 401) { window.location.href = '/login.html'; return; }
        const blob = await resp.blob();
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = 'productos.xlsx';
        document.body.appendChild(a);
        a.click();
        document.body.removeChild(a);
        window.URL.revokeObjectURL(url);
    } catch (e) {
        alert('Error al descargar Excel');
    }
}

/* ============================================================
   MENSAJES
   ============================================================ */

function mostrarMensaje(texto, tipo) {
    const mensajeDiv = document.getElementById("mensaje");
    if (!mensajeDiv) return;
    mensajeDiv.innerHTML = `
        <div class="alert alert-${tipo} alert-dismissible fade show" role="alert">
            <i class="bi bi-${tipo === 'success' ? 'check-circle' : 'exclamation-triangle'} me-2"></i>
            ${texto}
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        </div>
    `;
    setTimeout(() => { mensajeDiv.innerHTML = ''; }, 4000);
}
