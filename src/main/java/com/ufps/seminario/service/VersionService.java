package com.ufps.seminario.service;

import com.ufps.seminario.entity.Feria;
import com.ufps.seminario.entity.Usuario;
import com.ufps.seminario.entity.Version;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

public interface VersionService {

    public Page<Version> listarVersiones(Feria feria, Pageable pageable);

    public Version obtenerVersion(int id);

    public void ocultarVersion(int id);

    public Version guardarVersion(Version version);

    public List<Version> obtenerVersionesPorIntegranteYDisponibles(String correo, String keyword, LocalDate fecha);

    public List<Version> obtenerVersionesPorIntegranteYCerradas(String correo, String keyword, LocalDate fecha);

    List<Version> obtenerVersionesCerradas(String correo, LocalDate fecha);

    public List<Version> obtenerVersionesPorJurado(Usuario usuario);

    public List<Version> obtenerVersionesPorFeria(Feria feria);

    public int obtenerCantidadDisponiblePorFeriayFecha(Feria feria, LocalDate now);

    List<Version> obtenerVersionesDisponibles(String keyword, LocalDate now);

    public boolean estaCerrado(int idVersion);
}
