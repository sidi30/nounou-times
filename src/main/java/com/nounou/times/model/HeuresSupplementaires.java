package com.nounou.times.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

import java.time.Duration;

@Entity
@Getter
@Setter
public class HeuresSupplementaires extends Evenement {

    @Column(nullable = false)
    private Duration duree;

    @Column(nullable = false)
    private boolean validation;
}
