package com.ufps.seminario.repository;
import com.ufps.seminario.entity.Criterio;
import com.ufps.seminario.entity.Version;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CriterioRepository extends JpaRepository<Criterio, Integer> {
    List<Criterio> findByVersionAndEnabled(Version version, boolean b);
}
