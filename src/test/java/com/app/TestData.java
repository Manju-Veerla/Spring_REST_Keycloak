package com.app;

import com.app.model.entity.PreferredContact;
import com.app.model.request.RegistrationsRequest;
import com.app.model.request.WorkshopRequest;
import com.app.model.response.RegistrationsResponse;
import com.app.model.entity.Workshop;
import com.app.model.response.WorkshopResponse;

import java.time.ZonedDateTime;
import java.util.List;

public class TestData {

    public static RegistrationsRequest createRegistrationRequest() {
        return RegistrationsRequest.builder().
                registrationId(1)
                .workshopCode("WS_100")
                .userName("Test User")
                .userEmail("test@example.com")
                .userPhone("1234567890")
                .userPreferredContact(PreferredContact.EMAIL)
                .build();
    }

    public static WorkshopRequest newWorkshopRequest() {
        return WorkshopRequest.builder().
                code("WS_1000")
                .name("Workshop 1000")
                .description("Description of Workshop 1")
                .capacity(10)
                .startTime(ZonedDateTime.now().plusDays(10))
                .endTime(ZonedDateTime.now().plusDays(11))
                .build();
    }

    public static RegistrationsResponse createRegistrationResponse() {
        return RegistrationsResponse.builder().
                registrationId(1)
                .workshopCode("WS_100")
                .userPhone("1234567890")
                .userPreferredContact(PreferredContact.EMAIL)
                .build();
    }

    public static WorkshopRequest createWorkshopRequest(Workshop workshop) {
        return WorkshopRequest.builder().
                code(workshop.getCode())
                .name(workshop.getName())
                .description(workshop.getDescription())
                .capacity(workshop.getCapacity())
                .startTime(workshop.getStartTime())
                .endTime(workshop.getEndTime())
                .build();
    }

    public static WorkshopResponse createWorkshopResponse(Workshop workshop) {
        return WorkshopResponse.builder().
                code(workshop.getCode())
                .name(workshop.getName())
                .description(workshop.getDescription())
                .capacity(workshop.getCapacity())
                .startTime(workshop.getStartTime())
                .endTime(workshop.getEndTime())
                .build();
    }

    public static WorkshopResponse createWorkshopResponse() {
        return WorkshopResponse.builder().
                code("WS_100")
                .name("TEST WORKSHOP")
                .description("Test Description")
                .capacity(10)
                .startTime(ZonedDateTime.now().plusDays(10))
                .endTime(ZonedDateTime.now().plusDays(11))
                .build();
    }

    public static List<RegistrationsRequest> getRegistrationsList() {
        return List.of(
                RegistrationsRequest.builder()
                        .registrationId(1)
                        .workshopCode("WS_100")
                        .userName("Test User")
                        .userEmail("test@example.com")
                        .build(),
                RegistrationsRequest.builder()
                        .registrationId(2)
                        .workshopCode("WS_200")
                        .userName("Test User 2")
                        .userEmail("test2@example.com")
                        .build(),
                RegistrationsRequest.builder()
                        .registrationId(3)
                        .workshopCode("WS_300")
                        .userName("Test User 3")
                        .userEmail("test3@example.com")
                        .build());
    }

}
