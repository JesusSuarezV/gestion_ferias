package com.ufps.seminario.repository;
import com.ufps.seminario.entity.Auspiciador;
import com.ufps.seminario.entity.Version;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuspiciadorRepository extends JpaRepository<Auspiciador, Integer> {
    List<Auspiciador> findByVersion(Version version);

    List<Auspiciador> findByVersionAndEnabled(Version version, boolean b);
}
