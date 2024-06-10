package com.ufps.seminario.service.impl;

import com.ufps.seminario.entity.Integrante;
import com.ufps.seminario.entity.Proyecto;
import com.ufps.seminario.entity.TokenIntegrante;
import com.ufps.seminario.repository.IntegranteRepository;
import com.ufps.seminario.service.IntegranteService;
import com.ufps.seminario.service.TokenIntegranteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.Local;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class IntegranteServiceImpl implements IntegranteService {

    @Autowired
    IntegranteRepository integranteRepository;

    @Autowired
    TokenIntegranteService tokenIntegranteService;

    @Override
    public Integrante guardarIntegrante(Integrante integrante) {
        return integranteRepository.save(integrante);
    }

    @Override
    public List<Integrante> obtenerIntegrantePorProyecto(Proyecto proyecto) {
        return integranteRepository.findByProyectoAndEnabled(proyecto, true);
    }

    @Override
    public Integrante obtenerIntegrantePorId(int id) {
        return integranteRepository.getReferenceById(id);
    }

    @Override
    public void eliminarIntegrante(Integrante integrante) {
        integranteRepository.delete(integrante);
    }

    @Override
    public boolean esIntegrante(Proyecto proyecto, String username) {
        List<Integrante> integrantes = this.obtenerIntegrantePorProyecto(proyecto);
        boolean esIntegrante = false;
        for(Integrante integrante: integrantes){
            esIntegrante = esIntegrante || integrante.getCorreoRegistro().equals(username);
        }
        return esIntegrante;
    }


}
