package com.ufps.seminario.repository;
import com.ufps.seminario.entity.Calificacion;
import com.ufps.seminario.entity.Criterio;
import com.ufps.seminario.entity.Proyecto;
import com.ufps.seminario.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CalificacionRepository extends JpaRepository<Calificacion, Integer> {
    public List<Calificacion> findByProyectoAndJurado(Proyecto proyecto,Usuario jurado);

    public Calificacion findByProyectoAndJuradoAndCriterio(Proyecto proyecto,Usuario jurado, Criterio criterio);

}
