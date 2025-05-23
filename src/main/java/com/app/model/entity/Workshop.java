package com.app.model.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.ZonedDateTime;
import java.util.Set;

@Entity
@Table(name = "workshops")
@Data
public class Workshop {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "workshop_id")
    private Integer workshopId;

    @Column(name = "code", nullable = false, unique = true)
    private String code;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "start_time", nullable = false)
    private ZonedDateTime startTime;

    @Column(name = "end_time", nullable = false)
    private ZonedDateTime endTime;

    @Column(name = "capacity", nullable = false)
    private int capacity;

    @OneToMany( cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "workshop_code", referencedColumnName="code")
    private Set<Registrations> registrations;
}
