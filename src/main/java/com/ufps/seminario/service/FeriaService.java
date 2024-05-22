package com.ufps.seminario.service;

import com.ufps.seminario.entity.Feria;
import com.ufps.seminario.entity.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface FeriaService {
    public Feria guardarFeria(Feria feria);

    public Feria obtenerFeria(int id);

    public Page<Feria> listarMisFerias(Usuario usuario, String keyword, int pageNumber, int pageSize);

    public void ocultarFeria(int id);

    public Feria obtenerFeriaByVersion(int idVersion);
}
