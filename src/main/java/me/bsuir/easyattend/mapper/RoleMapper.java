package me.bsuir.easyattend.mapper;

import me.bsuir.easyattend.dto.create.RoleCreateDto;
import me.bsuir.easyattend.dto.get.RoleGetDto;
import me.bsuir.easyattend.model.Role;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import me.bsuir.easyattend.model.RoleType;


@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface RoleMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "users", ignore = true)
    Role toEntity(RoleCreateDto dto);

    RoleGetDto toDto(Role entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "users", ignore = true)
    void updateRoleFromDto(RoleCreateDto dto, @MappingTarget Role entity);

    default RoleType stringToRoleType(String roleName) {
        return RoleType.valueOf(roleName);
    }

    default String roleTypeToString(RoleType roleType) {
        return roleType.name();
    }
}