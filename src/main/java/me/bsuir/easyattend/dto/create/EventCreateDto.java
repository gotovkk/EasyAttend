package me.bsuir.easyattend.dto.create;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class EventCreateDto {

    @NotBlank(message = "Title cannot be blank")
    private String title;

    private String description;

    @NotNull(message = "Event date cannot be null")
    @Future(message = "Event date must be in the future")
    private LocalDateTime eventDate;

    private String location;

    @NotNull(message = "Organizer ID cannot be null")
    private Long organizerId;
}