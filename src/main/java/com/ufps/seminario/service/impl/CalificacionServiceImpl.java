package com.ufps.seminario.service.impl;

import com.ufps.seminario.entity.Calificacion;
import com.ufps.seminario.entity.Criterio;
import com.ufps.seminario.entity.Proyecto;
import com.ufps.seminario.entity.Usuario;
import com.ufps.seminario.repository.CalificacionRepository;
import com.ufps.seminario.service.CalificacionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CalificacionServiceImpl implements CalificacionService {

    @Autowired
    CalificacionRepository calificacionRepository;

    @Override
    public Calificacion guardarCalificacion(Calificacion calificacion) {
        calificacionRepository.save(calificacion);
        return calificacion;
    }

    @Override
    public List<Calificacion> obtenerCalificaciones(Proyecto proyecto, Usuario jurado) {
        return calificacionRepository.findByProyectoAndJurado(proyecto, jurado);
    }

    @Override
    public Calificacion obtenerCalificacion(Proyecto proyecto, Usuario jurado, Criterio criterio) {
        return calificacionRepository.findByProyectoAndJuradoAndCriterio(proyecto, jurado, criterio);
    }

    @Override
    public int obtenerCalificacionCriterio(Proyecto proyecto, Usuario jurado, Criterio criterio) {

        System.out.println(proyecto.getNombre());
        System.out.println(jurado.getUsername());
        System.out.println(criterio.getNombre());

        Calificacion calificacion = calificacionRepository.findByProyectoAndJuradoAndCriterio(proyecto, jurado, criterio);
        if (calificacion != null) return calificacion.getValor();
        return 0;
    }
}
