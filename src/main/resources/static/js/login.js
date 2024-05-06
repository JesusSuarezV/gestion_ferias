const container = document.getElementById('contenedor');
const registerBtn = document.getElementById('register');
const loginBtn = document.getElementById('login');
const registerIndex = document.getElementById('registerIndex');

registerBtn.addEventListener('click', () => {
    container.classList.add("active");
});

loginBtn.addEventListener('click', () => {
    container.classList.remove("active");
});

function closeNotification(notificationId) {
    var notification = document.getElementById(notificationId);
    notification.style.display = "none";}

