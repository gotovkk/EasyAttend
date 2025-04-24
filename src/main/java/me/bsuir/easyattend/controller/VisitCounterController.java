package me.bsuir.easyattend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import me.bsuir.easyattend.logs.VisitCounter;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/visits")
@Tag(name = "Учет посещений", description = "API для учета посещений страниц")
public class VisitCounterController {

    private final VisitCounter visitCounter;

    public VisitCounterController(VisitCounter visitCounter) {
        this.visitCounter = visitCounter;
    }

    @GetMapping
    @Operation(summary = "Получить количество посещений для указанного URL")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Количество посещений возвращено")
    })
    public ResponseEntity<Long> getVisitCount(
            @Parameter(description = "URL для подсчета посещений", example = "/api/logs")
            @RequestParam String url) {
        long count = visitCounter.getVisitCount(url);
        return ResponseEntity.ok(count);
    }
}