package com.ufps.seminario.service.impl;

import com.ufps.seminario.entity.Area;
import com.ufps.seminario.entity.Feria;
import com.ufps.seminario.repository.AreaRepository;
import com.ufps.seminario.service.AreaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class AreaServiceImpl implements AreaService {

    @Autowired
    AreaRepository areaRepository;

    @Override
    public Area guardarArea(Area area) {
        return areaRepository.save(area);
    }

    @Override
    public Area obtenerArea(int id) {
        return areaRepository.getReferenceById(id);
    }

    @Override
    public List<Area> obtenerAreasPorFeria(Feria feria) {
        return areaRepository.findAllByFeriaAndEnabled(feria, true);
    }

    @Override
    public void desactivarAreaPorId(int id) {
        Area area = areaRepository.getReferenceById(id);
        area.setEnabled(false);
        areaRepository.save(area);
    }
}
