package eu.unite.recruiting.service;

import eu.unite.recruiting.TestData;
import eu.unite.recruiting.exception.InvalidWorkshopDataException;
import eu.unite.recruiting.exception.WorkshopAlreadyExistException;
import eu.unite.recruiting.exception.WorkshopNotFoundException;
import eu.unite.recruiting.model.dto.WorkshopDto;
import eu.unite.recruiting.model.dto.WorkshopUpdateDto;
import eu.unite.recruiting.model.entity.Workshop;
import eu.unite.recruiting.model.mapper.WorkshopMapper;
import eu.unite.recruiting.repository.WorkshopRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.time.ZonedDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class WorkshopServiceTest {

    @Mock
    private WorkshopRepository workshopRepository;

    @Mock
    private WorkshopMapper workshopMapper;

    @Mock
    private WorkshopRegistrationService workshopRegistrationService;

    @InjectMocks
    private WorkshopService workshopService;

    private Workshop workshop;
    private WorkshopDto workshopDto;
    private WorkshopUpdateDto updateDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        workshop = new Workshop();
        workshop.setCode("WS_1000");
        workshop.setName("Test Workshop");
        workshop.setDescription("Test Description");
        workshop.setStartTime(ZonedDateTime.now().plusDays(1));
        workshop.setEndTime(ZonedDateTime.now().plusDays(2));
        workshop.setCapacity(10);

        workshopDto = TestData.createWorkshopDto(workshop);

        updateDto = new WorkshopUpdateDto();
        updateDto.setName("Updated Workshop");
        updateDto.setDescription("Updated Description");
        updateDto.setStartTime(ZonedDateTime.now().plusDays(3));
        updateDto.setEndTime(ZonedDateTime.now().plusDays(4));
        updateDto.setCapacity(20);
    }

    @Test
    @DisplayName("Should return only upcoming workshops")
    void getUpcomingWorkshops_returnsUpcoming() {
        Workshop pastWorkshop = new Workshop();
        pastWorkshop.setEndTime(ZonedDateTime.now().minusDays(1));
        when(workshopRepository.findAll()).thenReturn(List.of(workshop, pastWorkshop));
        when(workshopMapper.WorkshopToWorkshopDto(any())).thenReturn(workshopDto);

        List<WorkshopDto> result = workshopService.getUpcomingWorkshops();

        assertEquals(1, result.size());
        verify(workshopRepository).findAll();
    }

    @Test
    @DisplayName("Should return all workshops")
    void getAllWorkshops_returnsAll() {
        when(workshopRepository.findAll()).thenReturn(List.of(workshop));
        when(workshopMapper.WorkshopToWorkshopDto(any())).thenReturn(workshopDto);

        List<WorkshopDto> result = workshopService.getAllWorkshops();

        assertEquals(1, result.size());
        verify(workshopRepository).findAll();
    }

    @Test
    @DisplayName("Should create a new workshop")
    void createWorkshop_success() {
        when(workshopRepository.existsWorkshopByCode("WS_200")).thenReturn(false);
        when(workshopMapper.WorkshopDtoToWorkshop(any())).thenReturn(workshop);
        when(workshopRepository.save(any())).thenReturn(workshop);
        when(workshopMapper.WorkshopToWorkshopDto(any())).thenReturn(workshopDto);

        WorkshopDto result = workshopService.createWorkshop(workshopDto);

        assertNotNull(result);
        verify(workshopRepository).save(any());
    }

    @Test
    @DisplayName("Should throw if workshop code exists")
    void createWorkshop_alreadyExists() {
        when(workshopRepository.existsWorkshopByCode("WS_100")).thenReturn(true);

        assertThrows(WorkshopAlreadyExistException.class,
                () -> workshopService.createWorkshop(workshopDto));
    }

    @Test
    @DisplayName("Should throw if start time is after end time")
    void createWorkshop_invalidTimes() {
        workshopDto.setStartTime(ZonedDateTime.now().plusDays(2));
        workshopDto.setEndTime(ZonedDateTime.now().plusDays(1));
        when(workshopRepository.existsWorkshopByCode("WS_100")).thenReturn(false);

        assertThrows(InvalidWorkshopDataException.class,
                () -> workshopService.createWorkshop(workshopDto));
    }

    @Test
    @DisplayName("Should get workshop by code")
    void getWorkshopByCode_success() {
        when(workshopRepository.findByCode("WS_100")).thenReturn(Optional.of(workshop));
        when(workshopMapper.WorkshopToWorkshopDto(any())).thenReturn(workshopDto);

        WorkshopDto result = workshopService.getWorkshopByCode("WS_100");

        assertNotNull(result);
    }

    @Test
    @DisplayName("Should throw if workshop not found by code")
    void getWorkshopByCode_notFound() {
        when(workshopRepository.findByCode("WS_100")).thenReturn(Optional.empty());

        assertThrows(WorkshopNotFoundException.class,
                () -> workshopService.getWorkshopByCode("WS_100"));
    }

    @Test
    @DisplayName("Should delete workshop if no registrations")
    void deleteWorkshop_success() {
        when(workshopRepository.findByCode("WS_100")).thenReturn(Optional.of(workshop));
        when(workshopRegistrationService.getRegistrationsByCode("WS_200")).thenReturn(Collections.emptyList());

        workshopService.deleteWorkshop("WS_100");

        verify(workshopRepository).delete(workshop);
    }

    @Test
    @DisplayName("Should throw if deleting workshop with registrations")
    void deleteWorkshop_withRegistrations() {
        when(workshopRepository.findByCode("WS_200")).thenReturn(Optional.of(workshop));
        when(workshopRegistrationService.getRegistrationsByCode("WS_200")).
                thenReturn(Collections.singletonList(TestData.createRegistrationDto()));

        assertThrows(InvalidWorkshopDataException.class,
                () -> workshopService.deleteWorkshop("WS_200"));
    }

    @Test
    @DisplayName("Should throw if deleting non-existent workshop")
    void deleteWorkshop_notFound() {
        when(workshopRepository.findByCode("WS_1AA")).thenReturn(Optional.empty());

        assertThrows(WorkshopNotFoundException.class,
                () -> workshopService.deleteWorkshop("WS_1AA"));
    }

    @Test
    @DisplayName("Should update workshop")
    void updateWorkshop_success() {
        when(workshopRepository.findByCode("WS_100")).thenReturn(Optional.of(workshop));
        when(workshopRepository.save(any())).thenReturn(workshop);
        when(workshopMapper.WorkshopToWorkshopDto(any())).thenReturn(workshopDto);

        WorkshopDto result = workshopService.updateWorkshop("WS_100", updateDto);

        assertNotNull(result);
        verify(workshopRepository).save(any());
    }

    @Test
    @DisplayName("Should throw if updating non-existent workshop")
    void updateWorkshop_notFound() {
        when(workshopRepository.findByCode("WS_1AA")).thenReturn(Optional.empty());

        assertThrows(WorkshopNotFoundException.class,
                () -> workshopService.updateWorkshop("WS_1AA", updateDto));
    }
}
