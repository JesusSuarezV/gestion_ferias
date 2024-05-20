const agregarIntegrante=document.getElementById('btnIntegrante');
const modal_container=document.getElementById('modal-container');
const cerrarVentana=document.getElementById('cancelarBtnAgregar');

agregarIntegrante.addEventListener('click', () => {
    modal_container.classList.add('show');
});

cerrarVentana.addEventListener('click',()=>{
    modal_container.classList.remove('show');
});