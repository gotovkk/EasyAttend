package me.bsuir.easyattend.service;

import me.bsuir.easyattend.dto.create.EventCreateDto;
import me.bsuir.easyattend.dto.get.EventAttendeeDto;
import me.bsuir.easyattend.dto.get.EventGetDto;
import me.bsuir.easyattend.exception.ResourceNotFoundException;
import me.bsuir.easyattend.mapper.EventMapper;
import me.bsuir.easyattend.model.Event;
import me.bsuir.easyattend.model.User;
import me.bsuir.easyattend.repository.EventRepository;
import me.bsuir.easyattend.repository.RegistrationStatusRepository;
import me.bsuir.easyattend.repository.UserRepository;
import me.bsuir.easyattend.utils.InMemoryCache;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventServiceTest {

    @Mock
    private EventRepository eventRepository;

    @Mock
    private EventMapper eventMapper;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RegistrationStatusRepository registrationStatusRepository;

    @Mock
    private InMemoryCache<String, Object> inMemoryCache;

    @Mock
    private RegistrationStatusService registrationStatusService;

    @InjectMocks
    private EventService eventService;

    private Event testEvent;
    private User testUser;
    private EventCreateDto testCreateDto;
    private EventGetDto testGetDto;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("organizer");

        testEvent = new Event();
        testEvent.setId(1L);
        testEvent.setTitle("Test Event");
        testEvent.setOrganizer(testUser);

        testCreateDto = new EventCreateDto();
        testCreateDto.setTitle("Test Event");
        testCreateDto.setOrganizerId(1L);

        testGetDto = new EventGetDto();
        testGetDto.setId(1L);
        testGetDto.setTitle("Test Event");
    }

    @Test
    void getEventById_ShouldReturnEvent_WhenExists() {
        when(eventRepository.findById(1L)).thenReturn(Optional.of(testEvent));
        when(eventMapper.toDto(testEvent)).thenReturn(testGetDto);

        EventGetDto result = eventService.getEventById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Test Event", result.getTitle());
        verify(eventRepository, times(1)).findById(1L);
    }

    @Test
    void getEventById_ShouldThrowException_WhenNotFound() {
        when(eventRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> eventService.getEventById(1L));
    }

    @Test
    void getAttendeesByEventId_ShouldReturnAttendeesList() {
        User attendee = new User();
        attendee.setId(2L);
        attendee.setUsername("attendee");
        attendee.setFirstName("John");
        attendee.setLastName("Doe");

        when(registrationStatusRepository.findUsersByEventId(1L))
                .thenReturn(Collections.singletonList(attendee));

        List<EventAttendeeDto> result = eventService.getAttendeesByEventId(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("John", result.get(0).getFirstName());
        assertEquals("Doe", result.get(0).getLastName());
    }

    @Test
    void getAllEvents_ShouldReturnEventsList() {
        when(eventRepository.findAll()).thenReturn(Collections.singletonList(testEvent));
        when(eventMapper.toDto(testEvent)).thenReturn(testGetDto);

        List<EventGetDto> result = eventService.getAllEvents();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Test Event", result.get(0).getTitle());
    }

    @Test
    void createEvent_ShouldReturnCreatedEvent() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(eventMapper.toEntity(testCreateDto)).thenReturn(testEvent);
        when(eventRepository.save(testEvent)).thenReturn(testEvent);
        when(eventMapper.toDto(testEvent)).thenReturn(testGetDto);

        EventGetDto result = eventService.createEvent(testCreateDto);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(eventRepository, times(1)).save(testEvent);
    }

    @Test
    void createEvent_ShouldThrowException_WhenOrganizerNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> eventService.createEvent(testCreateDto));
    }

    @Test
    void updateEvent_ShouldReturnUpdatedEvent() {
        when(eventRepository.findById(1L)).thenReturn(Optional.of(testEvent));
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(eventRepository.save(testEvent)).thenReturn(testEvent);
        when(eventMapper.toDto(testEvent)).thenReturn(testGetDto);

        EventGetDto result = eventService.updateEvent(1L, testCreateDto);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(eventRepository, times(1)).save(testEvent);
    }

    @Test
    void updateEvent_ShouldThrowException_WhenEventNotFound() {
        when(eventRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> eventService.updateEvent(1L, testCreateDto));
    }

    @Test
    void updateEvent_ShouldThrowException_WhenOrganizerNotFound() {
        when(eventRepository.findById(1L)).thenReturn(Optional.of(testEvent));
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> eventService.updateEvent(1L, testCreateDto));
    }

    @Test
    void deleteEvent_ShouldDeleteEvent() {
        when(eventRepository.findById(1L)).thenReturn(Optional.of(testEvent));

        eventService.deleteEvent(1L);

        verify(eventRepository, times(1)).delete(testEvent);
        verify(inMemoryCache, times(2)).evictByPattern(anyString());
    }

    @Test
    void deleteEvent_ShouldThrowException_WhenEventNotFound() {
        when(eventRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> eventService.deleteEvent(1L));
    }

    @Test
    void removeAttendeeFromEvent_ShouldDeleteRegistration() {
        doNothing().when(registrationStatusRepository).deleteByEventIdAndUserId(1L, 2L);

        eventService.removeAttendeeFromEvent(1L, 2L);

        verify(registrationStatusRepository, times(1))
                .deleteByEventIdAndUserId(1L, 2L);
    }
}