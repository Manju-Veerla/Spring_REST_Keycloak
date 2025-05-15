package eu.unite.recruiting.controller;

import eu.unite.recruiting.model.dto.RegistrationsDto;
import eu.unite.recruiting.model.dto.RegistrationsResponseDto;
import eu.unite.recruiting.service.RegistrationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Slf4j
public class RegistrationController {

    private final RegistrationService registrationService;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping(value = "/registrations", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<RegistrationsDto> getRegistrations() {
        log.info("Getting all registration details ");
        return registrationService.getAllRegistrations();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping(value = "/registrations/{workshopCode}", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<RegistrationsDto> getRegistrations(@PathVariable String workshopCode) {
        log.info("Getting registration with workshop code  {} ", workshopCode);
        return registrationService.getRegistrationsByCode(workshopCode);
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping(value = "/registrations", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<RegistrationsDto> createRegistration(@RequestBody RegistrationsDto registrationsDto, Authentication authentication) {
        log.info("Registration Request data {}", registrationsDto);
        RegistrationsDto registrationSaved = registrationService.createRegistration(registrationsDto, authentication);
        if (null != registrationSaved) {
            return ResponseEntity.status(HttpStatus.CREATED).body(registrationSaved);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping(value = "/registrations/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> deleteRegistration(@PathVariable Integer id) {
        log.info("Deleting registration with id  {} ", id);
        registrationService.deleteRegistration(id);
        return ResponseEntity.status(HttpStatus.OK).body("Registration deleted successfully :" + id);
    }

    /**
     * Get all user registrations
     *
     * @return a list of all user registrations
     */
    @PreAuthorize("hasRole('USER')")
    @GetMapping(value = "/user/registrations", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get all user registrations", description = "Retrieves a list of all registrations specific to user.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list of registrations specific to user"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<?> getUserRegistrations(Authentication authentication) {
        log.info("Getting all registration details specific to user");
        if (null != authentication) {
            List<RegistrationsResponseDto> userRegistrations = registrationService.getUserRegistrations(authentication);
            if (CollectionUtils.isNotEmpty(userRegistrations)) {
                return ResponseEntity.status(HttpStatus.OK).body(userRegistrations);
            } else {
                log.debug("User has no userRegistrations");
                return ResponseEntity.status(HttpStatus.OK).body("User has no userRegistrations");
            }
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User is not authenticated");
        }
    }

}
