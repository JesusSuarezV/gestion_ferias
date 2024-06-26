package com.ufps.seminario.service.impl;

import com.ufps.seminario.entity.*;
import com.ufps.seminario.repository.IntegranteRepository;
import com.ufps.seminario.repository.ProyectoRepository;
import com.ufps.seminario.repository.VersionRepository;
import com.ufps.seminario.service.ProyectoService;
import com.ufps.seminario.service.VersionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class VersionServiceImpl implements VersionService {
    @Autowired
    VersionRepository versionRepository;
    @Autowired
    IntegranteRepository integranteRepository;
    @Autowired
    ProyectoRepository proyectoRepository;
    @Autowired
    ProyectoService proyectoService;

    @Override
    public Version guardarVersion(Version version) {
        version.setEnabled(true);
        return versionRepository.save(version);
    }

    @Override
    public List<Version> obtenerVersionesPorIntegranteYDisponibles(String correo, String keyword, LocalDate fecha) {

        List<Integrante> inscripciones = integranteRepository.findByCorreoRegistro(correo);

        List<Integer> ids = new ArrayList<>();
        for(Integrante integranteLista: inscripciones){
            String nombre = integranteLista.getProyecto().getVersion().getFeria().getNombre();
            LocalDate fechaCierre = integranteLista.getProyecto().getVersion().getFechaCierre();
            boolean cerrada = integranteLista.getProyecto().getVersion().isCierre();
            if((keyword == null || keyword.isEmpty()  || nombre.toLowerCase().contains(keyword.toLowerCase()))
                    && !fecha.isAfter(fechaCierre)
                    && !cerrada
                    && integranteLista.getProyecto().getVersion().getFeria().isEnabled()
                    && integranteLista.isEnabled()
                    && integranteLista.getProyecto().getVersion().isEnabled()
                    && integranteLista.getProyecto().isEnabled()){
                ids.add(integranteLista.getProyecto().getVersion().getId());
            }
        }

        return versionRepository.findByIdIn(ids);
    }

    @Override
    public List<Version> obtenerVersionesPorIntegranteYCerradas(String correo, String keyword, LocalDate fecha) {

        List<Integrante> inscripciones = integranteRepository.findByCorreoRegistro(correo);
        List<Integer> ids = new ArrayList<>();

        for(Integrante integranteLista: inscripciones){
            String nombre = integranteLista.getProyecto().getVersion().getFeria().getNombre();
            LocalDate fechaCierre = integranteLista.getProyecto().getVersion().getFechaCierre();
            boolean cerrada = integranteLista.getProyecto().getVersion().isCierre();
            if((keyword == null || keyword.isEmpty()  || nombre.toLowerCase().contains(keyword.toLowerCase()))
                    && (fecha.isAfter(fechaCierre) || cerrada)
                    && integranteLista.getProyecto().getVersion().getFeria().isEnabled()
                    && integranteLista.isEnabled()
                    && integranteLista.getProyecto().getVersion().isEnabled()
                    && integranteLista.getProyecto().isEnabled()){
                ids.add(integranteLista.getProyecto().getVersion().getId());
            }
        }

        return versionRepository.findByIdIn(ids);
    }

    @Override
    public List<Version> obtenerVersionesCerradas(String keyword, LocalDate fecha) {

        List<Version> versiones = versionRepository.findByCierreOrFechaCierreBefore(true, fecha);
        List<Version> versionesTerminadas = new ArrayList<>();
        for(Version version: versiones){
            String nombre = version.getFeria().getNombre();
            if((keyword == null || keyword.isEmpty()  || nombre.toLowerCase().contains(keyword.toLowerCase()) && version.isEnabled())
              && version.getFeria().isEnabled()
              && version.isEnabled()){
                versionesTerminadas.add(version);
            }
        }
        return versionesTerminadas;
    }

    @Override
    public List<Version> obtenerVersionesPorJurado(Usuario usuario) {
        List<Integer> ids = new ArrayList<>();
        for(Proyecto proyecto: usuario.getProyectosCalificar()){
            if(!this.estaCerrado(proyecto.getVersion()) && proyecto.isEnabled()){
                ids.add(proyecto.getVersion().getId());
            }
        }
        return versionRepository.findByIdIn(ids);
    }

    @Override
    public List<Version> obtenerVersionesPorFeria(Feria feria) {
        return versionRepository.findByFeriaAndEnabledOrderByFechaInicioDesc(feria, true);
    }

    @Override
    public int obtenerCantidadDisponiblePorFeriayFecha(Feria feria, LocalDate now) {
        return versionRepository.findByFeriaAndCierreAndFechaCierreAfter(feria, false, now).size();
    }

    @Override
    public List<Version> obtenerVersionesDisponibles(String keyword, LocalDate now) {
        List<Version> versiones = versionRepository.findAll();
        List<Version> versionesDisponibles = new ArrayList<>();
        for(Version version: versiones){
            LocalDate fechaLimite = version.getFechaLimite();
            String nombre = version.getFeria().getNombre();
            boolean cerrada = version.isCierre();
            if((keyword == null || keyword.isEmpty()  || nombre.toLowerCase().contains(keyword.toLowerCase()))
                    && (!now.isAfter(fechaLimite) && !cerrada)
                    && version.getFeria().isEnabled()
                    && version.isEnabled()){
                versionesDisponibles.add(version);
            }
        }
        return versionesDisponibles;
    }

    @Override
    public boolean estaCerrado(int idVersion) {
        Version version = this.obtenerVersion(idVersion);
        return LocalDate.now().isAfter(version.getFechaCierre()) || version.isCierre();
    }



    @Override
    public boolean estaCerrado(Version version) {
        return LocalDate.now().isAfter(version.getFechaCierre())
                || version.isCierre()
                || !version.getFeria().isEnabled()
                || !version.isEnabled();
    }

    @Override
    public Page<Version> listarVersiones(Feria feria, Pageable pageable) {
        return versionRepository.findByFeriaAndEnabledTrueOrderByFechaInicioDescNumeroAsc(feria, pageable);
    }

    @Override
    public Version obtenerVersion(int id) {
        return versionRepository.getReferenceById(id);
    }

    @Override
    public void ocultarVersion(int id) {
        Version version = versionRepository.getReferenceById(id);
        ocultarVersion(version);
    }

    @Override
    public void ocultarVersion(Version version) {
        for(Proyecto proyecto : proyectoRepository.findByVersion(version)){
            proyectoService.eliminarProyecto(proyecto);
        }
        version.setEnabled(false);
        version.setCierre(true);
        versionRepository.save(version);
    }

}
