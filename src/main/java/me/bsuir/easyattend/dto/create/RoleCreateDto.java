package me.bsuir.easyattend.dto.create;


import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import me.bsuir.easyattend.model.RoleType;

@Getter
@Setter
public class RoleCreateDto {
    @NotBlank(message = "Role name cannot be blank")
    private RoleType name;
}