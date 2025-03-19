package me.bsuir.easyattend.dto.get;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EventAttendeeDto {
    private Long id; // User ID
    private String username;
    private String firstName;
    private String lastName;
    //Можно добавить другие поля пользователя, которые тебе нужны
}