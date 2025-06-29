package com.app.model.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "registrations")
@Data
public class Registrations {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "reg_id")
    private Integer registrationId;


    @Column(name = "user_name", nullable = false)
    private String userName;

    @Column(name = "user_email", nullable = false)
    private String userEmail;

    @Column(name = "user_phone")
    private String userPhone;

    @Column(name = "workshop_code", nullable = false)
    private String workshopCode;


    @Enumerated(EnumType.STRING)
    @Column(name = "user_preferred_contact", nullable = false)
    private PreferredContact userPreferredContact;


}
