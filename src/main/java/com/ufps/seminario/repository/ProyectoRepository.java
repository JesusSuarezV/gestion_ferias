package com.ufps.seminario.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.ufps.seminario.entity.Proyecto;

@Repository
public interface ProyectoRepository extends JpaRepository<Proyecto, Integer> {
}
