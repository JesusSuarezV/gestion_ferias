package com.ufps.seminario.repository;
import com.ufps.seminario.entity.Feria;
import com.ufps.seminario.entity.Proyecto;
import com.ufps.seminario.entity.Version;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VersionRepository extends JpaRepository<Version, Integer> {

    Page<Version> findByFeriaAndEnabledTrueOrderByFechaInicioDescNumeroAsc(Feria feria, Pageable pageable);
}
