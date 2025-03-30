package com.nounou.times.model;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;
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
    private YearMonth mois;

    @Column(nullable = false)
    private int heuresTotales;

    @Column(nullable = false)
    private BigDecimal montantTotal;

    @ManyToOne
    @JoinColumn(name = "parent_id", nullable = false)
    private Parent parent;
}