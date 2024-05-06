package com.ufps.seminario.service.impl;

import com.ufps.seminario.entity.Feria;
import com.ufps.seminario.entity.Version;
import com.ufps.seminario.repository.VersionRepository;
import com.ufps.seminario.service.VersionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class VersionServiceImpl implements VersionService {
@Autowired
    VersionRepository versionRepository;
    @Override
    public void guardarVersion(Version version) {
        version.setEnabled(true);
        versionRepository.save(version);
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
        version.setEnabled(false);
        versionRepository.save(version);

    }
}
