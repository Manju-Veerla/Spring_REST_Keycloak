package eu.unite.recruiting.model.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "registrations")
@Data
public class Registrations {
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Id
    @Column(name = "id")
    private Integer id;

    @Column(name = "workshop_code", nullable = false, unique = true)
    private String workshopCode;

    @Column(name = "user_name", nullable = false)
    private String userName;

    @Column(name = "user_email", nullable = false)
    private String userEmail;

    @Column(name = "user_phone")
    private String userPhone;

    @Column(name = "user_preferred_contact ")
    private String userPreferredContact ;

}
