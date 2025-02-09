package com.nounou.times.model;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Data
public class FicheDePaie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, updatable = false)
    private Long id;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    private int heuresNormales;

    @Column(nullable = false)
    private int heuresSupplémentaires;

    @Column(nullable = false)
    private BigDecimal tauxHoraire;

    @Column(nullable = false)
    private BigDecimal montantTotal;

    @ManyToOne
    @JoinColumn(name = "nounou_id", nullable = false)
    private Nounou nounou;
}
