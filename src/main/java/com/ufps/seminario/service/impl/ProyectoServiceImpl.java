package com.ufps.seminario.service.impl;

import com.ufps.seminario.entity.Integrante;
import com.ufps.seminario.entity.Proyecto;
import com.ufps.seminario.entity.Usuario;
import com.ufps.seminario.entity.Version;
import com.ufps.seminario.repository.IntegranteRepository;
import com.ufps.seminario.repository.ProyectoRepository;
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

    @Override
    public List<Proyecto> obtenerProyectosPorVersion(Version version) {
        return proyectoRepository.findByVersion(version);
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
            LocalDate fechaCierre = integrante_proyecto.getProyecto().getVersion().getFechaCierre();
            LocalDate now = LocalDate.now();
            if(!now.isAfter(fechaCierre)){
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
            LocalDate fechaCierre = integrante_proyecto.getProyecto().getVersion().getFechaCierre();
            LocalDate now = LocalDate.now();
            if(now.isAfter(fechaCierre)){
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
}
