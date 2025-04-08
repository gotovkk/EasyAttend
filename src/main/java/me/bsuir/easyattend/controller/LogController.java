package me.bsuir.easyattend.controller;

import me.bsuir.easyattend.service.LogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/logs")
public class LogController {

    private final LogService logService;

    @Autowired
    public LogController(LogService logService) {
        this.logService = logService;
    }

    @GetMapping("/{date}")
    public ResponseEntity<String> getLogsByDate(@PathVariable String date) throws Exception {
        try {
            String logs = logService.getLogsByDate(date);
            return ResponseEntity.ok(logs);
        } catch (Exception e) {
            throw e; // Исключение будет обработано MyExceptionHandler
        }
    }
}