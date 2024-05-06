package com.ufps.seminario.service.impl;

import com.ufps.seminario.entity.Feria;
import com.ufps.seminario.entity.Usuario;
import com.ufps.seminario.repository.FeriaRepository;
import com.ufps.seminario.service.FeriaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class FeriaServiceImpl implements FeriaService {
    @Autowired
    FeriaRepository feriaRepository;
    @Override
    public void guardarFeria(Feria feria) {
        feria.setEnabled(true);
        feriaRepository.save(feria);
    }

    @Override
    public Feria obtenerFeria(int id) {
        return feriaRepository.getReferenceById(id);
    }

    @Override
    public Page listarMisFerias(Usuario usuario, String nombre, Pageable pageable) {
        return feriaRepository.findByCreadorAndEnabledTrueAndNombreContainingIgnoreCaseOrderByFechaCreacionDescNombreAsc(usuario, nombre, pageable);
    }

    @Override
    public void ocultarFeria(int id) {
        Feria feria = feriaRepository.getReferenceById(id);
        feria.setEnabled(false);
        feriaRepository.save(feria);
    }
}
