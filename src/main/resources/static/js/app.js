// =============================
// UI: login + prueba de endpoints + CRUD de productos
// - Una sola pantalla de login (no hay popup del navegador porque las
//   páginas estáticas son públicas; solo /api/** requiere auth).
// - El listado y el form "Crear producto" aparecen solo después de loguearse.
// - "Crear producto" se muestra únicamente si el rol es ADMIN.
// Patrones: $.ajax con done/fail (Clase 11), .on() con delegación (Clase 10.3)
// =============================

$(function () {

    // ---------- Mostrar / ocultar secciones según sesión ----------
    function refreshSession() {
        if (API.isLogged()) {
            // hay sesión → muestro nombre, logout y sección de productos
            $("#sessionInfo").removeClass("hidden").text("👤 " + API.user() + " (" + API.roles().join(", ") + ")");
            $("#btnLogout").removeClass("hidden");
            $("#loginCard").addClass("hidden");
            $("#productosCard").removeClass("hidden");

            // Crear producto solo para ADMIN
            if (API.isAdmin()) {
                $("#crearProductoCard").removeClass("hidden");
                $("#adminOnlyHint").removeClass("hidden");
            } else {
                $("#crearProductoCard").addClass("hidden");
                $("#adminOnlyHint").addClass("hidden");
            }
        } else {
            // no hay sesión → muestro solo el form de login
            $("#sessionInfo").addClass("hidden").text("");
            $("#btnLogout").addClass("hidden");
            $("#loginCard").removeClass("hidden");
            $("#productosCard").addClass("hidden");
            $("#crearProductoCard").addClass("hidden");
            $("#adminOnlyHint").addClass("hidden");
        }
    }

    // ---------- 1) Login (Basic Auth vía header) ----------
    $("#btnLogin").on("click", function () {
        const u = $("#loginUser").val().trim();
        const p = $("#loginPass").val().trim();
        if (!u || !p) { alert("Ingresa usuario y contraseña"); return; }

        // 1) guardo credenciales
        API.login(u, p);
        // 2) consulto /api/me para saber el rol y validar credenciales
        API.me()
            .done(function (data) {
                API.login(data.username, p, data.roles);   // actualizo roles
                $("#loginMsg").text("✅ Bienvenido " + data.username);
                refreshSession();
                cargarProductos();
            })
            .fail(function (xhr) {
                API.logout();
                $("#loginMsg").text("❌ Credenciales inválidas (" + xhr.status + ")");
                refreshSession();
            });
    });

    // Enter en el campo password también dispara login
    $("#loginPass").on("keypress", function (e) { if (e.which === 13) $("#btnLogin").click(); });

    // ---------- 2) Logout (borra sessionStorage) ----------
    $("#btnLogout").on("click", function () {
        API.logout();
        $("#loginUser,#loginPass").val("");
        $("#loginMsg").text("");
        $("#productosTable tbody").html("");
        $("#testOutput").text("— las respuestas aparecerán aquí —");
        refreshSession();
    });

    // ---------- 3) Botones de prueba de endpoints ----------
    $(".btn-test[data-endpoint]").on("click", function () {
        const path = $(this).data("endpoint");
        const call = {
            "/api/mensaje/publico": API.publico,
            "/api/mensaje/privado": API.privado,
            "/api/mensaje/admin":   API.admin
        }[path];

        $("#testOutput").text("⏳ " + path + " …");
        call()
            .done(r  => $("#testOutput").text("✅ " + path + "\n\n" + JSON.stringify(r, null, 2)))
            .fail(xhr => {
                if (xhr.status === 401) $("#testOutput").text("🔒 " + path + " → no autenticado (iniciá sesión)");
                else $("#testOutput").text("❌ " + path + " (" + xhr.status + ")\n\n" + (xhr.responseJSON?.mensaje || xhr.responseText || "Error"));
            });
    });

    // ---------- 4) CRUD de productos (consume REST API) ----------
    // USER → solo ve el listado (columna "Acciones" queda vacía)
    // ADMIN → ve Editar + Eliminar
    function renderProductos(pageData) {
        const isAdmin = API.isAdmin();
        const acciones = (p) => isAdmin
            ? `<button class="btn-secondary btn-sm" data-edit='${JSON.stringify(p)}'>Editar</button>
               <button class="btn-del"               data-del="${p.id}">Eliminar</button>`
            : `<span class="muted small">solo lectura</span>`;

        const rows = pageData.content.map(p => `
            <tr>
                <td>${p.id}</td>
                <td>${p.nombre}</td>
                <td>S/ ${p.precio.toFixed(2)}</td>
                <td>${p.stock}</td>
                <td>${acciones(p)}</td>
            </tr>
        `).join("");
        $("#productosTable tbody").html(rows || `<tr><td colspan="5" class="muted">No hay productos.</td></tr>`);
    }

    function cargarProductos() {
        if (!API.isLogged()) return;
        API.listarProductos()
            .done(renderProductos)
            .fail(xhr => $("#productosTable tbody").html(
                `<tr><td colspan="5" class="muted">Error listando productos (${xhr.status})</td></tr>`));
    }

    // delegación .on() para botones agregados dinámicamente (Clase 10.3 slide 13)
    $("#productosTable").on("click", "button[data-del]", function () {
        const id = $(this).data("del");
        if (!confirm("¿Eliminar producto #" + id + "?")) return;
        API.eliminarProducto(id)
            .done(() => cargarProductos())
            .fail(xhr => alert("No se pudo eliminar (" + xhr.status + "). Solo ADMIN."));
    });

    // Editar producto: pide los nuevos valores y manda PUT (solo ADMIN)
    $("#productosTable").on("click", "button[data-edit]", function () {
        const p = $(this).data("edit");               // {id, nombre, precio, stock}
        const nombre = prompt("Nuevo nombre:", p.nombre);
        if (nombre === null) return;                  // canceló
        const precio = parseFloat(prompt("Nuevo precio:", p.precio));
        const stock  = parseInt(prompt("Nuevo stock:", p.stock));
        if (!nombre || isNaN(precio) || isNaN(stock)) {
            alert("Datos inválidos"); return;
        }
        API.actualizarProducto(p.id, { nombre, precio, stock })
            .done(() => cargarProductos())
            .fail(xhr => alert("No se pudo editar (" + xhr.status + "). Solo ADMIN."));
    });

    // crear producto desde el form
    $("#btnCrearProducto").on("click", function (e) {
        e.preventDefault();
        const body = {
            nombre: $("#pNombre").val().trim(),
            precio: parseFloat($("#pPrecio").val()),
            stock:  parseInt($("#pStock").val())
        };
        if (!body.nombre || isNaN(body.precio) || isNaN(body.stock)) {
            alert("Completa todos los campos"); return;
        }
        API.crearProducto(body)
            .done(() => {
                $("#pNombre,#pPrecio,#pStock").val("");
                cargarProductos();
            })
            .fail(xhr => alert("No se pudo crear (" + xhr.status + ")."));
    });

    // ---------- init ----------
    refreshSession();
    if (API.isLogged()) {
        // Si ya hay sesión en sessionStorage, sincronizo roles con el server
        // (por si refrescaste la página o iniciaste sesión en otra pestaña)
        API.me()
            .done(data => {
                // reconstruyo pass desde el base64 solo para reusar API.login (no se guarda)
                const token = sessionStorage.getItem("auth") || "";
                const pass  = atob(token).split(":")[1] || "";
                API.login(data.username, pass, data.roles);
                refreshSession();
                cargarProductos();
            })
            .fail(() => {                       // token expirado o inválido
                API.logout();
                refreshSession();
            });
    }
});