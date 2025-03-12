package me.bsuir.easyattend.mapper;

import me.bsuir.easyattend.repository.UserRepository;
import me.bsuir.easyattend.dto.create.UserCreateDto;
import me.bsuir.easyattend.dto.get.UserGetDto;
import me.bsuir.easyattend.model.User;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;


@Mapper(componentModel = "spring", uses = {RoleMapper.class},
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public abstract class UserMapper {

    @Autowired
    protected UserRepository userRepository;

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "roles", ignore = true)
    public abstract User toEntity(UserCreateDto userCreateDto);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "username", target = "username")
    @Mapping(source = "email", target = "email")
    @Mapping(source = "firstName", target = "firstName")
    @Mapping(source = "lastName", target = "lastName")
    @Mapping(source = "registrationDate", target = "registrationDate")
    @Mapping(source = "roles", target = "roles")
    public abstract UserGetDto toDto(User user);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "roles", ignore = true)
    public abstract void updateUserFromDto(UserCreateDto dto, @MappingTarget User entity);

    @Named("userToUserGetDto")
    public UserGetDto userToUserGetDto(User user) {
        if (user == null) {
            return null;
        }
        return toDto(user);
    }
}