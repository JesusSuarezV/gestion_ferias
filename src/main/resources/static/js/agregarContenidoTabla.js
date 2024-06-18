
document.addEventListener('DOMContentLoaded', function () {
    fetch('https://api.tuservidor.com/proyectos')  // URL de la API
        .then(response => response.json())
        .then(data => {
            const table = document.getElementById('projectsTable').getElementsByTagName('tbody')[0];
            data.forEach(project => {
                const newRow = table.insertRow();

                const idCell = newRow.insertCell(0);
                const nameCell = newRow.insertCell(1);
                const gradeCell = newRow.insertCell(2);

                idCell.textContent = project.id;
                nameCell.textContent = project.nombre;
                gradeCell.textContent = project.calificacion;
            });
        })
        .catch(error => {
            console.error('Error al obtener los proyectos:', error);
        });
});

