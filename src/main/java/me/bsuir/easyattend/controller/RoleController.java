package me.bsuir.easyattend.controller;

import me.bsuir.easyattend.dto.create.RoleCreateDto;
import me.bsuir.easyattend.dto.get.RoleGetDto;
import me.bsuir.easyattend.service.RoleService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/roles")
public class RoleController {

    private final RoleService roleService;

    @Autowired
    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<RoleGetDto> getRoleById(@PathVariable Long id) {
        RoleGetDto role = roleService.getRoleById(id);
        return ResponseEntity.ok(role);
    }

    @GetMapping
    public ResponseEntity<List<RoleGetDto>> getAllRoles() {
        List<RoleGetDto> roles = roleService.getAllRoles();
        return ResponseEntity.ok(roles);
    }

    @PostMapping
    public ResponseEntity<RoleGetDto> createRole(@Valid @RequestBody RoleCreateDto roleCreateDto) {
        RoleGetDto createdRole = roleService.createRole(roleCreateDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdRole);
    }

    @PutMapping("/{id}")
    public ResponseEntity<RoleGetDto> updateRole(@PathVariable Long id, @Valid @RequestBody RoleCreateDto roleCreateDto) {
        RoleGetDto updatedRole = roleService.updateRole(id, roleCreateDto);
        return ResponseEntity.ok(updatedRole);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRole(@PathVariable Long id) {
        roleService.deleteRole(id);
        return ResponseEntity.noContent().build();
    }
}