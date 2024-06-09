package com.ufps.seminario.service;

import com.ufps.seminario.entity.Integrante;
import com.ufps.seminario.entity.Proyecto;

import java.util.List;

public interface IntegranteService {
    public Integrante guardarIntegrante(Integrante integrante);
    public List<Integrante> obtenerIntegrantePorProyecto(Proyecto proyecto);
    public Integrante obtenerIntegrantePorId(int id);
    public void eliminarIntegrante(Integrante integrante);
    public boolean esIntegrante(Proyecto proyecto, String username);
}
