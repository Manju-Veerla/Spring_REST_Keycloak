package eu.unite.recruiting.model.mapper;

import eu.unite.recruiting.model.dto.RegistrationsDto;
import eu.unite.recruiting.model.dto.RegistrationsResponseDto;
import eu.unite.recruiting.model.entity.Registrations;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

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
