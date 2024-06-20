package com.ufps.seminario.entity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Proyecto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String nombre;
    private String objetivos;
    private String descripcion;
    private String archivoUrl;
    private byte[] archivo;
    private String url;
    @ManyToOne
    @JoinColumn(name = "version_id")
    private Version version;
    private int estado;
    private LocalDate fechaRegistro;
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "jurado_proyecto",
            joinColumns = @JoinColumn(name = "proyecto_id"),
            inverseJoinColumns = @JoinColumn(name = "usuario_id")
    )
    private List<Usuario> jurados = new ArrayList<>();
    
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "proyecto_area",
            joinColumns = @JoinColumn(name = "proyecto_id"),
            inverseJoinColumns = @JoinColumn(name = "area_id")
    )
    private List<Area> areas = new ArrayList<>();

    @OneToMany(mappedBy = "proyecto", cascade = CascadeType.ALL)
    private List<Integrante> integrantes = new ArrayList<>();

    private boolean enabled;

    private float calificacion;

}
