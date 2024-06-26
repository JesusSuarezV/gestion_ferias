package com.ufps.seminario.entity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Area {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String nombre;

    @ManyToOne
    @JoinColumn(name = "feria_id")
    private Feria feria;
    private boolean enabled;

    @Override
    public boolean equals(Object obj){
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        final Area other = (Area)obj;
        return other.getId() == this.id;
    }
}
