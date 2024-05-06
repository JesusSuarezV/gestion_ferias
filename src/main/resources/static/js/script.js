//Despliegue del menu principal
let listElements = document.querySelectorAll('.lista-item');

listElements.forEach(listElement => {
    listElement.addEventListener('click', () => { //detecta el evento click en los items de la lista del menú
        listElement.classList.toggle('arrow'); //agrega la clase arrow
        let menu = listElement.nextElementSibling;
        menu.classList.toggle('active'); //agrega la clase active para mover el arrow   
    });
});

//ejecutar funcion con click
document.getElementById('btn-open').addEventListener("click", open_close_menu);

function closeNotification(notificationId) {
    var notification = document.getElementById(notificationId);
    notification.style.display = "none";}