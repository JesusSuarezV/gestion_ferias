package com.ufps.seminario.entity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Dominio {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String dominio;

    @ManyToOne
    @JoinColumn(name = "version_id")
    private Version version;

    private boolean enabled;
}
