package me.bsuir.easyattend.dto.get;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class RegistrationStatusGetDto {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;

    private EventGetDto event;
    private UserGetDto user;
    private String status; // Or use enum RegistrationStatusType
    private LocalDateTime statusDate;
}