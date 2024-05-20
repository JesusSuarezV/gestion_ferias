// Modal
const modal = document.getElementById("myModal");
const btn = document.querySelector(".boton-agregar"); // Botón que abre el modal
const span = document.querySelector(".close"); // Elemento <span> que cierra el modal

btn.onclick = function() {
    modal.style.display = "block";
}

span.onclick = function() {
    modal.style.display = "none";
}

window.onclick = function(event) {
    if (event.target == modal) {
        modal.style.display = "none";
    }
}