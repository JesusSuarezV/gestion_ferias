package com.ufps.seminario.service;

import com.ufps.seminario.entity.Feria;
import com.ufps.seminario.entity.Version;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface VersionService {
    public void guardarVersion(Version version);

    public Page<Version> listarVersiones(Feria feria, Pageable pageable);

    public Version obtenerVersion(int id);

    public void ocultarVersion(int id);

}
