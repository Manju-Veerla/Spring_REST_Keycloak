package eu.unite.recruiting.repository;

import eu.unite.recruiting.model.entity.Workshop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WorkshopRepository extends JpaRepository<Workshop, Integer> {

    Optional<Workshop> findByCode(String code);

    boolean existsWorkshopByCode(String code);
}
