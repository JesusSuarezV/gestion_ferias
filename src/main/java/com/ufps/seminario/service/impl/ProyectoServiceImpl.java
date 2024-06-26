package com.ufps.seminario.service.impl;

import com.ufps.seminario.entity.Integrante;
import com.ufps.seminario.entity.Proyecto;
import com.ufps.seminario.entity.Usuario;
import com.ufps.seminario.entity.Version;
import com.ufps.seminario.repository.IntegranteRepository;
import com.ufps.seminario.repository.ProyectoRepository;
import com.ufps.seminario.service.IntegranteService;
import com.ufps.seminario.service.ProyectoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class ProyectoServiceImpl implements ProyectoService {

    @Autowired
    ProyectoRepository proyectoRepository;

    @Autowired
    IntegranteRepository integranteRepository;

    @Autowired
    IntegranteService integranteService;

    @Override
    public List<Proyecto> obtenerProyectosPorVersion(Version version) {
        return proyectoRepository.findByVersionAndEnabled(version, true);
    }

    @Override
    public Proyecto guardarProyecto(Proyecto proyecto) {
        return proyectoRepository.save(proyecto);
    }

    @Override
    public List<Proyecto> obtenerProyectosPorIds(List<Integer> ids) {
        return proyectoRepository.findByIdIn(ids);
    }

    @Override
    public List<Proyecto> obtenerProyectosActualesPorCorreo(String correo) {
        List<Integrante> integrantes_proyecto =  integranteRepository.findByCorreoRegistro(correo);
        List<Integer> ids = new ArrayList<>();
        for(Integrante integrante_proyecto: integrantes_proyecto){
            Version version = integrante_proyecto.getProyecto().getVersion();
            LocalDate now = LocalDate.now();
            if(integrante_proyecto.isEnabled()
                    && !(now.isAfter(version.getFechaCierre()) || version.isCierre())
                    && integrante_proyecto.getProyecto().isEnabled()){
                ids.add(integrante_proyecto.getProyecto().getId());
            }
        }
        return obtenerProyectosPorIds(ids);
    }

    @Override
    public List<Proyecto> obtenerProyectosPasadosPorCorreo(String correo) {
        List<Integrante> integrantes_proyecto =  integranteRepository.findByCorreoRegistro(correo);
        List<Integer> ids = new ArrayList<>();
        for(Integrante integrante_proyecto: integrantes_proyecto){
            Version version = integrante_proyecto.getProyecto().getVersion();
            LocalDate now = LocalDate.now();
            if(integrante_proyecto.isEnabled() && (now.isAfter(version.getFechaCierre()) || version.isCierre())  && integrante_proyecto.getProyecto().isEnabled()){
                ids.add(integrante_proyecto.getProyecto().getId());
            }
        }
        return obtenerProyectosPorIds(ids);
    }

    @Override
    public Proyecto obtenerProyectoPorId(int idProyecto) {
        return proyectoRepository.getReferenceById(idProyecto);
    }

    @Override
    public List<Proyecto> obtenerProyectosPorCorreoOrdenadosPorFechaRegistro(String correo) {
        List<Integrante> integrantes_proyecto =  integranteRepository.findByCorreoRegistro(correo);
        List<Integer> ids = new ArrayList<>();
        for(Integrante integrante_proyecto: integrantes_proyecto){
            if(integrante_proyecto.getProyecto().isEnabled() && integrante_proyecto.isEnabled())
                ids.add(integrante_proyecto.getProyecto().getId());
        }
        return obtenerProyectosPorIdsOrdenadoPorFechaRegistro(ids);
    }

    @Override
    public List<Proyecto> obtenerProyectosPorIdsOrdenadoPorFechaRegistro(List<Integer> ids) {
        return proyectoRepository.findByIdInOrderByFechaRegistroDesc(ids);
    }

    @Override
    public List<Proyecto> obtenerProyectoPorJurado(Usuario usuario) {
        return List.of();
    }

    @Override
    public List<Proyecto> obtenerProyectosPorVersionYPalabra(Version version, String keyword) {
        List<Proyecto> proy = obtenerProyectosPorVersion(version);
        List<Proyecto> proyectos = new ArrayList<>();
        for(Proyecto proyecto: proy){
            if(keyword == null || keyword.isEmpty() || proyecto.getNombre().contains(keyword)){
                proyectos.add(proyecto);
            }
        }
        return proyectos;
    }

    @Override
    public List<Proyecto> obtenerProyectosAprobados(Version version) {
        return proyectoRepository.findProyectosAprobados(version);
    }

    @Override
    public List<Proyecto> obtenerProyectosReprobados(Version version) {
        return proyectoRepository.findProyectosReprobados(version);
    }

    @Override
    public void eliminarProyecto(Proyecto proyecto) {
        for(Integrante integrante: integranteRepository.findByProyecto(proyecto)){
            integranteService.eliminarIntegrante(integrante);
        }
        proyecto.setEnabled(false);
        proyectoRepository.save(proyecto);
    }

    @Override
    public void eliminarProyecto(int id) {
        Proyecto proyecto = proyectoRepository.getReferenceById(id);
        eliminarProyecto(proyecto);
    }
}
