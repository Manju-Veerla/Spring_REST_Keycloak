package com.app.service;


import com.app.exception.InvalidWorkshopDataException;
import com.app.exception.WorkshopAlreadyExistException;
import com.app.exception.WorkshopNotFoundException;
import com.app.model.entity.Workshop;
import com.app.model.mapper.WorkshopMapper;
import com.app.model.request.WorkshopRequest;
import com.app.model.request.WorkshopUpdateRequest;
import com.app.model.response.WorkshopResponse;
import com.app.repository.WorkshopRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;


/**
 * Service class for managing workshops.
 */
@Service
@AllArgsConstructor
@Slf4j
public class WorkshopService {

    // Repository for accessing workshop data
    private final WorkshopRepository workshopRepository;
    // Mapper for converting between Workshop and WorkshopRequest
    private final WorkshopMapper workshopMapper;

    private final WorkshopRegistrationService workshopRegistrationService;

    /**
     * Returns all workshops.
     *
     * @return a list of all upcoming workshops
     */
    public List<WorkshopResponse> getUpcomingWorkshops() {
        ZonedDateTime now = ZonedDateTime.now(); // Get the current date and time
        return workshopRepository.findAll().stream()
                .filter(workshop -> workshop.getEndTime().isAfter(now)) // Filter out past workshops
                .map(workshopMapper::WorkshopToWorkshopWithoutRegistrationsResponse) // Map to WorkshopSummaryResponse
                .toList();
    }

    /**
     * Returns all workshops.
     *
     * @return a list of all workshops
     */

    public List<WorkshopResponse> getAllWorkshops() {
        log.debug("Getting all workshop details for admin ");
        List<Workshop> workshops = workshopRepository.findAll();
        return workshops.stream()
                .map(workshopMapper::WorkshopToWorkshopResponse)
                .collect(Collectors.toList());
    }

