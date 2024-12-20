package com.nounou.times.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Entity
@Data
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "parent_id", nullable = false)
    private User parent;

    @ManyToOne
    @JoinColumn(name = "nounou_id", nullable = false)
    private User nounou;

    @Column(nullable = false)
    private LocalDate month;

    @OneToMany(cascade = CascadeType.ALL)
    private List<Schedule> schedules;

    @Column(nullable = false)
    private double totalPayment;

    @Column(nullable = false)
    private String createdDate;

    @Column(nullable = false)
    private String updatedDate;

    @PrePersist
    public void prePersist() {
        createdDate = updatedDate = String.valueOf(System.currentTimeMillis());
    }

    @PreUpdate
    public void preUpdate() {
        updatedDate = String.valueOf(System.currentTimeMillis());
    }
}
