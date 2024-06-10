package com.ufps.seminario.repository;
import com.ufps.seminario.entity.Integrante;
import com.ufps.seminario.entity.Proyecto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IntegranteRepository extends JpaRepository<Integrante, Integer> {
    List<Integrante> findByCorreoRegistro(String correoRegistro);
    List<Integrante> findByProyecto(Proyecto proyecto);

    List<Integrante> findByProyectoAndEnabled(Proyecto proyecto, boolean a);
}
