package com.ufps.seminario.service.impl;

import com.ufps.seminario.entity.Auspiciador;
import com.ufps.seminario.entity.Version;
import com.ufps.seminario.repository.AuspiciadorRepository;
import com.ufps.seminario.service.AuspiciadorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuspiciadorServiceImpl implements AuspiciadorService {
    @Autowired
    AuspiciadorRepository auspiciadorRepository;

    @Override
    public Auspiciador guardarAuspiciador(Auspiciador auspiciador) {
        return auspiciadorRepository.save(auspiciador);
    }

    @Override
    public List<Auspiciador> obtenerAuspiciadorPorVersion(Version version) {
        return auspiciadorRepository.findByVersion(version);
    }

    @Override
    public void eliminarAuspiciadorPorId(int id) {
       auspiciadorRepository.delete(auspiciadorRepository.getReferenceById(id));
    }
}
