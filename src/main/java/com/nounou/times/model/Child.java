package com.nounou.times.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Data
public class Child {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @ManyToOne
    @JoinColumn(name = "parent_id", nullable = false)
    private User parent;

    @OneToMany(mappedBy = "child", cascade = CascadeType.ALL)
    private List<Schedule> schedules;

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
