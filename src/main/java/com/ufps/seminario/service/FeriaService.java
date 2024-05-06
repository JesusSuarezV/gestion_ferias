package com.ufps.seminario.service;

import com.ufps.seminario.entity.Feria;
import com.ufps.seminario.entity.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface FeriaService {
    public void guardarFeria(Feria feria);

    public Feria obtenerFeria(int id);

    public Page listarMisFerias(Usuario usuario, String nombre, Pageable pageable);

    public void ocultarFeria(int id);
}
