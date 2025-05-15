package eu.unite.recruiting.model.mapper;

import eu.unite.recruiting.model.dto.WorkshopDto;
import eu.unite.recruiting.model.entity.Workshop;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-05-15T23:21:26+0200",
    comments = "version: 1.6.3, compiler: javac, environment: Java 21.0.1 (Oracle Corporation)"
)
@Component
public class WorkshopMapperImpl implements WorkshopMapper {

    @Override
    public Workshop WorkshopDtoToWorkshop(WorkshopDto workshopDto) {
        if ( workshopDto == null ) {
            return null;
        }

        Workshop workshop = new Workshop();

        workshop.setCode( workshopDto.getCode() );
        workshop.setName( workshopDto.getName() );
        workshop.setDescription( workshopDto.getDescription() );
        workshop.setStartTime( workshopDto.getStartTime() );
        workshop.setEndTime( workshopDto.getEndTime() );
        if ( workshopDto.getCapacity() != null ) {
            workshop.setCapacity( workshopDto.getCapacity() );
        }

        return workshop;
    }

    @Override
    public WorkshopDto WorkshopToWorkshopDto(Workshop workshop) {
        if ( workshop == null ) {
            return null;
        }

        WorkshopDto workshopDto = new WorkshopDto();

        workshopDto.setCode( workshop.getCode() );
        workshopDto.setName( workshop.getName() );
        workshopDto.setDescription( workshop.getDescription() );
        workshopDto.setStartTime( workshop.getStartTime() );
        workshopDto.setEndTime( workshop.getEndTime() );
        workshopDto.setCapacity( workshop.getCapacity() );

        return workshopDto;
    }
}
