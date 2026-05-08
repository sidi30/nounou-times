package com.nounou.times.model;

import com.nounou.times.converter.YearMonthConverter;
import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.YearMonth;

@Entity
@Getter
@Setter
public class FicheDePaie extends PanacheEntity {

    @Column(nullable = false)
    @Convert(converter = YearMonthConverter.class)
    private YearMonth periode;

    @Column
    private LocalDate dateGeneration;

    @Column
    private Double heuresTotales;

    @Column
    private Double salaireBase;

    @Column
    private Double congesPayes;

    @Column
    private Double indemnitesRepas;

    @Column
    private Double chargesSociales;

    @Column
    private Double salaireBrut;

    @Column
    private Double salaireNet;

    @ManyToOne
    @JoinColumn(name = "nounou_id", nullable = false)
    private Nounou nounou;
}
