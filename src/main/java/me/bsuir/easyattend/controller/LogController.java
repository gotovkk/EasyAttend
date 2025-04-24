package me.bsuir.easyattend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import me.bsuir.easyattend.logs.LogService;
import me.bsuir.easyattend.logs.LogService.LogTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.nio.file.Path;
import java.util.UUID;

@RestController
@RequestMapping("/api/logs")
@Tag(name = "Управление логами", description = "API для работы с логами системы")
public class LogController {

    private static final Logger logger = LoggerFactory.getLogger(LogController.class);
    private final LogService logService;

    @Autowired
    public LogController(LogService logService) {
        this.logService = logService;
    }

    @PostMapping("/generate")
    @Operation(summary = "Создать задачу для генерации лог-файла асинхронно")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "202", description = "Задача создана, возвращен ID задачи"),
            @ApiResponse(responseCode = "400", description = "Некорректный формат даты")
    })
    public ResponseEntity<String> createLogFileTask(
            @Parameter(description = "Дата в формате YYYY-MM-DD", example = "2023-12-31", required = true)
            @RequestParam String date,
            @Parameter(description = "Длительность выполнения задачи в секундах", example = "10")
            @RequestParam(defaultValue = "10") Long durationInSeconds) {
        try {
            String taskId = logService.createLogFileTask(date, durationInSeconds); // Передаём оба аргумента
            if (taskId == null || taskId.isEmpty()) {
                logger.error("Failed to create task for date: {}", date);
                return new ResponseEntity<>("Failed to create task", HttpStatus.INTERNAL_SERVER_ERROR);
            }
            logger.info("Created log file task with ID: {}", taskId);
            return new ResponseEntity<>(taskId, HttpStatus.ACCEPTED);
        } catch (IllegalArgumentException e) {
            logger.error("Invalid date format: {}", date, e);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/status/{taskId}")
    @Operation(summary = "Получить статус задачи генерации лог-файла")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Статус задачи возвращен"),
            @ApiResponse(responseCode = "400", description = "Некорректный формат ID задачи"),
            @ApiResponse(responseCode = "404", description = "Задача не найдена")
    })
    public ResponseEntity<LogTask> getTaskStatus(
            @Parameter(description = "ID задачи", example = "123e4567-e89b-12d3-a456-426614174000")
            @PathVariable String taskId) {
        try {
            UUID.fromString(taskId);
        } catch (IllegalArgumentException e) {
            logger.error("Invalid task ID format: {}", taskId);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        try {
            LogTask task = logService.getTaskStatus(taskId);
            logger.info("Retrieved status for task ID: {}", taskId);
            return new ResponseEntity<>(task, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            logger.warn("Task not found: {}", taskId);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }


    @GetMapping
    @Operation(summary = "Получить информацию о сервисе логов")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Информация о сервисе")
    })
    public ResponseEntity<String> getLogServiceInfo() {
        logger.info("Accessed log service info");
        return new ResponseEntity<>("Log Service: Use /generate to create a log file task", HttpStatus.OK);
    }

    @GetMapping("/download/{taskId}")
    @Operation(summary = "Скачать сгенерированный лог-файл по ID задачи")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Файл успешно скачан"),
            @ApiResponse(responseCode = "400", description = "Некорректный формат ID задачи"),
            @ApiResponse(responseCode = "404", description = "Файл или задача не найдены"),
            @ApiResponse(responseCode = "500", description = "Ошибка при чтении файла")
    })
    public ResponseEntity<Resource> downloadLogFile(
            @Parameter(description = "ID задачи", example = "123e4567-e89b-12d3-a456-426614174000")
            @PathVariable String taskId) {
        try {
            UUID.fromString(taskId); // Валидация формата UUID
        } catch (IllegalArgumentException e) {
            logger.error("Invalid task ID format: {}", taskId);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        Path filePath = logService.getLogFilePath(taskId);
        if (filePath == null) {
            logger.warn("File not found for task ID: {}", taskId);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        try {
            Resource resource = new FileSystemResource(filePath.toFile());
            if (!resource.exists()) {
                logger.warn("File does not exist: {}", filePath);
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            logger.info("Downloading file for task ID: {}, file: {}", taskId, filePath);
            return ResponseEntity.ok()
                    .contentType(MediaType.TEXT_PLAIN)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filePath.getFileName().toString() + "\"")
                    .body(resource);

        } catch (Exception e) {
            logger.error("Error downloading file for task ID: {}", taskId, e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}