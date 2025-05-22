package com.app.service;


import com.app.exception.InvalidUserException;
import com.app.exception.InvalidWorkshopDataException;
import com.app.exception.RegistrationDoesnotExistException;
import com.app.exception.UserAlreadyRegisteredException;
import com.app.model.entity.Registrations;
import com.app.model.entity.Workshop;
import com.app.model.mapper.RegistrationsMapper;
import com.app.model.request.RegistrationsRequest;
import com.app.model.response.RegistrationsResponse;
import com.app.repository.RegistrationsRepository;
import com.app.repository.WorkshopRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service class for managing registrations.
 */
@Service
@AllArgsConstructor
@Slf4j
public class RegistrationService {

    // The workshop registration repository
    private final WorkshopRegistrationService workshopRegistrationService;
    // The registration repository
    private final RegistrationsRepository registrationRepository;
    // The registration mapper
    private final RegistrationsMapper registrationsMapper;

    private final WorkshopRepository workshopRepository;

    /**
     * Returns all workshops.
     *
     * @return a list of all workshops
     */
    public List<RegistrationsResponse> getAllRegistrations() {
        return registrationRepository.findAll().stream()
                .map(registrationsMapper::RegistrationsToRegistrationsResponse)
                .toList();
    }

    /**
     * Creates a new registration.
     *
     * @param registrationsRequest the registration details
     * @param authentication       the authentication object
     * @return the created registration
     */
    public RegistrationsResponse createRegistration(RegistrationsRequest registrationsRequest, Authentication authentication) {
        String userName = "";
        String email = "";
        if (authentication instanceof JwtAuthenticationToken jwtAuth) {
            userName = jwtAuth.getTokenAttributes().get("preferred_username").toString();
            email = jwtAuth.getTokenAttributes().get("email").toString();
        }
        if (StringUtils.isBlank(userName) || StringUtils.isBlank(email)) {
            throw new InvalidUserException("User not found");
        }
        log.debug("Check if registration already exists {}", registrationsRequest.getWorkshopCode());
        if (checkIfUserAlreadyRegistered(registrationsRequest.getWorkshopCode(), userName)) {
            throw new UserAlreadyRegisteredException("User already registered");
        }
        log.debug("check if capacity is full for workshop code {}", registrationsRequest.getWorkshopCode());
        if (workshopRegistrationService.isWorkshopFull(registrationsRequest.getWorkshopCode())) {
            throw new InvalidWorkshopDataException("Workshop is full , user cannot register");
        }
        registrationsRequest.setUserName(userName);
        registrationsRequest.setUserEmail(email);
        final Registrations registrationToSave = registrationsMapper.RegistrationsRequestToRegistrations(registrationsRequest);
        Workshop updateWorkshop = workshopRepository.findByCode(registrationsRequest.getWorkshopCode())
                .orElseThrow(() -> new InvalidWorkshopDataException("Workshop not found with given code: " + registrationsRequest.getWorkshopCode()));

        Registrations savedRegistration = registrationRepository.save(registrationToSave);
        updateWorkshop.getRegistrations().add(registrationToSave);
        Workshop savedWorkshop = workshopRepository.save(updateWorkshop);
        savedRegistration.setWorkshopCode(savedWorkshop.getCode());
        return registrationsMapper.RegistrationsToRegistrationsResponse(savedRegistration);
    }

    /**
     * Checks if the user is already registered for the workshop.
     *
     * @param workshopCode the code of the workshop
     * @param userName     the name of the user
     * @return true if the user is already registered, false otherwise
     */
    private boolean checkIfUserAlreadyRegistered(@NotBlank(message = "Code cannot be empty") @Size(min = 5, max = 15, message = "Code of workshop must be of size 5-15") String workshopCode, String userName) {
        return workshopRepository.existsByCodeAndUserName(workshopCode, userName);
    }

    /**
     * Deletes a registration by its ID.
     *
     * @param id the ID of the registration to delete
     */
    @Transactional
    public void deleteRegistration(Integer id) {
        log.debug("Deleting registration with id {}", id);
        registrationRepository.findById(id)
                .orElseThrow(() -> new RegistrationDoesnotExistException("Registration not available with given id: " + id));
        registrationRepository.deleteById(id);
    }

    /**
     * Returns all registrations for a specific user.
     *
     * @param authentication the authentication object
     * @return a list of registrations for the user
     */
    public List<RegistrationsResponse> getUserRegistrations(Authentication authentication) {
        String userName = "";
        if (authentication instanceof JwtAuthenticationToken jwtAuth) {
            userName = jwtAuth.getTokenAttributes().get("preferred_username").toString();
        }
        if (StringUtils.isBlank(userName)) {
            throw new InvalidUserException("User not found");
        }
        log.debug("Getting all registration details specific to user {}", userName);
        List<Registrations> registrations = registrationRepository.findByUserName(userName);
        if (CollectionUtils.isNotEmpty(registrations)) {
            return registrations.stream()
                    .map(registrationsMapper::RegistrationsToRegistrationsResponse)
                    .toList();
        }
        return List.of();
    }
}
