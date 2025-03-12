package me.bsuir.easyattend.dto.create;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegistrationStatusCreateDto {

    @NotNull(message = "Event ID cannot be null")
    private Long eventId;

    @NotNull(message = "User ID cannot be null")
    private Long userId;

    @NotNull(message = "Status cannot be null")
    private String status; // Or use enum RegistrationStatusType
}