package me.bsuir.easyattend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import me.bsuir.easyattend.dto.create.UserCreateDto;
import me.bsuir.easyattend.dto.get.UserGetDto;
import me.bsuir.easyattend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@Tag(name = "Управление пользователями", description = "API для работы с пользователями системы")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Получить пользователя по ID",
            description = "Возвращает полную информацию о пользователе по его идентификатору")
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200", description = "Пользователь найден",
                            content = @Content(
                                    schema = @Schema(implementation = UserGetDto.class))),
                    @ApiResponse(responseCode = "404", description = "Пользователь не найден")
            })
    public ResponseEntity<UserGetDto> getUserById(
            @Parameter(description = "ID пользователя", required = true)
            @PathVariable Long id
    ) {
        UserGetDto user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    @GetMapping
    @Operation(
            summary = "Получить всех пользователей",
            description = "Возвращает список всех зарегистрированных пользователей системы")
    @ApiResponse(
            responseCode = "200", description = "Список пользователей",
            content = @Content(schema = @Schema(implementation = UserGetDto.class)))
    public ResponseEntity<List<UserGetDto>> getAllUsers() {
        List<UserGetDto> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @PostMapping
    @Operation(
            summary = "Создать нового пользователя",
            description = "Регистрирует нового пользователя в системе")
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "201", description = "Пользователь успешно создан",
                            content = @Content(
                                    schema = @Schema(implementation = UserGetDto.class))),
                    @ApiResponse(
                            responseCode = "400", description = "Некорректные данные пользователя")
            })
    public ResponseEntity<UserGetDto> createUser(
            @Parameter(description = "Данные для создания пользователя", required = true)
            @Valid @RequestBody UserCreateDto userCreateDto
    ) {
        UserGetDto createdUser = userService.createUser(userCreateDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }

    @PostMapping("/bulk")
    @Operation(
            summary = "Создать несколько пользователей",
            description = "Регистрирует несколько новых пользователей в системе в одной операции")
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "201", description = "Пользователи успешно созданы",
                            content = @Content(
                                    schema = @Schema(implementation = UserGetDto.class))),
                    @ApiResponse(
                            responseCode = "400", description = "Некорректные данные пользователей"),
                    @ApiResponse(
                            responseCode = "404", description = "Одна или несколько ролей не найдены")
            })
    public ResponseEntity<List<UserGetDto>> createUsersBulk(
            @Parameter(description = "Список данных для создания пользователей", required = true)
            @Valid @RequestBody List<UserCreateDto> userCreateDtos
    ) {
        List<UserGetDto> createdUsers = userService.createUsersBulk(userCreateDtos);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUsers);
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Обновить данные пользователя",
            description = "Обновляет информацию о пользователе по указанному ID")
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Данные пользователя успешно обновлены",
                            content = @Content(
                                    schema = @Schema(implementation = UserGetDto.class))),
                    @ApiResponse(responseCode = "400", description = "Некорректные данные"),
                    @ApiResponse(responseCode = "404", description = "Пользователь не найден")
            })
    public ResponseEntity<UserGetDto> updateUser(
            @Parameter(description = "ID пользователя для обновления", required = true)
            @PathVariable Long id,
            @Parameter(description = "Обновленные данные пользователя", required = true)
            @Valid @RequestBody UserCreateDto userCreateDto
    ) {
        UserGetDto updatedUser = userService.updateUser(id, userCreateDto);
        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Удалить пользователя",
            description = "Удаляет пользователя из системы по указанному ID")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "204", description = "Пользователь успешно удален"),
                    @ApiResponse(responseCode = "404", description = "Пользователь не найден")
            })
    public ResponseEntity<Void> deleteUser(
            @Parameter(description = "ID пользователя для удаления", required = true)
            @PathVariable Long id
    ) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}