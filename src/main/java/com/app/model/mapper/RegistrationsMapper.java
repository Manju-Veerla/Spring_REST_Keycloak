package com.app.model.mapper;

import com.app.model.entity.Registrations;
import com.app.model.request.RegistrationsRequest;
import com.app.model.response.RegistrationsResponse;
import org.mapstruct.Mapper;

/**
 * Maps between Registrations entities and RegistrationsRequest.
 */
@Mapper(componentModel = "spring")
public interface RegistrationsMapper {

    Registrations RegistrationsRequestToRegistrations(RegistrationsRequest registrationsDto);

    RegistrationsResponse RegistrationsToRegistrationsResponse(Registrations registrations);

}
