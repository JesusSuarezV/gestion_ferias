package com.ufps.seminario.repository;
import com.ufps.seminario.entity.Auspiciador;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuspiciadorRepository extends JpaRepository<Auspiciador, Integer> {
}
