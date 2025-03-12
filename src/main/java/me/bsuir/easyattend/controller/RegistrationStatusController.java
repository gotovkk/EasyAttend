package me.bsuir.easyattend.controller;

import me.bsuir.easyattend.dto.create.RegistrationStatusCreateDto;
import me.bsuir.easyattend.dto.get.RegistrationStatusGetDto;
import me.bsuir.easyattend.service.RegistrationStatusService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/registration-statuses")
public class RegistrationStatusController {

    private final RegistrationStatusService registrationStatusService;

    @Autowired
    public RegistrationStatusController(RegistrationStatusService registrationStatusService) {
        this.registrationStatusService = registrationStatusService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<RegistrationStatusGetDto> getRegistrationStatusById(@PathVariable Long id) {
        RegistrationStatusGetDto registrationStatus = registrationStatusService.getRegistrationStatusById(id);
        return ResponseEntity.ok(registrationStatus);
    }

    @GetMapping
    public ResponseEntity<List<RegistrationStatusGetDto>> getAllRegistrationStatuses() {
        List<RegistrationStatusGetDto> registrationStatuses = registrationStatusService.getAllRegistrationStatuses();
        return ResponseEntity.ok(registrationStatuses);
    }

    @PostMapping
    public ResponseEntity<RegistrationStatusGetDto> createRegistrationStatus(@Valid @RequestBody RegistrationStatusCreateDto registrationStatusCreateDto) {
        RegistrationStatusGetDto createdRegistrationStatus = registrationStatusService.createRegistrationStatus(registrationStatusCreateDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdRegistrationStatus);
    }

    @PutMapping("/{id}")
    public ResponseEntity<RegistrationStatusGetDto> updateRegistrationStatus(@PathVariable Long id, @Valid @RequestBody RegistrationStatusCreateDto registrationStatusCreateDto) {
        RegistrationStatusGetDto updatedRegistrationStatus = registrationStatusService.updateRegistrationStatus(id, registrationStatusCreateDto);
        return ResponseEntity.ok(updatedRegistrationStatus);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRegistrationStatus(@PathVariable Long id) {
        registrationStatusService.deleteRegistrationStatus(id);
        return ResponseEntity.noContent().build();
    }
}