package eu.unite.recruiting.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import eu.unite.recruiting.TestData;
import eu.unite.recruiting.model.dto.RegistrationsDto;
import eu.unite.recruiting.model.dto.RegistrationsResponseDto;
import eu.unite.recruiting.service.RegistrationService;
import eu.unite.recruiting.service.WorkshopRegistrationService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RegistrationController.class)
class RegistrationControllerTest {

    public static final String REGISTRATIONS_ENDPOINT = "/api/v1/registrations";
    public static final String WORKSHOP_CODE = "/WS_100";
    public static final String USER_REGISTRATIONS_ENDPOINT = "/api/v1/user/registrations";
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RegistrationService registrationService;

    @MockitoBean
    private WorkshopRegistrationService workshopRegistrationService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(roles = "ADMIN")
    void getRegistrations_returnsList() throws Exception {
        RegistrationsDto dto = TestData.createRegistrationDto();
        Mockito.when(registrationService.getAllRegistrations()).thenReturn(List.of(dto));

        mockMvc.perform(get(REGISTRATIONS_ENDPOINT))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getRegistrationsByWorkshopCode_found() throws Exception {
        RegistrationsDto dto = TestData.createRegistrationDto();
        Mockito.when(workshopRegistrationService.getRegistrationsByCode("WS_100")).thenReturn(List.of(dto));

        mockMvc.perform(get(REGISTRATIONS_ENDPOINT + WORKSHOP_CODE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getRegistrationsByWorkshopCode_notFound() throws Exception {
        Mockito.when(workshopRegistrationService.getRegistrationsByCode("WS2")).thenReturn(Collections.emptyList());

        mockMvc.perform(get(REGISTRATIONS_ENDPOINT +"/WS2"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("No registrations found for workshop code WS2")));
    }

    @Test
    @WithMockUser(roles = "USER")
    void createRegistration_success() throws Exception {
        RegistrationsDto input = TestData.createRegistrationDto();
        RegistrationsDto saved = TestData.createRegistrationDto();
        Mockito.when(registrationService.createRegistration(any(RegistrationsDto.class), any(Authentication.class))).thenReturn(saved);

        mockMvc.perform(post(REGISTRATIONS_ENDPOINT)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(roles = "USER")
    void createRegistration_failure() throws Exception {
        RegistrationsDto input = TestData.createRegistrationDto();
        Mockito.when(registrationService.createRegistration(any(RegistrationsDto.class), any(Authentication.class))).thenReturn(null);

        mockMvc.perform(post(REGISTRATIONS_ENDPOINT)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteRegistration_success() throws Exception {
        mockMvc.perform(delete(REGISTRATIONS_ENDPOINT+"/1")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Registration deleted successfully :1")));
        Mockito.verify(registrationService).deleteRegistration(1);
    }

    @Test
    @WithMockUser(roles = "USER")
    void getUserRegistrations_found() throws Exception {
        RegistrationsResponseDto dto = TestData.createRegistrationResponseDto();
        Mockito.when(registrationService.getUserRegistrations(any(Authentication.class))).thenReturn(List.of(dto));

        mockMvc.perform(get(USER_REGISTRATIONS_ENDPOINT))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    @WithMockUser(roles = "USER")
    void getUserRegistrations_notFound() throws Exception {
        Mockito.when(registrationService.getUserRegistrations(any(Authentication.class))).thenReturn(Collections.emptyList());

        mockMvc.perform(get(USER_REGISTRATIONS_ENDPOINT))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("User has not Registered for any workshop")));
    }

    @Test
    void getUserRegistrations_unauthorized() throws Exception {
        mockMvc.perform(get(USER_REGISTRATIONS_ENDPOINT))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string(containsString("User is not authenticated")));
    }
}
