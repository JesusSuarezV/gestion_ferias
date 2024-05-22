package com.ufps.seminario.service;

import com.ufps.seminario.entity.Area;
import com.ufps.seminario.entity.Feria;

import java.util.List;

public interface AreaService {
    public Area guardarArea(Area area);
    public Area obtenerArea(int id);
    List<Area> obtenerAreasPorFeria(Feria feria);
    public void desactivarAreaPorId(int id);
}
