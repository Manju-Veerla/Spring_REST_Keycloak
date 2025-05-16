package eu.unite.recruiting.service;


import eu.unite.recruiting.exception.InvalidWorkshopDataException;
import eu.unite.recruiting.exception.WorkshopAlreadyExistException;
import eu.unite.recruiting.exception.WorkshopNotFoundException;
import eu.unite.recruiting.model.dto.WorkshopDto;
import eu.unite.recruiting.model.dto.WorkshopUpdateDto;
import eu.unite.recruiting.model.entity.Workshop;
import eu.unite.recruiting.model.mapper.WorkshopMapper;
import eu.unite.recruiting.repository.WorkshopRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.List;


/**
 * Service class for managing workshops.
 */
@Service
@AllArgsConstructor
@Slf4j
public class WorkshopService {

    // Repository for accessing workshop data
    private final WorkshopRepository workshopRepository;
    // Mapper for converting between Workshop and WorkshopDto
    private final WorkshopMapper workshopMapper;
    // Service for managing workshop registrations
    private final WorkshopRegistrationService workshopRegistrationService;

    /**
     * Returns all workshops.
     *
     * @return a list of all upcoming workshops
     */
    public List<WorkshopDto> getUpcomingWorkshops() {
        ZonedDateTime now = ZonedDateTime.now(); // Get the current date and time
        return workshopRepository.findAll().stream()
                .filter(workshop -> workshop.getEndTime().isAfter(now)) // Filter out past workshops
                .map(workshopMapper::WorkshopToWorkshopDto)
                .toList();
    }

    /**
     * Returns all workshops.
     *
     * @return a list of all workshops
     */
    public List<WorkshopDto> getAllWorkshops() {
        log.debug("Getting all workshop details for admin ");
        return workshopRepository.findAll().stream()
                .map(workshopMapper::WorkshopToWorkshopDto)
                .toList();
    }

    /**
     * Creates a new Workshop. The workshop code must not yet exist in the database.
     *
     * @param workshopDto the workshop data to create
     * @return the created Workshop
     * @throws WorkshopAlreadyExistException if the workshop with the same code already exists
     * @throws InvalidWorkshopDataException  if the start time is after the end time
     */
    public WorkshopDto createWorkshop(WorkshopDto workshopDto) {
        log.debug("Check if workshop already exists {}", workshopDto.getCode());
        checkWorkshopExist(workshopDto.getCode());
        // Check if the start time is before the end time
        if (workshopDto.getStartTime() == null || workshopDto.getEndTime() == null) {
            throw new InvalidWorkshopDataException("Start /End time must be specified");
        }
        if (workshopDto.getStartTime().isAfter(workshopDto.getEndTime())) {
            throw new InvalidWorkshopDataException("Start time must be before end time");
        }
        return saveWorkshop(workshopDto);
    }

    /**
     * Checks if a workshop with the given code already exists in the database.
     *
     * @param code the code to check
     * @throws WorkshopAlreadyExistException if the workshop already exists
     */
    private void checkWorkshopExist(final String code) {
        if (workshopRepository.existsWorkshopByCode(code)) {
            throw new WorkshopAlreadyExistException("Workshop already exists with given code: " + code);
        }
    }

    /**
     * Saves a workshop to the database.
     *
     * @param workshopDto the workshop data to save
     * @return the saved Workshop
     */
    @Transactional
    private WorkshopDto saveWorkshop(WorkshopDto workshopDto) {
        final Workshop workshopToSave = workshopMapper.WorkshopDtoToWorkshop(workshopDto);
        Workshop savedWorkshop = workshopRepository.save(workshopToSave);
        return workshopMapper.WorkshopToWorkshopDto(savedWorkshop);
    }

    /**
     * Returns a workshop by its code.
     *
     * @param code the code of the workshop to retrieve
     * @return the workshop with the given code
     */
    public WorkshopDto getWorkshopByCode(String code) {
        Workshop workshop = workshopRepository.findByCode(code)
                .orElseThrow(() -> new WorkshopNotFoundException("Workshop not found with given code: " + code));
        return workshopMapper.WorkshopToWorkshopDto(workshop);
    }


