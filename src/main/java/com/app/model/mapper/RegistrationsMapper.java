package com.app.model.mapper;

import com.app.model.request.RegistrationsRequest;
import com.app.model.response.RegistrationsResponse;
import com.app.model.entity.Registrations;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.Set;

/**
 * Maps between Registrations entities and RegistrationsRequest.
 */
@Mapper(componentModel = "spring")
public interface RegistrationsMapper {

    RegistrationsMapper INSTANCE = Mappers.getMapper(RegistrationsMapper.class);

    /**
     * Maps a RegistrationsRequest to a Registrations entity.
     *
     * @param registrationsDto the request to map
     * @return the mapped Registrations entity
     */
    Registrations RegistrationsRequestToRegistrations(RegistrationsRequest registrationsDto);

    /**
     * Maps a Registrations entity to a RegistrationsRequest.
     *
     * @param registrations the Registrations entity to map
     * @return the mapped RegistrationsRequest
     */
    RegistrationsRequest RegistrationsToRegistrationsRequest(Registrations registrations);

    /**
     * Maps a Registrations entity to a RegistrationsResponse.
     *
     * @param registrations the Registrations entity to map
     * @return the mapped RegistrationsResponse
     */
    @Mapping(target = "workshopCode", source = "workshop.code")
    RegistrationsResponse RegistrationsToRegistrationsResponse(Registrations registrations);

    Set<RegistrationsResponse> RegistrationsSetToRegistrationsResponseSet(Set<Registrations> registrations);

}
