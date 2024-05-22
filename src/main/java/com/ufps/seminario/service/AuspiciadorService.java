package com.ufps.seminario.service;

import com.ufps.seminario.entity.Auspiciador;
import com.ufps.seminario.entity.Version;

import java.util.List;

public interface AuspiciadorService {
    public Auspiciador guardarAuspiciador(Auspiciador auspiciador);
    public List<Auspiciador> obtenerAuspiciadorPorVersion(Version version);

    public void eliminarAuspiciadorPorId(int id);
}