    /**
     * Deletes a workshop by its code.
     *
     * @param workshopCode the code of the workshop to delete
     */
    @Transactional
    public void deleteWorkshop(String workshopCode) {
        Workshop workshop = workshopRepository.findByCode(workshopCode)
                .orElseThrow(() -> new WorkshopNotFoundException("Workshop not found with given code: " + workshopCode));
        if (workshopRegistrationService.getRegistrationsByCode(workshopCode).size() > 0) {
            throw new InvalidWorkshopDataException("Cannot delete workshop with registrations");
        }
        workshopRepository.delete(workshop);
        log.info("Workshop with code {} deleted successfully", workshopCode);
    }

    /**
     * Updates a workshop by its code.
     *
     * @param workshopCode the code of the workshop to update
     * @param workshopDto  the new workshop data
     * @return the updated workshop
     */
    @Transactional
    public WorkshopDto updateWorkshop(String workshopCode, WorkshopUpdateDto workshopDto) {
        Workshop workshop = workshopRepository.findByCode(workshopCode)
                .orElseThrow(() -> new WorkshopNotFoundException("Workshop not found with given code: " + workshopCode));
        return updateData(workshop, workshopDto);
    }

    /**
     * Updates the workshop data.
     *
     * @param workshop    the workshop to update
     * @param workshopDto the new workshop data
     * @return the updated workshop
     */
    private WorkshopDto updateData(Workshop workshop, WorkshopUpdateDto workshopDto) {
        log.info("Updating workshop with code {}", workshop.getCode());
        if (StringUtils.isNotBlank(workshopDto.getName())) {
            workshop.setName(workshopDto.getName());
        }
        if (StringUtils.isNotBlank(workshopDto.getDescription())) {
            workshop.setDescription(workshopDto.getDescription());
        }
        if (workshopDto.getCapacity() != null && workshopDto.getCapacity() >= 0) {
            //TODO check existing registrations and add error while changing capacity
            workshop.setCapacity(workshopDto.getCapacity());
        }
        if (workshopDto.getStartTime() != null && workshopDto.getEndTime() != null) {
            validateStartAndEndDate(workshopDto);
            workshop.setStartTime(workshopDto.getStartTime());
            workshop.setEndTime(workshopDto.getEndTime());
        } else {
            validateStartOrEndTime(workshop, workshopDto);
        }
        Workshop updatedWorkshop = workshopRepository.save(workshop);
        return workshopMapper.WorkshopToWorkshopDto(updatedWorkshop);
    }

    /**
     * Validates the start or end time of the workshop.
     *
     * @param workshop    the workshop to validate
     * @param workshopDto the new workshop data
     */
    private static void validateStartOrEndTime(Workshop workshop, WorkshopUpdateDto workshopDto) {
        if (workshopDto.getStartTime() != null) {
            if (workshopDto.getStartTime().isBefore(ZonedDateTime.now())) {
                throw new InvalidWorkshopDataException("Start time must be in the future");
            }
            workshop.setStartTime(workshopDto.getStartTime());
        }
        if (workshopDto.getEndTime() != null) {
            if (workshopDto.getEndTime().isBefore(ZonedDateTime.now())) {
                throw new InvalidWorkshopDataException("End time must be in future ");
            }
            if (workshopDto.getEndTime().isBefore(workshopDto.getStartTime())) {
                throw new InvalidWorkshopDataException("End time must be after start time");
            }
            workshop.setEndTime(workshopDto.getEndTime());
        }
    }

    /**
     * Validates the start and end date of the workshop.
     *
     * @param workshopDto
     */
    private static void validateStartAndEndDate(WorkshopUpdateDto workshopDto) {
        if (workshopDto.getStartTime().isBefore(ZonedDateTime.now())) {
            throw new InvalidWorkshopDataException("Start time must be in the future");
        }
        if (workshopDto.getEndTime().isBefore(ZonedDateTime.now())) {
            throw new InvalidWorkshopDataException("End time must be in future ");
        }
        if (workshopDto.getEndTime().isBefore(workshopDto.getStartTime())) {
            throw new InvalidWorkshopDataException("End time must be after start time");
        }
    }

}
