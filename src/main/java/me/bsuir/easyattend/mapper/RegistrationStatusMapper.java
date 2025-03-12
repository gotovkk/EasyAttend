package me.bsuir.easyattend.mapper;

import me.bsuir.easyattend.repository.EventRepository;
import me.bsuir.easyattend.repository.UserRepository;
import me.bsuir.easyattend.dto.create.RegistrationStatusCreateDto;
import me.bsuir.easyattend.dto.get.RegistrationStatusGetDto;
import me.bsuir.easyattend.model.RegistrationStatus;
import me.bsuir.easyattend.model.Event;
import me.bsuir.easyattend.model.User;
import me.bsuir.easyattend.exception.ResourceNotFoundException;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring", uses = {UserMapper.class, EventMapper.class},
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public abstract class RegistrationStatusMapper {

    @Autowired
    protected UserRepository userRepository;

    @Autowired
    protected EventRepository eventRepository;

    @Mapping(target = "id", ignore = true)
    @Mapping(source = "eventId", target = "event")
    @Mapping(source = "userId", target = "user", qualifiedByName = "userFromId")
    public abstract RegistrationStatus toEntity(RegistrationStatusCreateDto dto);

    @Mapping(source = "event", target = "event")
    @Mapping(source = "user", target = "user", qualifiedByName = "userToUserGetDto")
    public abstract RegistrationStatusGetDto toDto(RegistrationStatus entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(source = "eventId", target = "event")
    @Mapping(source = "userId", target = "user")
    public abstract void updateRegistrationStatusFromDto(RegistrationStatusCreateDto dto, @MappingTarget RegistrationStatus entity);

    @Named("userFromId")
    protected User userFromId(Long userId) {
        return userId == null ? null : userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id " + userId));
    }

    protected Event eventFromId(Long eventId) {
        return eventId == null ? null : eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found with id " + eventId));
    }
}