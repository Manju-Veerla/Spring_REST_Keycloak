package com.app.repository;

import com.app.model.entity.Workshop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WorkshopRepository extends JpaRepository<Workshop, Integer> {

    Optional<Workshop> findByCode(String code);
    boolean existsWorkshopByCode(String code);
    int countByCode(String code);
    @Query("SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END FROM Workshop w JOIN w.registrations r WHERE w.code = :code AND r.userName = :userName")
    boolean existsByCodeAndUserName(@Param("code") String code, @Param("userName") String userName);


}
