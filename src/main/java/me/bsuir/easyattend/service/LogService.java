package me.bsuir.easyattend.service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class LogService {

    private static final String LOG_FILE_PATH = "logs/easyattend.log";
    private static final Logger logger = LoggerFactory.getLogger(LogService.class);

    public String getLogsByDate(String date) throws Exception {
        try {
            LocalDate targetDate = LocalDate.parse(date, DateTimeFormatter.ISO_LOCAL_DATE);
            String datePrefix = targetDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            String outputFilePath = "logs/logs-" + datePrefix + ".log"; // Новый файл для даты

            try (BufferedReader reader = new BufferedReader(new FileReader(LOG_FILE_PATH))) {
                String logs = reader.lines()
                        .filter(line -> line.startsWith(datePrefix))
                        .collect(Collectors.joining("\n"));

                if (logs.isEmpty()) {
                    logger.warn("No logs found for date: {}", date);
                    return "No logs found for " + date;
                }

                // Сохраняем логи в отдельный файл
                saveLogsToFile(outputFilePath, logs);
                return logs;
            }
        } catch (Exception e) {
            logger.error("Error reading logs for date {}: {}", date, e.getMessage());
            throw new IllegalArgumentException(
                    "Invalid date format or error reading logs: " + e.getMessage());
        }
    }

    private void saveLogsToFile(String filePath, String logs) {
        try (FileWriter writer = new FileWriter(filePath)) {
            writer.write(logs);
            logger.info("Logs for date saved to file: {}", filePath);
        } catch (IOException e) {
            logger.error("Error saving logs to file {}: {}", filePath, e.getMessage());
            throw new IllegalArgumentException("Error saving logs to file: " + e.getMessage());
        }
    }
}