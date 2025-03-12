package me.bsuir.easyattend.dto.get;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;


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