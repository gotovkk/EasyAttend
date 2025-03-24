package me.bsuir.easyattend.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import me.bsuir.easyattend.dto.create.RegistrationStatusCreateDto;
import me.bsuir.easyattend.dto.get.ConfirmedUserDto;
import me.bsuir.easyattend.dto.get.EventGetDto;
import me.bsuir.easyattend.dto.get.RegistrationStatusGetDto;
import me.bsuir.easyattend.exception.ResourceNotFoundException;
import me.bsuir.easyattend.mapper.EventMapper;
import me.bsuir.easyattend.mapper.RegistrationStatusMapper;
import me.bsuir.easyattend.model.Event;
import me.bsuir.easyattend.model.RegistrationStatus;
import me.bsuir.easyattend.model.User;
import me.bsuir.easyattend.repository.EventRepository;
import me.bsuir.easyattend.repository.RegistrationStatusRepository;
import me.bsuir.easyattend.repository.UserRepository;
import me.bsuir.easyattend.utils.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RegistrationStatusService {

    private final RegistrationStatusRepository registrationStatusRepository;
    private final RegistrationStatusMapper registrationStatusMapper;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final EventMapper eventMapper; // Add EventMapper
    private final InMemoryCache<String, List<RegistrationStatusGetDto>> registrationStatusCache;
    private final InMemoryCache<String, List<ConfirmedUserDto>> confirmedUsersCache;

    @Autowired
    public RegistrationStatusService(
            RegistrationStatusRepository registrationStatusRepository,
            RegistrationStatusMapper registrationStatusMapper,
            EventRepository eventRepository,
            UserRepository userRepository,
            EventMapper eventMapper,
            InMemoryCache<String, List<RegistrationStatusGetDto>> registrationStatusCache,
            InMemoryCache<String, List<ConfirmedUserDto>> confirmedUsersCache
    ) {
        this.registrationStatusRepository = registrationStatusRepository;
        this.registrationStatusMapper = registrationStatusMapper;
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
        this.eventMapper = eventMapper;
        this.registrationStatusCache = registrationStatusCache;
        this.confirmedUsersCache = confirmedUsersCache;
    }

    public List<RegistrationStatusGetDto> getRegistrationStatusesByEventIdAndUserLastName(Long eventId, String lastName) {
        String cacheKey = "eventId:" + eventId + ",lastName:" + lastName;
        List<RegistrationStatusGetDto> cachedResult = registrationStatusCache.get(cacheKey);
        if (cachedResult != null) {
            return cachedResult;
        }

        List<RegistrationStatus> registrationStatuses = registrationStatusRepository.findByEventIdAndUserLastName(eventId, lastName);
        List<RegistrationStatusGetDto> dtos = registrationStatuses.stream()
                .map(registrationStatusMapper::toDto)
                .collect(Collectors.toList());

        registrationStatusCache.put(cacheKey, dtos);
        return dtos;
    }

    @Transactional(readOnly = true)
    public RegistrationStatusGetDto getRegistrationStatusById(Long id) {
        RegistrationStatus registrationStatus
                = registrationStatusRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "RegistrationStatus not found with id " + id));
        return registrationStatusMapper.toDto(registrationStatus);
    }

    public List<RegistrationStatusGetDto> getRegistrationStatusesByUserId(Long userId) {
        List<RegistrationStatus> registrationStatuses
                = registrationStatusRepository.findByUserId(userId);
        return registrationStatuses.stream()
                .map(registrationStatusMapper::toDto) // Используем маппер
                .collect(Collectors.toList());
    }


    @Transactional(readOnly = true)
    public List<RegistrationStatusGetDto> getAllRegistrationStatuses() {
        List<RegistrationStatus> registrationStatuses = registrationStatusRepository.findAll();
        return registrationStatuses.stream()
                .map(registrationStatusMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public RegistrationStatusGetDto createRegistrationStatus(
            RegistrationStatusCreateDto registrationStatusCreateDto
    ) {
        RegistrationStatus registrationStatus
                = registrationStatusMapper.toEntity(registrationStatusCreateDto);

        Event event = eventRepository.findById(registrationStatusCreateDto.getEventId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Event not found with id "
                                        + registrationStatusCreateDto.getEventId()));
        User user = userRepository.findById(registrationStatusCreateDto.getUserId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found with id "
                                        + registrationStatusCreateDto.getUserId()));

        registrationStatus.setEvent(event);
        registrationStatus.setUser(user);
        registrationStatus.setStatusDate(LocalDateTime.now());

        RegistrationStatus savedRegistrationStatus
                = registrationStatusRepository
                .save(registrationStatus);
        return registrationStatusMapper.toDto(savedRegistrationStatus);
    }

    @Transactional
    public RegistrationStatusGetDto updateRegistrationStatus(
            Long id,
            RegistrationStatusCreateDto registrationStatusCreateDto
    ) {
        RegistrationStatus registrationStatus = registrationStatusRepository.findById(id)
                .orElseThrow(()
                        -> new ResourceNotFoundException(
                        "RegistrationStatus not found with id "
                                + id)
                );

        Event event = eventRepository.findById(registrationStatusCreateDto.getEventId())
                .orElseThrow(()
                        -> new ResourceNotFoundException(
                        "Event not found with id "
                                + registrationStatusCreateDto.getEventId())
                );
        User user = userRepository.findById(registrationStatusCreateDto.getUserId())
                .orElseThrow(()
                        -> new ResourceNotFoundException(
                        "User not found with id "
                                + registrationStatusCreateDto.getUserId())
                );

        registrationStatus.setEvent(event);
        registrationStatus.setUser(user);
        registrationStatus.setStatusDate(LocalDateTime.now());

        registrationStatusMapper.updateRegistrationStatusFromDto(
                registrationStatusCreateDto,
                registrationStatus
        );

        RegistrationStatus updatedRegistrationStatus
                = registrationStatusRepository
                .save(registrationStatus);
        return registrationStatusMapper.toDto(updatedRegistrationStatus);
    }

    @Transactional
    public void deleteRegistrationStatus(Long id) {
        RegistrationStatus registrationStatus = registrationStatusRepository.findById(id)
                .orElseThrow(()
                        -> new ResourceNotFoundException(
                        "RegistrationStatus not found with id "
                                + id)
                );
        registrationStatusRepository.delete(registrationStatus);
    }

    public List<EventGetDto> getEventsByUserId(Long userId) {
        List<RegistrationStatus> registrationStatuses
                = registrationStatusRepository.findByUserId(userId);
        return registrationStatuses.stream()
                .map(rs -> eventMapper.toDto(rs.getEvent()))
                .collect(Collectors.toList());
    }

    public List<ConfirmedUserDto> getConfirmedUsersByEventIdAndLastName(Long eventId, String lastName) {
        String cacheKey = "eventId:" + eventId + ",lastName:" + lastName;
        List<ConfirmedUserDto> cachedResult = confirmedUsersCache.get(cacheKey);
        if (cachedResult != null) {
            return cachedResult;
        }

        List<ConfirmedUserDto> confirmedUsers = registrationStatusRepository.findConfirmedUsersByEventIdAndLastName(eventId, lastName);
        confirmedUsersCache.put(cacheKey, confirmedUsers);
        return confirmedUsers;
    }
}