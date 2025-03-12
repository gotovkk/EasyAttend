package me.bsuir.easyattend.service;

import java.util.List;
import me.bsuir.easyattend.dto.create.EventCreateDto;
import me.bsuir.easyattend.dto.get.EventGetDto;
import me.bsuir.easyattend.exception.ResourceNotFoundException;
import me.bsuir.easyattend.mapper.EventMapper;
import me.bsuir.easyattend.model.Event;
import me.bsuir.easyattend.model.User;
import me.bsuir.easyattend.repository.EventRepository;
import me.bsuir.easyattend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EventService {

    private final EventRepository eventRepository;
    private final EventMapper eventMapper;
    private final UserRepository userRepository;

    @Autowired
    public EventService(
            EventRepository eventRepository,
            EventMapper eventMapper,
            UserRepository userRepository
    ) {
        this.eventRepository = eventRepository;
        this.eventMapper = eventMapper;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public EventGetDto getEventById(Long id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found with id " + id));
        return eventMapper.toDto(event);
    }

    @Transactional(readOnly = true)
    public List<EventGetDto> getAllEvents() {
        List<Event> events = eventRepository.findAll();
        return events.stream()
                .map(eventMapper::toDto)
                .toList();
    }

    @Transactional
    public EventGetDto createEvent(EventCreateDto eventCreateDto) {
        Event event = eventMapper.toEntity(eventCreateDto);

        User organizer = userRepository.findById(eventCreateDto.getOrganizerId())
                .orElseThrow(()
                        -> new ResourceNotFoundException(
                                "User not found with id "
                                        + eventCreateDto.getOrganizerId()));
        event.setOrganizer(organizer);

        Event savedEvent = eventRepository.save(event);
        return eventMapper.toDto(savedEvent);
    }

    @Transactional
    public EventGetDto updateEvent(Long id, EventCreateDto eventCreateDto) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found with id " + id));

        User organizer = userRepository.findById(eventCreateDto.getOrganizerId())
                .orElseThrow(()
                        -> new ResourceNotFoundException(
                                "User not found with id "
                                        + eventCreateDto.getOrganizerId()));
        event.setOrganizer(organizer);

        eventMapper.updateEventFromDto(eventCreateDto, event);
        Event updatedEvent = eventRepository.save(event);
        return eventMapper.toDto(updatedEvent);
    }

    @Transactional
    public void deleteEvent(Long id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found with id " + id));
        eventRepository.delete(event);
    }
}