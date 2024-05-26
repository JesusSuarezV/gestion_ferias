package com.ufps.seminario.service.impl;

import com.ufps.seminario.entity.Feria;
import com.ufps.seminario.entity.Usuario;
import com.ufps.seminario.repository.FeriaRepository;
import com.ufps.seminario.repository.VersionRepository;
import com.ufps.seminario.service.FeriaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FeriaServiceImpl implements FeriaService {
    @Autowired
    FeriaRepository feriaRepository;
    @Autowired
    VersionRepository versionRepository;

    @Override
    public Feria guardarFeria(Feria feria) {
        feria.setEnabled(true);
        return feriaRepository.save(feria);
    }

    @Override
    public Feria obtenerFeria(int id) {
        return feriaRepository.getReferenceById(id);
    }

    @Override
    public Page<Feria> listarMisFerias(Usuario usuario, String keyword, int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);
        return feriaRepository.findByCreadorAndEnabledTrueAndNombreContainingIgnoreCaseOrderByFechaCreacionDescNombreAsc(usuario, keyword, pageable);
    }

    @Override
    public void ocultarFeria(int id) {
        Feria feria = feriaRepository.getReferenceById(id);
        feria.setEnabled(false);
        feriaRepository.save(feria);
    }

    @Override
    public Feria obtenerFeriaByVersion(int idVersion) {
        return versionRepository.getReferenceById(idVersion).getFeria();
    }

}
