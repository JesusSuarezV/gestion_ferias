document.addEventListener("DOMContentLoaded", function() {
    const itemsPerPage = 2;
    const ferias = document.querySelectorAll('.rectangulo');
    const totalPages = Math.ceil(ferias.length / itemsPerPage);
    const pageNumbers = document.getElementById('pageNumbers');
    let currentPage = 1;

    function showPage(page) {
        currentPage = page;
        const start = (page - 1) * itemsPerPage;
        const end = start + itemsPerPage;

        ferias.forEach((feria, index) => {
            if (index >= start && index < end) {
                feria.style.display = ''; // Use the default display style
            } else {
                feria.style.display = 'none';
            }
        });

        updatePagination();
    }

    function updatePagination() {
        pageNumbers.innerHTML = '';

        if (currentPage > 1) {
            document.getElementById('firstPage').style.display = 'inline';
            document.getElementById('prevPage').style.display = 'inline';
        } else {
            document.getElementById('firstPage').style.display = 'none';
            document.getElementById('prevPage').style.display = 'none';
        }

        if (currentPage < totalPages) {
            document.getElementById('nextPage').style.display = 'inline';
            document.getElementById('lastPage').style.display = 'inline';
        } else {
            document.getElementById('nextPage').style.display = 'none';
            document.getElementById('lastPage').style.display = 'none';
        }

        for (let i = 1; i <= totalPages; i++) {
            const pageNumber = document.createElement('span');
            pageNumber.className = 'page-number';
            pageNumber.innerText = i;
            pageNumber.addEventListener('click', function() {
                showPage(i);
            });

            if (i === currentPage) {
                pageNumber.classList.add('active');
            }

            pageNumbers.appendChild(pageNumber);
        }
    }

    document.getElementById('firstPage').addEventListener('click', function() {
        showPage(1);
    });

    document.getElementById('prevPage').addEventListener('click', function() {
        if (currentPage > 1) {
            showPage(currentPage - 1);
        }
    });

    document.getElementById('nextPage').addEventListener('click', function() {
        if (currentPage < totalPages) {
            showPage(currentPage + 1);
        }
    });

    document.getElementById('lastPage').addEventListener('click', function() {
        showPage(totalPages);
    });

    // Inicializar la primera página
    showPage(1);
});
