// =============================
// Conecta los botones de prueba de endpoints JSON
// =============================
$(function () {

    $(".btn-test[data-endpoint]").on("click", function () {
        const path = $(this).data("endpoint");
        const call = {
            "/api/mensaje/publico": API.publico,
            "/api/mensaje/privado": API.privado,
            "/api/mensaje/admin":   API.admin
        }[path];

        $("#testOutput").text("⏳ " + path + " …");
        call()
            .done(r => $("#testOutput").text("✅ " + path + "\n\n" + JSON.stringify(r, null, 2)))
            .fail(xhr => $("#testOutput").text("❌ " + path + "\n\n" + (xhr.responseJSON?.mensaje || xhr.responseText)));
    });
});
