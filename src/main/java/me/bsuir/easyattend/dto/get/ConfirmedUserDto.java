package me.bsuir.easyattend.dto.get;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ConfirmedUserDto {
    private Long userId;
    private String firstName;
    private String lastName;
}