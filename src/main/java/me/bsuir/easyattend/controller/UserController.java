package me.bsuir.easyattend.controller;

import me.bsuir.easyattend.dto.create.UserCreateDto;
import me.bsuir.easyattend.dto.get.UserGetDto;
import me.bsuir.easyattend.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserGetDto> getUserById(@PathVariable Long id) {
        UserGetDto user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    @GetMapping
    public ResponseEntity<List<UserGetDto>> getAllUsers() {
        List<UserGetDto> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @PostMapping
    public ResponseEntity<UserGetDto> createUser(@Valid @RequestBody UserCreateDto userCreateDto) {
        UserGetDto createdUser = userService.createUser(userCreateDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserGetDto> updateUser(@PathVariable Long id, @Valid @RequestBody UserCreateDto userCreateDto) {
        UserGetDto updatedUser = userService.updateUser(id, userCreateDto);
        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}