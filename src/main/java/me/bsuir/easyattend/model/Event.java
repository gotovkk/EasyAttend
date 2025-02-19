package me.bsuir.easyattend.model;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Event {
    private Long id;
    private String name;
    private String location;
    private String description;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    public Event(
            Long id,
            String name,
            String location,
            String description,
            LocalDateTime startTime,
            LocalDateTime endTime) {
        this.id = id;
        this.name = name;
        this.location = location;
        this.description = description;
        this.startTime = startTime;
        this.endTime = endTime;
    }

}