    /**
     * Creates a new Workshop. The workshop code must not yet exist in the database.
     *
     * @param workshopRequest the workshop data to create
     * @return the created Workshop
     * @throws WorkshopAlreadyExistException if the workshop with the same code already exists
     * @throws InvalidWorkshopDataException  if the start time is after the end time
     */
    public WorkshopRequest createWorkshop(WorkshopRequest workshopRequest) {
        log.debug("Check if workshop already exists {}", workshopRequest.getCode());
        checkWorkshopExist(workshopRequest.getCode());
        // Check if the start time is before the end time
        if (workshopRequest.getStartTime() == null || workshopRequest.getEndTime() == null) {
            throw new InvalidWorkshopDataException("Start /End time must be specified");
        }
        if (workshopRequest.getStartTime().isAfter(workshopRequest.getEndTime())) {
            throw new InvalidWorkshopDataException("Start time must be before end time");
        }
        return saveWorkshop(workshopRequest);
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
     * @param workshopRequest the workshop data to save
     * @return the saved Workshop
     */
    @Transactional
    private WorkshopRequest saveWorkshop(WorkshopRequest workshopRequest) {
        final Workshop workshopToSave = workshopMapper.WorkshopRequestToWorkshop(workshopRequest);
        Workshop savedWorkshop = workshopRepository.save(workshopToSave);
        return workshopMapper.WorkshopToWorkshopRequest(savedWorkshop);
    }

    /**
     * Returns a workshop by its code.
     *
     * @param code the code of the workshop to retrieve
     * @return the workshop with the given code
     */
    public WorkshopResponse getWorkshopByCode(String code) {
        Workshop workshop = workshopRepository.findByCode(code)
                .orElseThrow(() -> new WorkshopNotFoundException("Workshop not found with given code: " + code));
        return workshopMapper.WorkshopToWorkshopResponse(workshop);
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
        if (!workshopRegistrationService.getRegistrationsByCode(workshopCode).isEmpty()) {
            throw new InvalidWorkshopDataException("Cannot delete workshop with registrations");
        }
        workshopRepository.delete(workshop);
        log.info("Workshop with code {} deleted successfully", workshopCode);
    }

    /**
     * Updates a workshop by its code.
     *
     * @param workshopCode          the code of the workshop to update
     * @param workshopUpdateRequest the new workshop data
     * @return the updated workshop
     */
    @Transactional
    public WorkshopResponse updateWorkshop(String workshopCode, WorkshopUpdateRequest workshopUpdateRequest) {
        Workshop workshop = workshopRepository.findByCode(workshopCode)
                .orElseThrow(() -> new WorkshopNotFoundException("Workshop not found with given code: " + workshopCode));
        return updateData(workshop, workshopUpdateRequest);
    }

    /**
     * Updates the workshop data.
     *
     * @param workshop              the workshop to update
     * @param workshopUpdateRequest the new workshop data
     * @return the updated workshop
     */
    private WorkshopResponse updateData(Workshop workshop, WorkshopUpdateRequest workshopUpdateRequest) {
        log.info("Updating workshop with code {}", workshop.getCode());
        if (StringUtils.isNotBlank(workshopUpdateRequest.getName())) {
            workshop.setName(workshopUpdateRequest.getName());
        }
        if (StringUtils.isNotBlank(workshopUpdateRequest.getDescription())) {
            workshop.setDescription(workshopUpdateRequest.getDescription());
        }
        if (workshopUpdateRequest.getCapacity() != null && workshopUpdateRequest.getCapacity() >= 0) {
            //TODO check existing registrations and add error while changing capacity
            workshop.setCapacity(workshopUpdateRequest.getCapacity());
        }
        if (workshopUpdateRequest.getStartTime() != null && workshopUpdateRequest.getEndTime() != null) {
            validateStartAndEndDate(workshopUpdateRequest);
            workshop.setStartTime(workshopUpdateRequest.getStartTime());
            workshop.setEndTime(workshopUpdateRequest.getEndTime());
        } else {
            validateStartOrEndTime(workshop, workshopUpdateRequest);
        }
        Workshop updatedWorkshop = workshopRepository.save(workshop);
        return workshopMapper.WorkshopToWorkshopWithoutRegistrationsResponse(updatedWorkshop);
    }

    /**
     * Validates the start or end time of the workshop.
     *
     * @param workshop              the workshop to validate
     * @param workshopUpdateRequest the new workshop data
     */
    private static void validateStartOrEndTime(Workshop workshop, WorkshopUpdateRequest workshopUpdateRequest) {
        if (workshopUpdateRequest.getStartTime() != null) {
            if (workshopUpdateRequest.getStartTime().isBefore(ZonedDateTime.now())) {
                throw new InvalidWorkshopDataException("Start time must be in the future");
            }
            workshop.setStartTime(workshopUpdateRequest.getStartTime());
        }
        if (workshopUpdateRequest.getEndTime() != null) {
            if (workshopUpdateRequest.getEndTime().isBefore(ZonedDateTime.now())) {
                throw new InvalidWorkshopDataException("End time must be in future ");
            }
            if (workshopUpdateRequest.getEndTime().isBefore(workshopUpdateRequest.getStartTime())) {
                throw new InvalidWorkshopDataException("End time must be after start time");
            }
            workshop.setEndTime(workshopUpdateRequest.getEndTime());
        }
    }

    /**
     * Validates the start and end date of the workshop.
     *
     * @param workshopUpdateRequest the new workshop data
     */
    private static void validateStartAndEndDate(WorkshopUpdateRequest workshopUpdateRequest) {
        if (workshopUpdateRequest.getStartTime().isBefore(ZonedDateTime.now())) {
            throw new InvalidWorkshopDataException("Start time must be in the future");
        }
        if (workshopUpdateRequest.getEndTime().isBefore(ZonedDateTime.now())) {
            throw new InvalidWorkshopDataException("End time must be in future ");
        }
        if (workshopUpdateRequest.getEndTime().isBefore(workshopUpdateRequest.getStartTime())) {
            throw new InvalidWorkshopDataException("End time must be after start time");
        }
    }

}
