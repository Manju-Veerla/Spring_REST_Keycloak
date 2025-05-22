package com.app.model.mapper;

import com.app.model.entity.Workshop;
import com.app.model.request.WorkshopRequest;
import com.app.model.response.WorkshopResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Maps between Workshop entities and WorkshopRequest.
 */
@Mapper(componentModel = "spring", uses = RegistrationsMapper.class)
public interface WorkshopMapper {

    /**
     * Maps a WorkshopRequest to a Workshop entity.
     *
     * @param workshopDto the request to map
     * @return the mapped Workshop entity
     */
    Workshop WorkshopRequestToWorkshop(WorkshopRequest workshopDto);

    /**
     * Maps a Workshop entity to a WorkshopRequest.
     *
     * @param workshop the Workshop entity to map
     * @return the mapped WorkshopRequest
     */
    WorkshopRequest WorkshopToWorkshopRequest(Workshop workshop);


    @Mapping(target = "registrations", ignore = true)
    WorkshopResponse WorkshopToWorkshopWithoutRegistrationsResponse(Workshop workshop);


    WorkshopResponse WorkshopToWorkshopResponse(Workshop workshop);


}
