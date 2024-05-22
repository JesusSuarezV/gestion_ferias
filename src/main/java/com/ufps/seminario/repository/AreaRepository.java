package com.ufps.seminario.repository;
import com.ufps.seminario.entity.Area;
import com.ufps.seminario.entity.Feria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AreaRepository extends JpaRepository<Area, Integer> {

    List<Area> findAllByFeria(Feria feria);
    List<Area> findAllByFeriaAndEnabled(Feria feria, boolean enabled);
}