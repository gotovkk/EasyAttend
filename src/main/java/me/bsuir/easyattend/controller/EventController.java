package me.bsuir.easyattend.controller;

import jakarta.validation.Valid;
import java.util.List;
import me.bsuir.easyattend.annotation.Timed;
import me.bsuir.easyattend.dto.create.EventCreateDto;
import me.bsuir.easyattend.dto.get.EventAttendeeDto;
import me.bsuir.easyattend.dto.get.EventGetDto;
import me.bsuir.easyattend.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/events")
public class EventController {

    private final EventService eventService;

    @Autowired
    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @GetMapping("/{id}")
    @Timed
    public ResponseEntity<EventGetDto> getEventById(@PathVariable Long id) {
        EventGetDto event = eventService.getEventById(id);
        return ResponseEntity.ok(event);
    }

    @GetMapping
    public ResponseEntity<List<EventGetDto>> getAllEvents() {
        List<EventGetDto> events = eventService.getAllEvents();
        return ResponseEntity.ok(events);
    }

    @PostMapping
    public ResponseEntity<EventGetDto> createEvent(
            @Valid @RequestBody EventCreateDto eventCreateDto
    ) {
        EventGetDto createdEvent = eventService.createEvent(eventCreateDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdEvent);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EventGetDto> updateEvent(
            @PathVariable Long id,
            @Valid @RequestBody EventCreateDto eventCreateDto
    ) {
        EventGetDto updatedEvent = eventService.updateEvent(id, eventCreateDto);
        return ResponseEntity.ok(updatedEvent);
    }

    @DeleteMapping("/{eventId}/attendees/{userId}")
    public ResponseEntity<Void> removeAttendeeFromEvent(
            @PathVariable Long eventId,
            @PathVariable Long userId
    ) {
        eventService.removeAttendeeFromEvent(eventId, userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/attendees") // Changed mapping
    public ResponseEntity<List<EventAttendeeDto>> getAttendeesByEventId(@PathVariable Long id) {
        List<EventAttendeeDto> attendees = eventService.getAttendeesByEventId(id);
        return ResponseEntity.ok(attendees);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEvent(@PathVariable Long id) {
        eventService.deleteEvent(id);
        return ResponseEntity.noContent().build();
    }

}