package eu.unite.recruiting.model.mapper;

import eu.unite.recruiting.model.dto.WorkshopDto;
import eu.unite.recruiting.model.entity.Workshop;
import org.mapstruct.Mapper;

/**
 * Maps between Workshop entities and WorkshopDto.
 */
@Mapper(componentModel = "spring")
public interface WorkshopMapper {

    /**
     * Maps a WorkshopDto to a Workshop entity.
     *
     * @param workshopDto the request to map
     * @return the mapped Workshop entity
     */
    Workshop WorkshopDtoToWorkshop(WorkshopDto workshopDto);

    /**
     * Maps a Workshop entity to a WorkshopDto.
     *
     * @param workshop the Workshop entity to map
     * @return the mapped WorkshopDto
     */
    WorkshopDto WorkshopToWorkshopDto(Workshop workshop);

}
