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
import me.bsuir.easyattend.annotation.Timed;
import me.bsuir.easyattend.dto.create.EventCreateDto;
import me.bsuir.easyattend.dto.get.EventAttendeeDto;
import me.bsuir.easyattend.dto.get.EventGetDto;
import me.bsuir.easyattend.service.EventService;
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
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/events")
@Tag(name = "Управление мероприятиями",
     description = "API для работы с мероприятиями и их участниками")
public class EventController {

    private final EventService eventService;

    @Autowired
    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @SuppressWarnings("checkstyle:Indentation")
    @GetMapping("/{id}")
    @Timed
    @Operation(
            summary = "Получить мероприятие по ID",
            description = "Возвращает информацию о мероприятии по его идентификатору")
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200", description = "Мероприятие найдено",
                            content = @Content(schema = @Schema(
                                    implementation = EventGetDto.class))),
                    @ApiResponse(responseCode = "404", description = "Мероприятие не найдено")
            })
    public ResponseEntity<EventGetDto> getEventById(
            @Parameter(description = "ID мероприятия", required = true)
            @PathVariable Long id) {
        EventGetDto event = eventService.getEventById(id);
        return ResponseEntity.ok(event);
    }

    @GetMapping
    @Operation(summary = "Получить все мероприятия",
               description = "Возвращает список всех доступных мероприятий")
    @ApiResponse(
            responseCode = "200", description = "Список мероприятий",
            content = @Content(schema = @Schema(implementation = EventGetDto.class)))
    public ResponseEntity<List<EventGetDto>> getAllEvents() {
        List<EventGetDto> events = eventService.getAllEvents();
        return ResponseEntity.ok(events);
    }

    @SuppressWarnings("checkstyle:Indentation")
    @PostMapping
    @Operation(summary = "Создать новое мероприятие",
               description = "Создает новое мероприятие с предоставленными данными")
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "201", description = "Мероприятие успешно создано",
                            content = @Content(
                                    schema = @Schema(implementation = EventGetDto.class))),
                    @ApiResponse(responseCode = "400", description = "Некорректные данные")
            })
    public ResponseEntity<EventGetDto> createEvent(
            @Parameter(description = "Данные для создания мероприятия", required = true)
            @Valid @RequestBody EventCreateDto eventCreateDto) {
        EventGetDto createdEvent = eventService.createEvent(eventCreateDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdEvent);
    }

    @SuppressWarnings("checkstyle:Indentation")
    @PutMapping("/{id}")
    @Operation(summary = "Обновить мероприятие",
               description = "Обновляет данные мероприятия с указанным ID")
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200", description = "Мероприятие успешно обновлено",
                            content = @Content(schema = @Schema(
                                    implementation = EventGetDto.class))),
                    @ApiResponse(responseCode = "400", description = "Некорректные данные"),
                    @ApiResponse(responseCode = "404", description = "Мероприятие не найдено")
            })
    public ResponseEntity<EventGetDto> updateEvent(
            @Parameter(description = "ID мероприятия для обновления", required = true)
            @PathVariable Long id,
            @Parameter(description = "Обновленные данные мероприятия", required = true)
            @Valid @RequestBody EventCreateDto eventCreateDto) {
        EventGetDto updatedEvent = eventService.updateEvent(id, eventCreateDto);
        return ResponseEntity.ok(updatedEvent);
    }

    @SuppressWarnings("checkstyle:Indentation")
    @DeleteMapping("/{eventId}/attendees/{userId}")
    @Operation(
            summary = "Удалить участника мероприятия",
            description = "Удаляет указанного пользователя из списка участников мероприятия")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "204", description = "Участник успешно удален"),
                    @ApiResponse(responseCode = "404",
                                 description = "Мероприятие или пользователь не найдены")
            })
    public ResponseEntity<Void> removeAttendeeFromEvent(
            @Parameter(description = "ID мероприятия", required = true)
            @PathVariable Long eventId,
            @Parameter(description = "ID пользователя для удаления", required = true)
            @PathVariable Long userId) {
        eventService.removeAttendeeFromEvent(eventId, userId);
        return ResponseEntity.noContent().build();
    }

    @SuppressWarnings("checkstyle:Indentation")
    @GetMapping("/{id}/attendees")
    @Operation(
            summary = "Получить участников мероприятия",
            description = "Возвращает список всех участников указанного мероприятия")
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200", description = "Список участников",
                            content = @Content(schema = @Schema(
                                    implementation = EventAttendeeDto.class))),
                    @ApiResponse(responseCode = "404", description = "Мероприятие не найдено")
            })
    public ResponseEntity<List<EventAttendeeDto>> getAttendeesByEventId(
            @Parameter(description = "ID мероприятия", required = true)
            @PathVariable Long id) {
        List<EventAttendeeDto> attendees = eventService.getAttendeesByEventId(id);
        return ResponseEntity.ok(attendees);
    }

    @SuppressWarnings("checkstyle:Indentation")
    @DeleteMapping("/{id}")
    @Operation(summary = "Удалить мероприятие", description = "Удаляет мероприятие с указанным ID")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "204", description = "Мероприятие успешно удалено"),
                    @ApiResponse(responseCode = "404", description = "Мероприятие не найдено")
            })
    public ResponseEntity<Void> deleteEvent(
            @Parameter(description = "ID мероприятия для удаления", required = true)
            @PathVariable Long id) {
        eventService.deleteEvent(id);
        return ResponseEntity.noContent().build();
    }
}