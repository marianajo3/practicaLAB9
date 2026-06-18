// =============================
// Cliente AJAX para el backend (Basic Auth, per Clase 10.1 + Clase 11)
// =============================

const API = (() => {

    // Guarda credenciales + rol en sessionStorage (se borran al cerrar pestaña)
    function setCredentials(user, pass, roles) {
        const token = btoa(user + ":" + pass);    // base64(user:pass) para Basic Auth
        sessionStorage.setItem("auth",  token);
        sessionStorage.setItem("user",  user);
        sessionStorage.setItem("roles", JSON.stringify(roles || []));
    }
    function clearCredentials() {
        sessionStorage.removeItem("auth");
        sessionStorage.removeItem("user");
        sessionStorage.removeItem("roles");
    }
    function authHeader() {
        const t = sessionStorage.getItem("auth");
        return t ? { "Authorization": "Basic " + t } : {};
    }
    function currentUser() { return sessionStorage.getItem("user"); }
    function currentRoles() {
        try { return JSON.parse(sessionStorage.getItem("roles") || "[]"); }
        catch (e) { return []; }
    }
    function hasRole(role) { return currentRoles().includes("ROLE_" + role); }

    // Helper $.ajax con cabecera Basic Auth (Clase 11, slide 29-34)
    function ajax(opts) {
        const cfg = Object.assign({}, opts, {
            headers: Object.assign({}, opts.headers || {}, authHeader()),
            contentType: opts.contentType || "application/json",
            dataType: "json"
        });
        return $.ajax(cfg);
    }

    return {
        // ---- sesión ----
        login:  (user, pass) => setCredentials(user, pass),  // setCredentials + whoami
        me:     () => ajax({ url: "/api/mensaje/me", method: "GET" }),
        logout: () => clearCredentials(),
        user:   () => currentUser(),
        roles:  () => currentRoles(),
        isAdmin: () => hasRole("ADMIN"),
        isLogged: () => !!sessionStorage.getItem("auth"),

        // ---- mensajes ----
        publico: () => ajax({ url: "/api/mensaje/publico", method: "GET" }),
        privado: () => ajax({ url: "/api/mensaje/privado", method: "GET" }),
        admin:   () => ajax({ url: "/api/mensaje/admin",   method: "GET" }),

        // ---- productos (CRUD) ----
        listarProductos: (page = 0, size = 50) =>
            ajax({ url: `/api/productos?page=${page}&size=${size}&sort=id,asc`, method: "GET" }),

        obtenerProducto: (id) =>
            ajax({ url: `/api/productos/${id}`, method: "GET" }),

        crearProducto: (body) =>
            ajax({ url: "/api/productos", method: "POST", data: JSON.stringify(body) }),

        actualizarProducto: (id, body) =>
            ajax({ url: `/api/productos/${id}`, method: "PUT", data: JSON.stringify(body) }),

        eliminarProducto: (id) =>
            ajax({ url: `/api/productos/${id}`, method: "DELETE" })
    };
})();