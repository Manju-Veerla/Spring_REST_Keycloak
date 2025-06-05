package com.app.service;

import com.app.TestData;
import com.app.exception.InvalidWorkshopDataException;
import com.app.exception.WorkshopAlreadyExistException;
import com.app.exception.WorkshopNotFoundException;
import com.app.model.request.WorkshopRequest;
import com.app.model.request.WorkshopUpdateRequest;
import com.app.model.entity.Workshop;
import com.app.model.mapper.WorkshopMapper;
import com.app.model.response.WorkshopResponse;
import com.app.repository.WorkshopRepository;
import org.junit.jupiter.api.BeforeEach;
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
    private WorkshopRequest workshopRequest;

    private WorkshopResponse workshopResponse;
    private WorkshopUpdateRequest workshopUpdateRequest;

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

        workshopRequest = TestData.createWorkshopRequest(workshop);
        workshopResponse = TestData.createWorkshopResponse(workshop);

        workshopUpdateRequest = new WorkshopUpdateRequest();
        workshopUpdateRequest.setName("Updated Workshop");
        workshopUpdateRequest.setDescription("Updated Description");
        workshopUpdateRequest.setStartTime(ZonedDateTime.now().plusDays(3));
        workshopUpdateRequest.setEndTime(ZonedDateTime.now().plusDays(4));
        workshopUpdateRequest.setCapacity(20);
    }

    @Test
    void getUpcomingWorkshops_returnsUpcoming() {
        Workshop pastWorkshop = new Workshop();
        pastWorkshop.setEndTime(ZonedDateTime.now().minusDays(1));
        when(workshopRepository.findAll()).thenReturn(List.of(workshop, pastWorkshop));
        when(workshopMapper.WorkshopToWorkshopResponse(any())).thenReturn(workshopResponse);

        List<WorkshopResponse> result = workshopService.getUpcomingWorkshops();

        assertEquals(1, result.size());
        verify(workshopRepository).findAll();
    }

    @Test
    void getAllWorkshops_returnsAll() {
        when(workshopRepository.findAll()).thenReturn(List.of(workshop));
        when(workshopMapper.WorkshopToWorkshopResponse(any())).thenReturn(workshopResponse);

        List<WorkshopResponse> result = workshopService.getAllWorkshops();

        assertEquals(1, result.size());
        verify(workshopRepository).findAll();
    }

    @Test
    void createWorkshop_success() {
        when(workshopRepository.existsWorkshopByCode("WS_200")).thenReturn(false);
        when(workshopMapper.WorkshopRequestToWorkshop(any())).thenReturn(workshop);
        when(workshopRepository.save(any())).thenReturn(workshop);
        when(workshopMapper.WorkshopToWorkshopResponse(any())).thenReturn(workshopResponse);

        WorkshopRequest result = workshopService.createWorkshop(workshopRequest);

        assertNotNull(result);
        verify(workshopRepository).save(any());
    }

    @Test
    void createWorkshop_alreadyExists() {
        when(workshopRepository.existsWorkshopByCode("WS_100")).thenReturn(true);

        assertThrows(WorkshopAlreadyExistException.class,
                () -> workshopService.createWorkshop(workshopRequest));
    }

    @Test
    void createWorkshop_invalidTimes() {
        workshopRequest.setStartTime(ZonedDateTime.now().plusDays(2));
        workshopRequest.setEndTime(ZonedDateTime.now().plusDays(1));
        when(workshopRepository.existsWorkshopByCode("WS_100")).thenReturn(false);

        assertThrows(InvalidWorkshopDataException.class,
                () -> workshopService.createWorkshop(workshopRequest));
    }

    @Test
    void getWorkshopByCode_success() {
        when(workshopRepository.findByCode("WS_100")).thenReturn(Optional.of(workshop));
        when(workshopMapper.WorkshopToWorkshopResponse(any())).thenReturn(workshopResponse);

        WorkshopResponse result = workshopService.getWorkshopByCode("WS_100");

        assertNotNull(result);
    }

    @Test
    void getWorkshopByCode_notFound() {
        when(workshopRepository.findByCode("WS_100")).thenReturn(Optional.empty());

        assertThrows(WorkshopNotFoundException.class,
                () -> workshopService.getWorkshopByCode("WS_100"));
    }

    @Test
    void deleteWorkshop_success() {
        when(workshopRepository.findByCode("WS_100")).thenReturn(Optional.of(workshop));
        when(workshopRegistrationService.getRegistrationsByCode("WS_200")).thenReturn(Collections.emptyList());

        workshopService.deleteWorkshop("WS_100");

        verify(workshopRepository).delete(workshop);
    }

    @Test
    void deleteWorkshop_withRegistrations() {
        when(workshopRepository.findByCode("WS_200")).thenReturn(Optional.of(workshop));
        when(workshopRegistrationService.getRegistrationsByCode("WS_200")).
                thenReturn(Collections.singletonList(TestData.createRegistrationResponse()));

        assertThrows(InvalidWorkshopDataException.class,
                () -> workshopService.deleteWorkshop("WS_200"));
    }

    @Test
    void deleteWorkshop_notFound() {
        when(workshopRepository.findByCode("WS_1AA")).thenReturn(Optional.empty());

        assertThrows(WorkshopNotFoundException.class,
                () -> workshopService.deleteWorkshop("WS_1AA"));
    }

    @Test
    void updateWorkshop_success() {
        when(workshopRepository.findByCode("WS_100")).thenReturn(Optional.of(workshop));
        when(workshopRepository.save(any())).thenReturn(workshop);
        when(workshopMapper.WorkshopToWorkshopResponse(any())).thenReturn(workshopResponse);

        WorkshopResponse result = workshopService.updateWorkshop("WS_100", workshopUpdateRequest);

        assertNotNull(result);
        verify(workshopRepository).save(any());
    }

    @Test
    void updateWorkshop_notFound() {
        when(workshopRepository.findByCode("WS_1AA")).thenReturn(Optional.empty());

        assertThrows(WorkshopNotFoundException.class,
                () -> workshopService.updateWorkshop("WS_1AA", workshopUpdateRequest));
    }
}
