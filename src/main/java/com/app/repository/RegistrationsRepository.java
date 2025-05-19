package com.app.repository;

import com.app.model.entity.Registrations;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RegistrationsRepository extends JpaRepository<Registrations, Integer> {

    List<Registrations> findByWorkshopCode(String code);

    boolean existsByWorkshopCodeAndUserName(@NotBlank(message = "Code cannot be empty") @Size(min = 5, max = 15, message = "Code of workshop must be of size 5-15") String workshopCode, String email);

    List<Registrations> findByUserName(String userName);

    int countByWorkshopCode(String code);
}
