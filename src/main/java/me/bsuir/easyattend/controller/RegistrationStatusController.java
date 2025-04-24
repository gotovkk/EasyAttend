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
import me.bsuir.easyattend.dto.create.RegistrationStatusCreateDto;
import me.bsuir.easyattend.dto.get.ConfirmedUserDto;
import me.bsuir.easyattend.dto.get.EventGetDto;
import me.bsuir.easyattend.dto.get.RegistrationStatusGetDto;
import me.bsuir.easyattend.service.RegistrationStatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/registration-statuses")
@Tag(
        name = "Управление статусами регистрации",
        description = "API для работы со статусами регистрации пользователей на мероприятия")
public class RegistrationStatusController {

    private final RegistrationStatusService registrationStatusService;

    @Autowired
    public RegistrationStatusController(RegistrationStatusService registrationStatusService) {
        this.registrationStatusService = registrationStatusService;
    }

    @SuppressWarnings("checkstyle:Indentation")
    @GetMapping("/by-user/{userId}")
    @Operation(
            summary = "Получить статусы регистрации по ID пользователя",
            description = "Возвращает все статусы регистрации для указанного пользователя")
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200", description = "Список статусов регистрации",
                            content = @Content(
                                    schema = @Schema(
                                            implementation = RegistrationStatusGetDto.class))),
                    @ApiResponse(responseCode = "404", description = "Пользователь не найден")
            })
    public ResponseEntity<List<RegistrationStatusGetDto>> getRegistrationStatusesByUserId(
            @Parameter(description = "ID пользователя", required = true)
            @PathVariable Long userId
    ) {
        List<RegistrationStatusGetDto> registrationStatuses
                = registrationStatusService.getRegistrationStatusesByUserId(userId);
        return ResponseEntity.ok(registrationStatuses);
    }

    @SuppressWarnings("checkstyle:Indentation")
    @GetMapping("/events-by-user/{userId}")
    @Operation(
            summary = "Получить мероприятия пользователя",
            description = "Возвращает список мероприятий, на которые зарегистрирован пользователь")
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200", description = "Список мероприятий",
                            content = @Content(
                                    schema = @Schema(implementation = EventGetDto.class))),
                    @ApiResponse(responseCode = "404", description = "Пользователь не найден")
            })
    public ResponseEntity<List<EventGetDto>> getEventsByUserId(
            @Parameter(description = "ID пользователя", required = true)
            @PathVariable Long userId
    ) {
        List<EventGetDto> events = registrationStatusService.getEventsByUserId(userId);
        return ResponseEntity.ok(events);
    }

    @SuppressWarnings("checkstyle:Indentation")
    @GetMapping("/{id}")
    @Operation(
            summary = "Получить статус регистрации по ID",
            description = "Возвращает статус регистрации по его идентификатору")
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200", description = "Статус регистрации найден",
                            content = @Content(
                                    schema = @Schema(
                                            implementation = RegistrationStatusGetDto.class))),
                    @ApiResponse(responseCode = "404", description = "Статус регистрации не найден")
            })
    public ResponseEntity<RegistrationStatusGetDto> getRegistrationStatusById(
            @Parameter(description = "ID статуса регистрации", required = true)
            @PathVariable Long id
    ) {
        RegistrationStatusGetDto registrationStatus =
                registrationStatusService.getRegistrationStatusById(id);
        return ResponseEntity.ok(registrationStatus);
    }

    @GetMapping
    @Operation(
            summary = "Получить все статусы регистрации",
            description = "Возвращает список всех статусов регистрации в системе")
    @ApiResponse(
            responseCode = "200", description = "Список всех статусов регистрации",
            content = @Content(schema = @Schema(implementation = RegistrationStatusGetDto.class)))
    public ResponseEntity<List<RegistrationStatusGetDto>> getAllRegistrationStatuses() {
        List<RegistrationStatusGetDto> registrationStatuses =
                registrationStatusService.getAllRegistrationStatuses();
        return ResponseEntity.ok(registrationStatuses);
    }

    @SuppressWarnings("checkstyle:Indentation")
    @PostMapping
    @Operation(
            summary = "Создать новый статус регистрации",
            description = "Создает новую запись о статусе регистрации пользователя на мероприятие")
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "201", description = "Статус регистрации успешно создан",
                            content = @Content(
                                    schema = @Schema(
                                            implementation = RegistrationStatusGetDto.class))),
                    @ApiResponse(responseCode = "400", description = "Некорректные данные"),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Пользователь или мероприятие не найдены")
            })
    public ResponseEntity<RegistrationStatusGetDto> createRegistrationStatus(
            @Parameter(description = "Данные для создания статуса регистрации", required = true)
            @Valid @RequestBody RegistrationStatusCreateDto registrationStatusCreateDto
    ) {
        RegistrationStatusGetDto createdRegistrationStatus
                = registrationStatusService.createRegistrationStatus(registrationStatusCreateDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdRegistrationStatus);
    }

    @SuppressWarnings("checkstyle:Indentation")
    @PutMapping("/{id}")
    @Operation(
            summary = "Обновить статус регистрации",
            description = "Обновляет данные статуса регистрации по указанному ID")
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Статус регистрации успешно обновлен",
                            content = @Content(
                                    schema = @Schema(
                                            implementation = RegistrationStatusGetDto.class))),
                    @ApiResponse(responseCode = "400", description = "Некорректные данные"),
                    @ApiResponse(responseCode = "404", description = "Статус регистрации не найден")
            })
    public ResponseEntity<RegistrationStatusGetDto> updateRegistrationStatus(
            @Parameter(description = "ID статуса регистрации для обновления", required = true)
            @PathVariable Long id,
            @Parameter(description = "Обновленные данные статуса регистрации", required = true)
            @Valid @RequestBody RegistrationStatusCreateDto registrationStatusCreateDto
    ) {
        RegistrationStatusGetDto updatedRegistrationStatus
                = registrationStatusService.updateRegistrationStatus(
                id, registrationStatusCreateDto);
        return ResponseEntity.ok(updatedRegistrationStatus);
    }

    @SuppressWarnings("checkstyle:Indentation")
    @DeleteMapping("/{id}")
    @Operation(
            summary = "Удалить статус регистрации",
            description = "Удаляет запись о статусе регистрации по указанному ID")
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "204",
                            description = "Статус регистрации успешно удален"),
                    @ApiResponse(responseCode = "404", description = "Статус регистрации не найден")
            })
    public ResponseEntity<Void> deleteRegistrationStatus(
            @Parameter(description = "ID статуса регистрации для удаления", required = true)
            @PathVariable Long id
    ) {
        registrationStatusService.deleteRegistrationStatus(id);
        return ResponseEntity.noContent().build();
    }

    @SuppressWarnings("checkstyle:Indentation")
    @GetMapping("/filtered")
    @Operation(
            summary = "Фильтрация подтвержденных пользователей",
            description
                    = "Возвращает список подтвержденных пользователей по ID мероприятия и фамилии")
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Список подтвержденных пользователей",
                            content = @Content(
                                    schema = @Schema(implementation = ConfirmedUserDto.class))),
                    @ApiResponse(
                            responseCode = "400", description = "Некорректные параметры запроса"),
                    @ApiResponse(responseCode = "404", description = "Мероприятие не найдено")
            })
    public ResponseEntity<List<ConfirmedUserDto>> getConfirmedUsersByEventIdAndLastName(
            @Parameter(description = "ID мероприятия", required = true)
            @RequestParam Long eventId,
            @Parameter(description = "Фамилия пользователя для поиска", required = true)
            @RequestParam String lastName
    ) {
        List<ConfirmedUserDto> confirmedUsers
                = registrationStatusService.getConfirmedUsersByEventIdAndLastName(
                eventId, lastName);
        return ResponseEntity.ok(confirmedUsers);
    }
}