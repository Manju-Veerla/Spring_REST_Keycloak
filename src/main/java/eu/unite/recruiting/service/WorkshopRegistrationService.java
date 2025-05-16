package eu.unite.recruiting.service;


import eu.unite.recruiting.exception.InvalidWorkshopDataException;
import eu.unite.recruiting.model.dto.RegistrationsDto;
import eu.unite.recruiting.model.entity.Workshop;
import eu.unite.recruiting.model.mapper.RegistrationsMapper;
import eu.unite.recruiting.model.mapper.WorkshopMapper;
import eu.unite.recruiting.repository.RegistrationsRepository;
import eu.unite.recruiting.repository.WorkshopRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service class for managing workshop registrations.
 */
@Service
@AllArgsConstructor
@Slf4j
public class WorkshopRegistrationService {
    // Repository for accessing workshop data
    private final WorkshopRepository workshopRepository;
    // Mapper for converting between Workshop and WorkshopDto
    private final WorkshopMapper workshopMapper;
    // Service for managing workshop registrations
    private final RegistrationsRepository registrationsRepository;
    // Mapper for converting between Registrations and RegistrationsDto
    private final RegistrationsMapper registrationsMapper;

    /**
     * Returns all registrations by workshop code.
     *
     * @param code the code of the workshop
     * @return the registrations with the given code
     */
    public List<RegistrationsDto> getRegistrationsByCode(String code) {
        log.debug("Getting registration with workshop code  {} ", code);
        return registrationsRepository.findByWorkshopCode(code).stream()
                .map(registrationsMapper::RegistrationsToRegistrationsDto)
                .toList();
    }

    /**
     * Checks if a user is already registered for a workshop.
     *
     * @param code the code of the workshop
     * @return true if the user is already registered, false otherwise
     */
    public int getWorkshopCapacity(String code) {
        log.debug("Getting registration count with workshop code  {} ", code);
        return registrationsRepository.countByWorkshopCode(code);
    }

    /**
     * Checks if a workshop is full based on its code.
     *
     * @param code the code of the workshop
     * @return true if the workshop is full, false otherwise
     */
    public boolean isWorkshopFull(String code) {
        log.debug("checking if capacity is full for workshop code {} ", code);
        Workshop workshop = workshopRepository.findByCode(code)
                .orElseThrow(() -> new InvalidWorkshopDataException("Workshop not found with given code: " + code));
        return registrationsRepository.countByWorkshopCode(code) >= workshop.getCapacity();
    }
}
