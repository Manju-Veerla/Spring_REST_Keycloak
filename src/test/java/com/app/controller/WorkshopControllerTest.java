package com.app.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.app.TestData;
import com.app.model.dto.WorkshopDto;
import com.app.model.dto.WorkshopUpdateDto;
import com.app.service.WorkshopService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collections;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(WorkshopController.class)
class WorkshopControllerTest {

    public static final String WORKSHOP_ENDPOINT = "/api/v1/workshops";
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private WorkshopService workshopService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser
    void getUpcomingWorkshops_returnsWorkshops() throws Exception {
        WorkshopDto dto = TestData.createWorkshopDto();
        Mockito.when(workshopService.getUpcomingWorkshops()).thenReturn(Arrays.asList(dto));

        mockMvc.perform(get(WORKSHOP_ENDPOINT + "/upcoming"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getWorkshops_returnsWorkshops() throws Exception {
        WorkshopDto dto = TestData.createWorkshopDto();
        Mockito.when(workshopService.getAllWorkshops()).thenReturn(Collections.singletonList(dto));

        mockMvc.perform(get(WORKSHOP_ENDPOINT))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createWorkshop_success() throws Exception {
        WorkshopDto input = TestData.newWorkshopDto();
        WorkshopDto saved = TestData.newWorkshopDto();
        Mockito.when(workshopService.createWorkshop(any(WorkshopDto.class))).thenReturn(saved);

        mockMvc.perform(post(WORKSHOP_ENDPOINT)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createWorkshop_failure() throws Exception {
        WorkshopDto input = TestData.createWorkshopDto();
        Mockito.when(workshopService.createWorkshop(any(WorkshopDto.class))).thenReturn(null);

        mockMvc.perform(post(WORKSHOP_ENDPOINT)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void getWorkshop_found() throws Exception {
        WorkshopDto dto = TestData.createWorkshopDto();
        Mockito.when(workshopService.getWorkshopByCode("WS_100")).thenReturn(dto);

        mockMvc.perform(get(WORKSHOP_ENDPOINT + "/WS_100"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = {"ADMIN", "USER"})
    void getWorkshop_notFound() throws Exception {
        Mockito.when(workshopService.getWorkshopByCode("WS_1000")).thenReturn(null);

        mockMvc.perform(get(WORKSHOP_ENDPOINT + "/WS_1000"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteWorkshop_success() throws Exception {
        mockMvc.perform(delete(WORKSHOP_ENDPOINT + "/WS_200").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("WS_200")));
        Mockito.verify(workshopService).deleteWorkshop("WS_200");
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateWorkshop_success() throws Exception {
        WorkshopUpdateDto updateDto = new WorkshopUpdateDto();
        WorkshopDto updated = TestData.createWorkshopDto();
        Mockito.when(workshopService.updateWorkshop(eq("WS_100"), any(WorkshopUpdateDto.class))).thenReturn(updated);

        mockMvc.perform(put(WORKSHOP_ENDPOINT + "/WS_100")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateWorkshop_failure() throws Exception {
        WorkshopUpdateDto updateDto = new WorkshopUpdateDto();
        Mockito.when(workshopService.updateWorkshop(eq("WS_100"), any(WorkshopUpdateDto.class))).thenReturn(null);

        mockMvc.perform(put(WORKSHOP_ENDPOINT + "/WS_100")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isBadRequest());
    }
}
