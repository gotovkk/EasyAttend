package me.bsuir.easyattend.dto.get;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class EventGetDto {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;

    private String title;
    private String description;
    private LocalDateTime eventDate;
    private String location;
    private UserGetDto organizer;
}