package com.nounou.times.model;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;

@Entity
@Getter
@Setter
public class FicheDePaie extends PanacheEntity {

    @Column(nullable = false)
    private YearMonth periode;

    @Column(nullable = false)
    private LocalDate dateGeneration;

    @Column(nullable = false)
    private double heuresTotales;

    @Column(nullable = false)
    private BigDecimal salaireBase;

    @Column(nullable = false)
    private BigDecimal congesPayes;

    @Column(nullable = false)
    private BigDecimal indemnitesRepas;

    @Column(nullable = false)
    private BigDecimal chargesSociales;

    @Column(nullable = false)
    private BigDecimal salaireBrut;

    @Column(nullable = false)
    private BigDecimal salaireNet;

    @Column(nullable = false)
    private String cheminFichier;

    @Column(nullable = false)
    private boolean envoyee = false;

    @ManyToOne
    @JoinColumn(name = "nounou_id", nullable = false)
    private Nounou nounou;
}
