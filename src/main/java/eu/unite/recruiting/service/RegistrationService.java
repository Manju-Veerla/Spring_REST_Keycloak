package eu.unite.recruiting.service;


import eu.unite.recruiting.exception.InvalidUserException;
import eu.unite.recruiting.exception.InvalidWorkshopDataException;
import eu.unite.recruiting.exception.RegistrationDoesnotExistException;
import eu.unite.recruiting.exception.UserAlreadyRegisteredException;
import eu.unite.recruiting.model.dto.RegistrationsDto;
import eu.unite.recruiting.model.dto.RegistrationsResponseDto;
import eu.unite.recruiting.model.entity.Registrations;
import eu.unite.recruiting.model.entity.Workshop;
import eu.unite.recruiting.model.mapper.RegistrationsMapper;
import eu.unite.recruiting.repository.RegistrationsRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import org.apache.commons.lang3.StringUtils;

import java.util.List;


@Service
@AllArgsConstructor
@Slf4j
public class RegistrationService {

    private final RegistrationsRepository registrationRepository;

    private final RegistrationsMapper registrationsMapper;
    /**
     * Returns all workshops.
     *
     * @return a list of all workshops
     */
    public List<RegistrationsDto> getAllRegistrations() {
        return registrationRepository.findAll().stream()
                .map(registrationsMapper::RegistrationsToRegistrationsDto)
                .toList();
    }
    /**
     * Returns all registrations by workshop code.
     *
     * @param code the code of the workshop
     * @return the registrations with the given code
     */
    public List<RegistrationsDto> getRegistrationsByCode(String code) {
        return registrationRepository.findByWorkshopCode(code).stream()
                .map(registrationsMapper::RegistrationsToRegistrationsDto)
                .toList();
    }

    public RegistrationsDto createRegistration(RegistrationsDto registrationsDto, Authentication authentication) {
        String userName = "";
        String email = "";
        if (authentication instanceof JwtAuthenticationToken jwtAuth) {
            userName = jwtAuth.getTokenAttributes().get("preferred_username").toString();
            email = jwtAuth.getTokenAttributes().get("email").toString();
        }
        if(StringUtils.isBlank(userName) || StringUtils.isBlank(email)) {
            throw new InvalidUserException("User not found");
        }
        //TODO check for capacityof workshop
        log.debug("Getting workshop details for code {}", registrationsDto.getWorkshopCode());

        log.debug("Check if registration already exists {}", registrationsDto.getWorkshopCode());
        if(checkIfUserAlreadyRegistered(registrationsDto.getWorkshopCode(),userName)) {
            throw new UserAlreadyRegisteredException("User already registered");
        }
        registrationsDto.setUserName(userName);
        registrationsDto.setUserEmail(email);
        final Registrations registrationToSave = registrationsMapper.RegistrationsDtoToRegistrations(registrationsDto);
        Registrations savedRegistration = registrationRepository.save(registrationToSave);
        return registrationsMapper.RegistrationsToRegistrationsDto(savedRegistration);
    }

    private boolean checkIfUserAlreadyRegistered(@NotBlank(message = "Code cannot be empty") @Size(min = 5, max = 15, message = "Code of workshop must be of size 5-15") String workshopCode, String userName) {
        return registrationRepository.existsByWorkshopCodeAndUserName(workshopCode,userName);
    }

    @Transactional
    public void deleteRegistration(Integer id) {
        log.debug("Deleting registration with id {}", id);
        Registrations registrations = registrationRepository.findById(id)
                .orElseThrow(() -> new RegistrationDoesnotExistException("Registration not available with given id: " + id));
        registrationRepository.deleteById(id);
    }

    public List<RegistrationsResponseDto> getUserRegistrations(Authentication authentication) {
        String userName = "";
        if (authentication instanceof JwtAuthenticationToken jwtAuth) {
            userName = jwtAuth.getTokenAttributes().get("preferred_username").toString();
     }
        if(StringUtils.isBlank(userName)) {
            throw new InvalidUserException("User not found");
        }
        log.debug("Getting all registration details specific to user {}", userName);
        List<Registrations> registrations = registrationRepository.findByUserName(userName);
        if (CollectionUtils.isNotEmpty(registrations)) {
            return registrations.stream()
                    .map(registrationsMapper::RegistrationsToRegistrationsResponseDto)
                    .toList();
        }
        return List.of();
    }
}
