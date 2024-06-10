let paginaActual = 1;
const elementosPorPagina = 5; 
const items = document.querySelectorAll('.item_lista');
const paginacionContainer = document.getElementById('pagination');

function mostrarPagina(pagina) {
    const inicio = (pagina - 1) * elementosPorPagina;
    const fin = pagina * elementosPorPagina;

    items.forEach((item, indice) => {
        item.style.display = (indice >= inicio && indice < fin) ? 'flex' : 'none';
    });

    actualizarPaginacion(pagina);
}

function actualizarPaginacion(pagina) {
    paginacionContainer.innerHTML = '';
    const totalPaginas = Math.ceil(items.length / elementosPorPagina);

    if (totalPaginas <= 1) return;

    if (pagina > 1) {
        const firstButton = document.createElement('button');
        firstButton.textContent = '<<';
        firstButton.addEventListener('click', () => cambiarPagina(1));
        paginacionContainer.appendChild(firstButton);

        const prevButton = document.createElement('button');
        prevButton.textContent = '<';
        prevButton.addEventListener('click', () => cambiarPagina(pagina - 1));
        paginacionContainer.appendChild(prevButton);
    }

    for (let i = 1; i <= totalPaginas; i++) {
        const boton = document.createElement('button');
        boton.textContent = i;
        boton.addEventListener('click', () => cambiarPagina(i));
        if (i === pagina) {
            boton.classList.add('active');
        }
        paginacionContainer.appendChild(boton);
    }

    if (pagina < totalPaginas) {
        const nextButton = document.createElement('button');
        nextButton.textContent = '>';
        nextButton.addEventListener('click', () => cambiarPagina(pagina + 1));
        paginacionContainer.appendChild(nextButton);

        const lastButton = document.createElement('button');
        lastButton.textContent = '>>';
        lastButton.addEventListener('click', () => cambiarPagina(totalPaginas));
        paginacionContainer.appendChild(lastButton);
    }
}

function cambiarPagina(pagina) {
    paginaActual = pagina;
    mostrarPagina(paginaActual);
}

// Inicializar paginación y mostrar la primera página
document.addEventListener('DOMContentLoaded', () => {
    cambiarPagina(paginaActual);
});
