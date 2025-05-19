package com.app;

import com.app.model.dto.RegistrationsDto;
import com.app.model.dto.RegistrationsResponseDto;
import com.app.model.dto.WorkshopDto;
import com.app.model.entity.Workshop;

import java.time.ZonedDateTime;
import java.util.List;

public class TestData {

    public static RegistrationsDto createRegistrationDto() {
        return RegistrationsDto.builder().
                id(1)
                .workshopCode("WS_100")
                .userName("Test User")
                .userEmail("test@example.com")
                .build();
    }

    public static WorkshopDto createWorkshopDto() {
        return WorkshopDto.builder().
                code("WS_100")
                .name("Workshop 1")
                .description("Description of Workshop 1")
                .capacity(10)
                .build();
    }

    public static WorkshopDto newWorkshopDto() {
        return WorkshopDto.builder().
                code("WS_1000")
                .name("Workshop 1000")
                .description("Description of Workshop 1")
                .capacity(10)
                .startTime(ZonedDateTime.now().plusDays(10))
                .endTime(ZonedDateTime.now().plusDays(11))
                .build();
    }

    public static RegistrationsResponseDto createRegistrationResponseDto() {
        return RegistrationsResponseDto.builder().
                id(1)
                .workshopCode("WS_100")
                .userPhone("1234567890")
                .userPreferredContact("email")
                .build();
    }

    public static WorkshopDto createWorkshopDto(Workshop workshop) {
        return WorkshopDto.builder().
                code(workshop.getCode())
                .name(workshop.getName())
                .description(workshop.getDescription())
                .capacity(workshop.getCapacity())
                .startTime(workshop.getStartTime())
                .endTime(workshop.getEndTime())
                .build();
    }

    public static List<RegistrationsDto> getRegistrationsList() {
        return List.of(
                RegistrationsDto.builder()
                        .id(1)
                        .workshopCode("WS_100")
                        .userName("Test User")
                        .userEmail("test@example.com")
                        .build(),
                RegistrationsDto.builder()
                        .id(2)
                        .workshopCode("WS_200")
                        .userName("Test User 2")
                        .userEmail("test2@example.com")
                        .build(),
                RegistrationsDto.builder()
                        .id(3)
                        .workshopCode("WS_300")
                        .userName("Test User 3")
                        .userEmail("test3@example.com")
                        .build());
    }

}
