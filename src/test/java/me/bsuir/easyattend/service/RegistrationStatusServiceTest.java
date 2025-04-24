package me.bsuir.easyattend.service;

import me.bsuir.easyattend.dto.create.RegistrationStatusCreateDto;
import me.bsuir.easyattend.dto.get.ConfirmedUserDto;
import me.bsuir.easyattend.dto.get.EventGetDto;
import me.bsuir.easyattend.dto.get.RegistrationStatusGetDto;
import me.bsuir.easyattend.exception.ResourceNotFoundException;
import me.bsuir.easyattend.mapper.EventMapper;
import me.bsuir.easyattend.mapper.RegistrationStatusMapper;
import me.bsuir.easyattend.model.Event;
import me.bsuir.easyattend.model.RegistrationStatus;
import me.bsuir.easyattend.model.RegistrationStatusType;
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

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RegistrationStatusServiceTest {

    @Mock
    private RegistrationStatusRepository registrationStatusRepository;

    @Mock
    private RegistrationStatusMapper registrationStatusMapper;

    @Mock
    private EventRepository eventRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private EventMapper eventMapper;

    @Mock
    private InMemoryCache<String, Object> inMemoryCache;

    @InjectMocks
    private RegistrationStatusService registrationStatusService;

    private RegistrationStatus registrationStatus;
    private RegistrationStatusGetDto registrationStatusGetDto;
    private RegistrationStatusCreateDto registrationStatusCreateDto;
    private Event event;
    private User user;
    private EventGetDto eventGetDto;
    private ConfirmedUserDto confirmedUserDto;

    @BeforeEach
    void setUp() {
        event = new Event();
        event.setId(1L);

        user = new User();
        user.setId(1L);
        user.setLastName("Smith");

        registrationStatus = new RegistrationStatus();
        registrationStatus.setId(1L);
        registrationStatus.setEvent(event);
        registrationStatus.setUser(user);
        registrationStatus.setStatus(RegistrationStatusType.CONFIRMED);
        registrationStatus.setStatusDate(LocalDateTime.now());

        registrationStatusGetDto = new RegistrationStatusGetDto();
        registrationStatusGetDto.setId(1L);
        // Assume RegistrationStatusGetDto has fields eventId, userId, status
        // If it maps event and user as DTOs, adjust accordingly
        registrationStatusGetDto.setStatus(RegistrationStatusType.CONFIRMED.name());

        registrationStatusCreateDto = new RegistrationStatusCreateDto();
        registrationStatusCreateDto.setEventId(1L);
        registrationStatusCreateDto.setUserId(1L);
        registrationStatusCreateDto.setStatus(RegistrationStatusType.CONFIRMED.name());

        eventGetDto = new EventGetDto();
        eventGetDto.setId(1L);

        confirmedUserDto = new ConfirmedUserDto();
        confirmedUserDto.setUserId(1L);
        confirmedUserDto.setLastName("Smith");
    }

    @Test
    void getRegistrationStatusesByEventIdAndUserLastName_CacheHit() {
        String cacheKey = "registrationStatus:filtered:eventId=1,lastName=Smith";
        List<RegistrationStatusGetDto> cachedDtos = Arrays.asList(registrationStatusGetDto);
        when(inMemoryCache.get(cacheKey)).thenReturn(cachedDtos);

        List<RegistrationStatusGetDto> result = registrationStatusService
                .getRegistrationStatusesByEventIdAndUserLastName(1L, "Smith");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(registrationStatusGetDto, result.get(0));
        verify(inMemoryCache).get(cacheKey);
        verifyNoInteractions(registrationStatusRepository, registrationStatusMapper);
    }

    @Test
    void getRegistrationStatusesByEventIdAndUserLastName_CacheMiss() {
        String cacheKey = "registrationStatus:filtered:eventId=1,lastName=Smith";
        List<RegistrationStatus> statuses = Arrays.asList(registrationStatus);
        List<RegistrationStatusGetDto> dtos = Arrays.asList(registrationStatusGetDto);

        when(inMemoryCache.get(cacheKey)).thenReturn(null);
        when(registrationStatusRepository.findByEventIdAndUserLastName(1L, "Smith")).thenReturn(statuses);
        when(registrationStatusMapper.toDto(registrationStatus)).thenReturn(registrationStatusGetDto);

        List<RegistrationStatusGetDto> result = registrationStatusService
                .getRegistrationStatusesByEventIdAndUserLastName(1L, "Smith");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(registrationStatusGetDto, result.get(0));
        verify(inMemoryCache).get(cacheKey);
        verify(registrationStatusRepository).findByEventIdAndUserLastName(1L, "Smith");
        verify(registrationStatusMapper).toDto(registrationStatus);
        verify(inMemoryCache).put(cacheKey, dtos);
    }

    @Test
    void getRegistrationStatusById_Success() {
        when(registrationStatusRepository.findById(1L)).thenReturn(Optional.of(registrationStatus));
        when(registrationStatusMapper.toDto(registrationStatus)).thenReturn(registrationStatusGetDto);

        RegistrationStatusGetDto result = registrationStatusService.getRegistrationStatusById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(RegistrationStatusType.CONFIRMED.name(), result.getStatus());
        verify(registrationStatusRepository).findById(1L);
        verify(registrationStatusMapper).toDto(registrationStatus);
    }

    @Test
    void getRegistrationStatusById_NotFound() {
        when(registrationStatusRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> registrationStatusService.getRegistrationStatusById(1L));
        verify(registrationStatusRepository).findById(1L);
        verifyNoInteractions(registrationStatusMapper);
    }

    @Test
    void getRegistrationStatusesByUserId_Success() {
        List<RegistrationStatus> statuses = Arrays.asList(registrationStatus);
        List<RegistrationStatusGetDto> dtos = Arrays.asList(registrationStatusGetDto);

        when(registrationStatusRepository.findByUserId(1L)).thenReturn(statuses);
        when(registrationStatusMapper.toDto(registrationStatus)).thenReturn(registrationStatusGetDto);

        List<RegistrationStatusGetDto> result = registrationStatusService.getRegistrationStatusesByUserId(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(registrationStatusGetDto, result.get(0));
        verify(registrationStatusRepository).findByUserId(1L);
        verify(registrationStatusMapper).toDto(registrationStatus);
    }

    @Test
    void getAllRegistrationStatuses_Success() {
        List<RegistrationStatus> statuses = Arrays.asList(registrationStatus);
        List<RegistrationStatusGetDto> dtos = Arrays.asList(registrationStatusGetDto);

        when(registrationStatusRepository.findAll()).thenReturn(statuses);
        when(registrationStatusMapper.toDto(registrationStatus)).thenReturn(registrationStatusGetDto);

        List<RegistrationStatusGetDto> result = registrationStatusService.getAllRegistrationStatuses();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(registrationStatusGetDto, result.get(0));
        verify(registrationStatusRepository).findAll();
        verify(registrationStatusMapper).toDto(registrationStatus);
    }

    @Test
    void createRegistrationStatus_Success() {
        when(registrationStatusMapper.toEntity(registrationStatusCreateDto)).thenReturn(registrationStatus);
        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(registrationStatusRepository.save(any(RegistrationStatus.class))).thenReturn(registrationStatus);
        when(registrationStatusMapper.toDto(registrationStatus)).thenReturn(registrationStatusGetDto);

        RegistrationStatusGetDto result = registrationStatusService.createRegistrationStatus(registrationStatusCreateDto);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(RegistrationStatusType.CONFIRMED.name(), result.getStatus());
        verify(registrationStatusMapper).toEntity(registrationStatusCreateDto);
        verify(eventRepository).findById(1L);
        verify(userRepository).findById(1L);
        verify(registrationStatusRepository).save(registrationStatus);
        verify(registrationStatusMapper).toDto(registrationStatus);
    }

    @Test
    void updateRegistrationStatus_Success() {
        when(registrationStatusRepository.findById(1L)).thenReturn(Optional.of(registrationStatus));
        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        doNothing().when(registrationStatusMapper).updateRegistrationStatusFromDto(registrationStatusCreateDto, registrationStatus);
        when(registrationStatusRepository.save(registrationStatus)).thenReturn(registrationStatus);
        when(registrationStatusMapper.toDto(registrationStatus)).thenReturn(registrationStatusGetDto);

        RegistrationStatusGetDto result = registrationStatusService.updateRegistrationStatus(1L, registrationStatusCreateDto);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(RegistrationStatusType.CONFIRMED.name(), result.getStatus());
        verify(registrationStatusRepository).findById(1L);
        verify(eventRepository).findById(1L);
        verify(userRepository).findById(1L);
        verify(registrationStatusMapper).updateRegistrationStatusFromDto(registrationStatusCreateDto, registrationStatus);
        verify(registrationStatusRepository).save(registrationStatus);
        verify(registrationStatusMapper).toDto(registrationStatus);
    }

    @Test
    void updateRegistrationStatus_NotFound() {
        when(registrationStatusRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> registrationStatusService.updateRegistrationStatus(1L, registrationStatusCreateDto));
        verify(registrationStatusRepository).findById(1L);
        verifyNoMoreInteractions(eventRepository, userRepository, registrationStatusMapper, registrationStatusRepository);
    }

    @Test
    void deleteRegistrationStatus_Success() {
        when(registrationStatusRepository.findById(1L)).thenReturn(Optional.of(registrationStatus));

        registrationStatusService.deleteRegistrationStatus(1L);

        verify(registrationStatusRepository).findById(1L);
        verify(registrationStatusRepository).delete(registrationStatus);
    }

    @Test
    void deleteRegistrationStatus_NotFound() {
        when(registrationStatusRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> registrationStatusService.deleteRegistrationStatus(1L));
        verify(registrationStatusRepository).findById(1L);
        verifyNoMoreInteractions(registrationStatusRepository);
    }

    @Test
    void getEventsByUserId_Success() {
        List<RegistrationStatus> statuses = Arrays.asList(registrationStatus);
        List<EventGetDto> dtos = Arrays.asList(eventGetDto);

        when(registrationStatusRepository.findByUserId(1L)).thenReturn(statuses);
        when(eventMapper.toDto(event)).thenReturn(eventGetDto);

        List<EventGetDto> result = registrationStatusService.getEventsByUserId(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(eventGetDto, result.get(0));
        verify(registrationStatusRepository).findByUserId(1L);
        verify(eventMapper).toDto(event);
    }

    @Test
    void getConfirmedUsersByEventIdAndLastName_CacheHit() {
        String cacheKey = "registrationStatus:confirmedUsers:eventId=1,lastName=Smith";
        List<ConfirmedUserDto> cachedDtos = Arrays.asList(confirmedUserDto);
        when(inMemoryCache.get(cacheKey)).thenReturn(cachedDtos);

        List<ConfirmedUserDto> result = registrationStatusService.getConfirmedUsersByEventIdAndLastName(1L, "Smith");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(confirmedUserDto, result.get(0));
        verify(inMemoryCache).get(cacheKey);
        verifyNoInteractions(registrationStatusRepository);
    }

    @Test
    void getConfirmedUsersByEventIdAndLastName_CacheMiss() {
        String cacheKey = "registrationStatus:confirmedUsers:eventId=1,lastName=Smith";
        List<ConfirmedUserDto> dtos = Arrays.asList(confirmedUserDto);

        when(inMemoryCache.get(cacheKey)).thenReturn(null);
        when(registrationStatusRepository.findConfirmedUsersByEventIdAndLastName(1L, "Smith")).thenReturn(dtos);

        List<ConfirmedUserDto> result = registrationStatusService.getConfirmedUsersByEventIdAndLastName(1L, "Smith");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(confirmedUserDto, result.get(0));
        verify(inMemoryCache).get(cacheKey);
        verify(registrationStatusRepository).findConfirmedUsersByEventIdAndLastName(1L, "Smith");
        verify(inMemoryCache).put(cacheKey, dtos);
    }
}