package com.ufps.seminario.repository;
import com.ufps.seminario.entity.Version;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.ufps.seminario.entity.Proyecto;

import java.util.List;

@Repository
public interface ProyectoRepository extends JpaRepository<Proyecto, Integer> {

    public List<Proyecto> findByVersion(Version version);
    public List<Proyecto> findByIdIn(List<Integer> ids);
    public List<Proyecto> findByIdInOrderByFechaRegistroDesc(List<Integer> ids);
    public List<Proyecto> findByVersionAndEnabled(Version version, boolean b);

    @Query("SELECT p FROM Proyecto p WHERE ((p.calificacion >= p.version.aprobacion AND p.estado != 2) OR p.estado = 1) AND p.version = :version AND p.enabled = true ORDER BY p.calificacion DESC")
    List<Proyecto> findProyectosAprobados(@Param("version") Version version);
    @Query("SELECT p FROM Proyecto p WHERE ((p.calificacion < p.version.aprobacion AND p.estado = 0) OR p.estado = 2) AND p.version = :version AND p.enabled = true ORDER BY p.calificacion DESC")
    List<Proyecto> findProyectosReprobados(@Param("version") Version version);


}
