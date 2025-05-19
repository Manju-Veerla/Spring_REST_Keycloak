package com.app.controller;

import com.app.model.dto.WorkshopDto;
import com.app.model.dto.WorkshopUpdateDto;
import com.app.service.WorkshopService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller for the workshops endpoint
 */
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Slf4j
public class WorkshopController {
    /**
     * The workshop service
     */
    private final WorkshopService workshopService;

    /**
     * Get all upcoming workshops
     *
     * @return a list of all upcoming workshops.
     */
    @GetMapping(value = "/workshops/upcoming", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get all upcoming workshops", description = "Retrieves a list of all  upcoming workshops.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list of upcoming workshops"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public List<WorkshopDto> getUpcomingWorkshops() {
        log.info("Getting all upcoming workshop details ");
        return workshopService.getUpcomingWorkshops();

    }

    /**
     * Get all workshops
     *
     * @return a list of all workshops.
     */
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping(value = "/workshops", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get all workshops", description = "Retrieves a list of all workshops.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list of workshops"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public List<WorkshopDto> getWorkshops() {
        log.info("Getting all workshop details by admin user");
        return workshopService.getAllWorkshops();
    }

    /**
     * Create a new workshop when the user is an admin.
     *
     * @param workshopDto the request that contains the workshop data.
     * @return the newly created workshop.
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(value = "/workshops", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Create a new workshop", description = "Create a new workshop")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Create a new workshop",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = WorkshopDto.class))}),
            @ApiResponse(responseCode = "400", description = "Workshop not created", content = @Content)})
    public ResponseEntity<WorkshopDto> createWorkshop(@Valid @RequestBody WorkshopDto workshopDto) {
        log.info("Workshop Request data {}", workshopDto);
        WorkshopDto workshopSaved = workshopService.createWorkshop(workshopDto);
        if (null != workshopSaved) {
            return ResponseEntity.status(HttpStatus.CREATED).body(workshopSaved);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    /**
     * Get a workshop by code
     *
     * @param workshopCode the code of the workshop to retrieve.
     * @return the workshop with the specified code.
     */
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    @GetMapping(value = "/workshops/{workshopCode}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get a workshop by code", description = "Retrieves a workshop's details using its code.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved workshop details",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = WorkshopDto.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid workshop code")
    })
    public ResponseEntity<WorkshopDto> getWorkshop(@PathVariable String workshopCode) {
        log.info("Getting workshop with code  {} ", workshopCode);
        // Get the workshop details using the provided code
        WorkshopDto workshopDto = workshopService.getWorkshopByCode(workshopCode);
        if (null != workshopDto) {
            return ResponseEntity.status(HttpStatus.OK).body(workshopDto);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    /**
     * Delete a workshop by code
     *
     * @param workshopCode the code of the workshop to delete
     * @return a response indicating the result of the deletion
     */
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping(value = "/workshops/{workshopCode}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Delete a workshop", description = "Deletes a workshop using its code.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Workshop deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Workshop not found"),
            @ApiResponse(responseCode = "400", description = "Invalid workshop code")
    })
    public ResponseEntity<String> deleteWorkshop(@PathVariable String workshopCode) {
        log.info("Deleting workshop with code  {} ", workshopCode);
        workshopService.deleteWorkshop(workshopCode);
        return ResponseEntity.status(HttpStatus.OK).body("Workshop deleted successfully :" + workshopCode);
    }

    /**
     * Update a workshop
     *
     * @param workshopCode the code of the workshop to update
     * @param workshopDto  the new workshop data
     * @return the updated workshop
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping(value = "/workshops/{workshopCode}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Update a workshop", description = "Updates the details of a workshop using its code.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Workshop updated successfully",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = WorkshopDto.class))}),
            @ApiResponse(responseCode = "404", description = "Workshop not found"),
            @ApiResponse(responseCode = "400", description = "Invalid workshop code or data")
    })
    public ResponseEntity<WorkshopDto> updateWorkshop(@PathVariable String workshopCode, @Valid @RequestBody WorkshopUpdateDto workshopDto) {
        log.info("Updating workshop with code  {} ", workshopCode);
        WorkshopDto workshopSaved = workshopService.updateWorkshop(workshopCode, workshopDto);
        if (null != workshopSaved) {
            return ResponseEntity.status(HttpStatus.OK).body(workshopSaved);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
}
