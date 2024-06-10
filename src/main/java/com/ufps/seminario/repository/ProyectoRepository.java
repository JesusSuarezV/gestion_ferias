package com.ufps.seminario.repository;
import com.ufps.seminario.entity.Version;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.ufps.seminario.entity.Proyecto;

import java.util.List;

@Repository
public interface ProyectoRepository extends JpaRepository<Proyecto, Integer> {

    public List<Proyecto> findByVersion(Version version);
    public List<Proyecto> findByIdIn(List<Integer> ids);
    public List<Proyecto> findByIdInOrderByFechaRegistroDesc(List<Integer> ids);
    public List<Proyecto> findByVersionAndEnabled(Version version, boolean b);
}
