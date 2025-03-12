package me.bsuir.easyattend.dto.get;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import me.bsuir.easyattend.model.RoleType;

@Getter
@Setter
public class RoleGetDto {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;

    private RoleType name;
}