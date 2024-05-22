package com.ufps.seminario.service.impl;

import com.ufps.seminario.entity.Integrante;
import com.ufps.seminario.entity.Proyecto;
import com.ufps.seminario.repository.IntegranteRepository;
import com.ufps.seminario.service.IntegranteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class IntegranteServiceImpl implements IntegranteService {

    @Autowired
    IntegranteRepository integranteRepository;

    @Override
    public Integrante guardarIntegrante(Integrante integrante) {
        return integranteRepository.save(integrante);
    }

    @Override
    public List<Integrante> obtenerIntegrantePorProyecto(Proyecto proyecto) {
        return integranteRepository.findByProyecto(proyecto);
    }

    @Override
    public Integrante obtenerIntegrantePorId(int id) {
        return integranteRepository.getReferenceById(id);
    }

    @Override
    public void eliminarIntegrante(Integrante integrante) {
        integranteRepository.delete(integrante);
    }
}
