package eu.unite.recruiting.controller;

import eu.unite.recruiting.model.dto.RegistrationsDto;
import eu.unite.recruiting.model.dto.RegistrationsResponseDto;
import eu.unite.recruiting.service.RegistrationService;
import eu.unite.recruiting.service.WorkshopRegistrationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
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

/**
 * Controller for the registrations endpoint
 */
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Slf4j
public class RegistrationController {

    // The registration service
    private final RegistrationService registrationService;
    // The workshop registration service
    private final WorkshopRegistrationService workshopRegistrationService;

    /**
     * Get all registrations
     * @return  a list of all registrations
     */
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping(value = "/registrations", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get all registrations", description = "Retrieves a list of all registrations.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list of registrations"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public List<RegistrationsDto> getRegistrations() {
        log.info("Getting all registration details ");
        return registrationService.getAllRegistrations();
    }

    /**
     * Get all registrations by workshop code
     * @param workshopCode  the code of the workshop
     * @return a list of all registrations for the workshop
     */
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping(value = "/registrations/{workshopCode}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get all registrations for a workshop", description = "Retrieves a list of all registrations for a workshop.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list of registrations"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<?> getRegistrations(@PathVariable String workshopCode) {
        log.info("Getting registration with workshop code  {} ", workshopCode);
        List<RegistrationsDto> registrations = workshopRegistrationService.getRegistrationsByCode(workshopCode);
        if (CollectionUtils.isNotEmpty(registrations)) {
            return ResponseEntity.status(HttpStatus.OK).body(registrations);
        } else {
            log.debug("No registrations found for workshop code {}" , workshopCode);
            return ResponseEntity.status(HttpStatus.OK).body("No registrations found for workshop code " + workshopCode);
        }
    }

    /**
     * Create a new registration
     * @param registrationsDto the registration data
     * @param authentication   the authentication object
     * @return the created registration
     */
    @PreAuthorize("hasRole('USER')")
    @PostMapping(value = "/registrations", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Register user for a workshop", description = "Register user for a workshop")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Register user for a workshop",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = RegistrationsDto.class))}),
            @ApiResponse(responseCode = "400", description = "Registration not successful", content = @Content)})
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
    @Operation(summary = "Delete a user registration", description = "Deletes a user registration using its id.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User registration deleted successfully"),
            @ApiResponse(responseCode = "404", description = "User registration not found"),
            @ApiResponse(responseCode = "400", description = "Invalid User registration id")
    })
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
            @ApiResponse(responseCode = "401", description = "User is not authenticated"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<?> getUserRegistrations(Authentication authentication) {
        log.info("Getting all registration details specific to user");
        if (null != authentication) {
            List<RegistrationsResponseDto> userRegistrations = registrationService.getUserRegistrations(authentication);
            if (CollectionUtils.isNotEmpty(userRegistrations)) {
                return ResponseEntity.status(HttpStatus.OK).body(userRegistrations);
            } else {
                log.debug("User has not Registered for any workshop {}" , authentication.getName());
                return ResponseEntity.status(HttpStatus.OK).body("User has not Registered for any workshop");
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User is not authenticated");
        }
    }

}
