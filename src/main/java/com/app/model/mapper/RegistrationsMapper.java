package com.app.model.mapper;

import com.app.model.dto.RegistrationsDto;
import com.app.model.dto.RegistrationsResponseDto;
import com.app.model.entity.Registrations;
import org.mapstruct.Mapper;

/**
 * Maps between Registrations entities and RegistrationsDto.
 */
@Mapper(componentModel = "spring")
public interface RegistrationsMapper {

    /**
     * Maps a RegistrationsDto to a Registrations entity.
     *
     * @param registrationsDto the request to map
     * @return the mapped Registrations entity
     */
    Registrations RegistrationsDtoToRegistrations(RegistrationsDto registrationsDto);

    /**
     * Maps a Registrations entity to a RegistrationsDto.
     *
     * @param registrations the Registrations entity to map
     * @return the mapped RegistrationsDto
     */
    RegistrationsDto RegistrationsToRegistrationsDto(Registrations registrations);

    /**
     * Maps a Registrations entity to a RegistrationsResponseDto.
     *
     * @param registrations the Registrations entity to map
     * @return the mapped RegistrationsResponseDto
     */
    RegistrationsResponseDto RegistrationsToRegistrationsResponseDto(Registrations registrations);

}
