package com.app.model.mapper;

import com.app.model.entity.PreferredContact;
import com.app.model.entity.Registrations;
import com.app.model.request.RegistrationsRequest;
import com.app.model.response.RegistrationsResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

/**
 * Maps between Registrations entities and RegistrationsRequest.
 */
@Mapper(componentModel = "spring")
public interface RegistrationsMapper {

    @Mapping(target = "userPreferredContact", source = "userPreferredContact", qualifiedByName = "mapToPreferredContact")
    Registrations RegistrationsRequestToRegistrations(RegistrationsRequest registrationsDto);

    @Mapping(target = "userPreferredContact", source = "userPreferredContact", qualifiedByName = "mapPreferredContactToString")
    RegistrationsResponse RegistrationsToRegistrationsResponse(Registrations registrations);

    @Named("mapToPreferredContact")
    default PreferredContact mapToPreferredContact(PreferredContact value) {
        return value; // Direct mapping since we're already using the enum
    }

    @Named("mapPreferredContactToString")
    default PreferredContact mapPreferredContactToString(PreferredContact value) {
        return value; // Direct mapping since we're using the enum in response too
    }
}
