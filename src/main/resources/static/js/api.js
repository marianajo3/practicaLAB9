// =============================
// Sin auth/token (el pom actual no incluye Spring Security)
// =============================
const API = (() => {

    function get(path) {
        return $.ajax({
            url: path,
            method: "GET",
            contentType: "application/json"
        });
    }

    return {
        publico: () => get("/api/mensaje/publico"),
        privado: () => get("/api/mensaje/privado"),
        admin:   () => get("/api/mensaje/admin")
    };
})();
