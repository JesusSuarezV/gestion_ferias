package com.ufps.seminario.service;

import com.ufps.seminario.entity.Proyecto;
import com.ufps.seminario.entity.Usuario;
import com.ufps.seminario.entity.Version;

import java.util.List;

public interface ProyectoService {
    public List<Proyecto> obtenerProyectosPorVersion(Version version);
    public Proyecto guardarProyecto(Proyecto proyecto);
    public List<Proyecto> obtenerProyectosPorIds(List<Integer> ids);

    public List<Proyecto> obtenerProyectosActualesPorCorreo(String correo);
    public List<Proyecto> obtenerProyectosPasadosPorCorreo(String correo);
    public Proyecto obtenerProyectoPorId(int idProyecto);
    public List<Proyecto> obtenerProyectosPorCorreoOrdenadosPorFechaRegistro(String correo);

    public List<Proyecto> obtenerProyectosPorIdsOrdenadoPorFechaRegistro(List<Integer> ids);
    public List<Proyecto> obtenerProyectoPorJurado(Usuario usuario);

    public List<Proyecto> obtenerProyectosPorVersionYPalabra(Version version, String keyword);

    void eliminarProyecto(Proyecto proyecto);

    void eliminarProyecto(int id);
}
