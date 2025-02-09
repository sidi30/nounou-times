package com.nounou.times.model;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Getter
@Setter
public class FicheDePaie extends PanacheEntity {


    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    private int heuresNormales;

    @Column(nullable = false)
    private int heuresSupplementaires;

    @Column(nullable = false)
    private BigDecimal tauxHoraire;

    @Column(nullable = false)
    private BigDecimal montantTotal;

    @ManyToOne
    @JoinColumn(name = "nounou_id", nullable = false)
    private Nounou nounou;
}
