package me.bsuir.easyattend.mapper;

import me.bsuir.easyattend.dto.create.EventCreateDto;
import me.bsuir.easyattend.dto.get.EventGetDto;
import me.bsuir.easyattend.exception.ResourceNotFoundException;
import me.bsuir.easyattend.model.Event;
import me.bsuir.easyattend.model.User;
import me.bsuir.easyattend.repository.UserRepository;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring", uses = {UserMapper.class},
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public abstract class EventMapper {

    @Autowired
    protected UserRepository userRepository;

    @Mapping(target = "id", ignore = true)
    @Mapping(source = "organizerId", target = "organizer")
    public abstract Event toEntity(EventCreateDto dto);

    @Mapping(source = "organizer", target = "organizer", qualifiedByName = "userToUserGetDto")
    public abstract EventGetDto toDto(Event entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(source = "organizerId", target = "organizer")
    public abstract void updateEventFromDto(EventCreateDto dto, @MappingTarget Event entity);

    protected User userFromId(Long organizerId) {
        return organizerId == null ? null : userRepository.findById(organizerId)
                .orElseThrow(()
                        -> new ResourceNotFoundException(
                                "User not found with id "
                                        + organizerId));
    }
}