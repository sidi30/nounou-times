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
public class RapportMensuel extends PanacheEntity {

    @Column(nullable = false)
    private YearMonth periode;

    @Column(nullable = false)
    private Long nombreGardes;

    @Column(nullable = false)
    private Long nombreAbsences;

    @Column(nullable = false)
    private double heuresTotales;

    @Column(nullable = false)
    private double montantTotal;

    @Column(nullable = false)
    private LocalDate dateGeneration;

    @ManyToOne
    @JoinColumn(name = "nounou_id", nullable = false)
    private Nounou nounou;
}