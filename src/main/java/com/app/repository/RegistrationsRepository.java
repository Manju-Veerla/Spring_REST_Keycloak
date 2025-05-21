package com.app.repository;

import com.app.model.entity.Registrations;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RegistrationsRepository extends JpaRepository<Registrations, Integer> {

    List<Registrations> findByUserName(String userName);


}
