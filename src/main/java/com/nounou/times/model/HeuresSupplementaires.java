package com.nounou.times.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.Data;

import java.time.Duration;

@Entity
@Data
public class HeuresSupplementaires extends Evenement {

    @Column(nullable = false)
    private Duration durée;

    @Column(nullable = false)
    private boolean validation; // True if validated, false otherwise
}
