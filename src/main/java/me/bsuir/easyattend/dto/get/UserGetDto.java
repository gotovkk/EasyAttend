package me.bsuir.easyattend.dto.get;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
public class UserGetDto {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;

    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private LocalDateTime registrationDate;
    private Set<RoleGetDto> roles;
}