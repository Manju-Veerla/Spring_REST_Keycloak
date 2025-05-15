package eu.unite.recruiting.controller;

import eu.unite.recruiting.model.dto.WorkshopDto;
import eu.unite.recruiting.service.WorkshopService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.ZonedDateTime;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(WorkshopController.class) // Change this line
public class WorkshopControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private WorkshopService workshopService;

    @InjectMocks
    private WorkshopController workshopController;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCreateWorkshop() throws Exception {
        // Prepare test data
        WorkshopDto workshopDto = new WorkshopDto();
        workshopDto.setCode("testCode");
        workshopDto.setName("Test Workshop");
        workshopDto.setStartTime(ZonedDateTime.parse("2025-05-15T10:00:00Z"));
        workshopDto.setEndTime(ZonedDateTime.parse("2025-05-15T12:00:00Z"));

        // Mock the service call
        when(workshopService.createWorkshop(any(WorkshopDto.class))).thenReturn(workshopDto);

        // Perform the request and verify the response
        mockMvc.perform(post("/api/v1/workshops")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"code\":\"testCode\",\"name\":\"Test Workshop\",\"startTime\":\"2025-05-15T10:00:00Z\",\"endTime\":\"2025-05-15T12:00:00Z\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value("testCode"));
    }

    @Test
    public void testGetWorkshop() throws Exception {
        // Prepare test data
        WorkshopDto workshopDto = new WorkshopDto();
        workshopDto.setCode("testCode");
        workshopDto.setName("Test Workshop");

        // Mock the service call
        when(workshopService.getWorkshopByCode("testCode")).thenReturn(workshopDto);

        // Perform the request and verify the response
        mockMvc.perform(get("/api/v1/workshops/testCode")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("testCode"));
    }

    @Test
    public void testDeleteWorkshop() throws Exception {
        // Perform the request and verify the response
        mockMvc.perform(delete("/api/v1/workshops/testCode")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // Verify the service call
        verify(workshopService, times(1)).deleteWorkshop("testCode");
    }
}