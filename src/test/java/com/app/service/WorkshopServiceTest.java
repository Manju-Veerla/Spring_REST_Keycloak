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
import org.apache.commons.lang3.RandomStringUtils;
import org.mockito.*;
import org.testng.annotations.*;
import org.testng.ITestContext;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.Optional;

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

    private AutoCloseable closeable;

    @BeforeClass
    public void initMocks(ITestContext context) {
        closeable = MockitoAnnotations.openMocks(this);
    }

    @AfterClass
    public void releaseMocks() throws Exception {
        if (closeable != null) {
            closeable.close();
        }
    }

    @BeforeMethod
    public void setUp() {
        // Reset mocks
        Mockito.reset(workshopRepository, workshopMapper, workshopRegistrationService);

        // Setup test data
        workshop = new Workshop();
        workshop.setWorkshopId(Math.abs(UUID.randomUUID().hashCode()));
        workshop.setCode("WS_"+ RandomStringUtils.randomNumeric(3));
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
    public void createWorkshop_success() {
        // Given
        when(workshopRepository.existsWorkshopByCode(workshopRequest.getCode())).thenReturn(false);
        when(workshopMapper.WorkshopRequestToWorkshop(workshopRequest)).thenReturn(workshop);
        when(workshopRepository.save(workshop)).thenReturn(workshop);
        when(workshopMapper.WorkshopToWorkshopResponse(workshop)).thenReturn(workshopResponse);

        // When
        WorkshopRequest result = workshopService.createWorkshop(workshopRequest);

        // Then
        assertNotNull(result, "Workshop response should not be null");
        assertEquals(workshopResponse, result, "Returned workshop response should match the expected one");
        
        verify(workshopRepository, times(1)).existsWorkshopByCode(workshopRequest.getCode());
        verify(workshopMapper, times(1)).WorkshopRequestToWorkshop(workshopRequest);
        verify(workshopRepository, times(1)).save(workshop);
        verify(workshopMapper, times(1)).WorkshopToWorkshopResponse(workshop);
        
        // Verify no more interactions
        verifyNoMoreInteractions(workshopRepository, workshopMapper);
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
