package com.ufps.seminario.service.impl;

import com.ufps.seminario.repository.CriterioRepository;
import com.ufps.seminario.service.CriterioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ufps.seminario.entity.Criterio;
import com.ufps.seminario.entity.Version;

import java.util.List;

@Service
public class CriterioServiceImpl implements CriterioService {
    @Autowired
    CriterioRepository criterioRepository;
    public List<Criterio> obtenerCriterioPorVersion(Version version){
        return criterioRepository.findByVersionAndEnabled(version, true);
    }

    @Override
    public Criterio obtenerCriterioPorId(int idCriterio) {
        return criterioRepository.getReferenceById(idCriterio);
    }

    @Override
    public Criterio guardarCriterio(Criterio criterio) {
        return criterioRepository.save(criterio);
    }
}
