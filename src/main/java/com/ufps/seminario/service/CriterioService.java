package com.ufps.seminario.service;

import com.ufps.seminario.entity.Criterio;
import com.ufps.seminario.entity.Version;

import java.util.List;

public interface CriterioService {
    public List<Criterio> obtenerCriterioPorVersion(Version version);

    public Criterio obtenerCriterioPorId(int idCriterio);

    public Criterio guardarCriterio(Criterio criterio);
}
