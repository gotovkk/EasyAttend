package me.bsuir.easyattend.logs;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class LogService {

    private final String logFilePath;
    private static final Logger logger = LoggerFactory.getLogger(LogService.class);
    private final ConcurrentHashMap<String, LogTask> tasks = new ConcurrentHashMap<>();

    public LogService(@Value("${log.file.path:logs/easyattend.log}") String logFilePath) {
        this.logFilePath = logFilePath;
    }

    public String createLogFileTask(String date, Long durationInSeconds) {        String taskId = UUID.randomUUID().toString();
        LogTask task = new LogTask(taskId, "PENDING", null, null);
        tasks.put(taskId, task);

        // Асинхронно выполняем задачу
        CompletableFuture.runAsync(() -> {
            // Устанавливаем статус IN_PROGRESS
            tasks.get(taskId).setStatus("IN_PROGRESS");
            logger.info("Task {} is now IN_PROGRESS", taskId);

            try {
                Thread.sleep(10000);

                Path filePath = Files.createTempFile("log-" + taskId, ".log");
                Files.write(filePath, ("Log for " + date).getBytes());
                tasks.get(taskId).setStatus("COMPLETED");
                tasks.get(taskId).setFilePath(filePath);
                logger.info("Task {} completed, file: {}", taskId, filePath);
            } catch (Exception e) {
                tasks.get(taskId).setStatus("FAILED");
                tasks.get(taskId).setErrorMessage(e.getMessage());
                logger.error("Task {} failed: {}", taskId, e.getMessage());
            }
        });

        return taskId;
    }

    @Async
    public void generateLogFileAsync(LogTask task, String date) {
        try {
            LocalDate targetDate = LocalDate.parse(date, DateTimeFormatter.ISO_LOCAL_DATE);
            String datePrefix = targetDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            String outputFilePath = "logs/logs-" + task.getTaskId() + "-" + datePrefix + ".log";

            try (BufferedReader reader = new BufferedReader(new FileReader(logFilePath))) {
                String logs = reader.lines()
                        .filter(line -> line.startsWith(datePrefix))
                        .collect(Collectors.joining("\n"));

                if (logs.isEmpty()) {
                    task.setStatus("FAILED");
                    task.setErrorMessage("No logs found for date: " + date);
                    logger.warn("No logs found for date: {}", date);
                    return;
                }

                saveLogsToFile(outputFilePath, logs);
                task.setFilePath(Paths.get(outputFilePath));
                task.setStatus("COMPLETED");
            }
        } catch (Exception e) {
        }
    }

    private void saveLogsToFile(String filePath, String logs) {
        try (FileWriter writer = new FileWriter(filePath)) {
            writer.write(logs);
        } catch (IOException e) {
        }
    }

    public LogTask getTaskStatus(String taskId) {
        LogTask task = tasks.get(taskId);
        if (task == null) {
            throw new IllegalArgumentException("Task not found: " + taskId);
        }
        return task;
    }

    public Path getLogFilePath(String taskId) {
        LogTask task = tasks.get(taskId);
        if (task != null && "COMPLETED".equals(task.getStatus())) {
            return task.getFilePath();
        }
        return null;
    }

    public static class LogTask {
        private final String taskId;
        private String status = "PENDING";
        @JsonIgnore
        private String errorMessage;
        @JsonIgnore
        private Path filePath;

        public LogTask(String taskId, String status, Path filePath, String errorMessage) {
            this.taskId = taskId;
            this.status = status;
            this.filePath = filePath;
            this.errorMessage = errorMessage;
        }

        public String getTaskId() {
            return taskId;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getErrorMessage() {
            return errorMessage;
        }

        public void setErrorMessage(String errorMessage) {
            this.errorMessage = errorMessage;
        }

        public Path getFilePath() {
            return filePath;
        }

        public void setFilePath(Path filePath) {
            this.filePath = filePath;
        }
    }
}