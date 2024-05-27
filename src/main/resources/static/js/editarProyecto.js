   let areaIndex = 0;
    function agregarArea() {
        let select = document.getElementById('areasSelect');
        let selectedOption = select.options[select.selectedIndex];
        let id = selectedOption.value;
        let nombre = selectedOption.text;

        let selectedAreas = document.getElementById('areasSeleccionadas');
        let newArea = document.createElement('li');
        newArea.textContent = nombre;
        newArea.id = "area["+id+"]";

        // Crear un botón para remover el área
        let removeButton = document.createElement('button');
        removeButton.textContent = 'X';
        removeButton.type = 'button';
        removeButton.class = 'btnArea';
        removeButton.onclick = function() {
            selectedAreas.removeChild(newArea);
        };

        newArea.appendChild(removeButton);
        selectedAreas.appendChild(newArea);

        // Agregar un campo oculto para enviar el ID al backend
        let hiddenInput = document.createElement('input');
        hiddenInput.type = 'hidden';
        hiddenInput.name = "area["+id+"]";
        hiddenInput.value = "area["+id+"]";
        newArea.appendChild(hiddenInput);
    }
    function addIntegrante() {
        const areasDiv = document.getElementById('integrantes');
        const newAreaDiv = document.createElement('tr');
        newAreaDiv.id = "integrante["+areaIndex+"]";
        newAreaDiv.innerHTML = `
            <td>
                <input type="text" name="integrante[${areaIndex}]">
                <button type="button" class="btnClear" onclick="eliminarComponente2('integrante[${areaIndex}]')">
                    X
                </button><br>
            </td>
        `;
        areasDiv.appendChild(newAreaDiv);
        areaIndex++;
    }
    function eliminarComponente(id) {
        var componente = document.getElementById('['+id+']');
        if (componente) {
            componente.remove();
        }
        const areasDiv = document.getElementById('areasEliminadas');
        const newAreaDiv = document.createElement('tr');
        newAreaDiv.id = "feria_area_delete[${id}]";
        newAreaDiv.innerHTML = `
            <td>
                <input type="hidden" name="feria_area_delete[${id}]" id="feria_area_delete[${id}]">
            </td>
        `;
        areasDiv.appendChild(newAreaDiv);
    }
    function eliminarIntegrante(id) {
        var componente = document.getElementById('integrante_back['+id+']');
        if (componente) {
            componente.remove();
        }
        const areasDiv = document.getElementById('integrantesEliminados');
        const newAreaDiv = document.createElement('tr');
        newAreaDiv.id = "proyecto_integrante_delete[${id}]";
        newAreaDiv.innerHTML = `
            <td>
                <input type="hidden" name="proyecto_integrante_delete[${id}]" id="proyecto_integrante_delete[${id}]">
            </td>
        `;
        areasDiv.appendChild(newAreaDiv);
    }

    function eliminarArea(id) {
        var componente = document.getElementById('area_back['+id+']');
        if (componente) {
            componente.remove();
        }
        const areasDiv = document.getElementById('areasEliminadas');
        const newAreaDiv = document.createElement('tr');
        newAreaDiv.id = "proyecto_area_delete[${id}]";
        newAreaDiv.innerHTML = `
            <td>
                <input type="hidden" name="proyecto_area_delete[${id}]" id="proyecto_area_delete[${id}]">
            </td>
        `;
        areasDiv.appendChild(newAreaDiv);
    }

    function eliminarComponente2(id) {
        var componente = document.getElementById(id);
        if (componente) {
            componente.remove();
        }
    }