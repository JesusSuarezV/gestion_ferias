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
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String username;
    private String password;
    private String nombre;
    private String apellido;
    @ManyToOne
    @JoinColumn(name = "role_id")
    private Role role;
    @OneToMany (mappedBy = "creador", cascade = CascadeType.ALL)
    private List<Feria> misFerias = new ArrayList<>();

    private boolean enabled;
}
