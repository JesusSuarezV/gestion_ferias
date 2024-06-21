package com.ufps.seminario.repository;
import com.ufps.seminario.entity.Feria;
import com.ufps.seminario.entity.Integrante;
import com.ufps.seminario.entity.Proyecto;
import com.ufps.seminario.entity.Version;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

@Repository
public interface VersionRepository extends JpaRepository<Version, Integer> {

    Page<Version> findByFeriaAndEnabledTrueOrderByFechaInicioDescNumeroAsc(Feria feria, Pageable pageable);
    List<Version> findByIdIn(List<Integer> id);
    List<Version> findByFeriaAndEnabledOrderByFechaInicioDesc(Feria feria, boolean enabled);
    List<Version> findByFeriaAndFechaCierreAfter(Feria feria, LocalDate now);

    List<Version> findByFeriaAndCierreAndFechaCierreAfter(Feria feria, boolean cierre, LocalDate now);

    List<Version> findByCierreOrFechaCierreBefore(boolean b, LocalDate fecha);

    Version[] findByFeria(Feria feria);

} 
