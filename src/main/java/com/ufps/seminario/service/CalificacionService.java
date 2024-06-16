package com.ufps.seminario.service;

import com.ufps.seminario.entity.Calificacion;
import com.ufps.seminario.entity.Criterio;
import com.ufps.seminario.entity.Proyecto;
import com.ufps.seminario.entity.Usuario;

import java.util.List;

public interface CalificacionService {

    public Calificacion guardarCalificacion(Calificacion calificacion);

    public List<Calificacion> obtenerCalificaciones(Proyecto proyecto, Usuario jurado);

    public Calificacion obtenerCalificacion(Proyecto proyecto, Usuario jurado, Criterio criterio);

    public int obtenerCalificacionCriterio(Proyecto proyecto, Usuario jurado, Criterio criterio);

}
