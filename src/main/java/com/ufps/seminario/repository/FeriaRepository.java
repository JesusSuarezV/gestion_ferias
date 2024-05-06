package com.ufps.seminario.repository;
import com.ufps.seminario.entity.Feria;
import com.ufps.seminario.entity.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FeriaRepository extends JpaRepository<Feria, Integer> {
    Page<Feria> findByEnabledTrueAndNombreContainingIgnoreCaseOrderByFechaCreacionDescNombreAsc(String nombre, Pageable pageable);

    Page<Feria> findByCreadorAndEnabledTrueAndNombreContainingIgnoreCaseOrderByFechaCreacionDescNombreAsc(Usuario creador, String nombre, Pageable pageable);
}
