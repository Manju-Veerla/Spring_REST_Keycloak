package eu.unite.recruiting.model.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "registrations")
@Data
public class Registrations {
    @SequenceGenerator(
            name = "registrations_seq",
            sequenceName = "registrations_seq",
            allocationSize = 1
    )
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "registrations_seq")
    @Id
    @Column(name = "id")
    private Integer id;

    @Column(name = "workshop_code", nullable = false)
    private String workshopCode;

    @Column(name = "user_name", nullable = false)
    private String userName;

    @Column(name = "user_email", nullable = false)
    private String userEmail;

    @Column(name = "user_phone")
    private String userPhone;

    @Column(name = "user_preferred_contact ")
    private String userPreferredContact;

}
