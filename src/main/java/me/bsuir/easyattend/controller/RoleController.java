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
import me.bsuir.easyattend.dto.create.RoleCreateDto;
import me.bsuir.easyattend.dto.get.RoleGetDto;
import me.bsuir.easyattend.service.RoleService;
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
@RequestMapping("/api/roles")
@Tag(name = "Управление ролями", description = "API для работы с ролями пользователей")
public class RoleController {

    private final RoleService roleService;

    @Autowired
    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @SuppressWarnings({"checkstyle:Indentation", "checkstyle:AnnotationLocation"})
    @GetMapping("/{id}")
    @Operation(
            summary = "Получить роль по ID",
            description = "Возвращает информацию о роли по её идентификатору")
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200", description = "Роль найдена",
                            content = @Content(
                                    schema = @Schema(implementation = RoleGetDto.class))),
                    @ApiResponse(responseCode = "404", description = "Роль не найдена")
            })
    public ResponseEntity<RoleGetDto> getRoleById(
            @Parameter(description = "ID роли", required = true)
            @PathVariable Long id
    ) {
        RoleGetDto role = roleService.getRoleById(id);
        return ResponseEntity.ok(role);
    }

    @GetMapping
    @Operation(
            summary = "Получить все роли",
            description = "Возвращает список всех доступных ролей в системе")
    @ApiResponse(
            responseCode = "200", description = "Список ролей",
            content = @Content(schema = @Schema(implementation = RoleGetDto.class)))
    public ResponseEntity<List<RoleGetDto>> getAllRoles() {
        List<RoleGetDto> roles = roleService.getAllRoles();
        return ResponseEntity.ok(roles);
    }

    @SuppressWarnings({"checkstyle:Indentation", "checkstyle:AnnotationLocation"})
    @PostMapping
    @Operation(
            summary = "Создать новую роль",
            description = "Создает новую роль с указанными параметрами")
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "201", description = "Роль успешно создана",
                            content = @Content(
                                    schema = @Schema(implementation = RoleGetDto.class))),
                    @ApiResponse(responseCode = "400", description = "Некорректные данные")
            })
    public ResponseEntity<RoleGetDto> createRole(
            @Parameter(description = "Данные для создания роли", required = true)
            @Valid @RequestBody RoleCreateDto roleCreateDto
    ) {
        RoleGetDto createdRole = roleService.createRole(roleCreateDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdRole);
    }

    @SuppressWarnings("checkstyle:Indentation")
    @PutMapping("/{id}")
    @Operation(
            summary = "Обновить роль",
            description = "Обновляет данные роли по указанному ID")
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200", description = "Роль успешно обновлена",
                            content = @Content(
                                    schema = @Schema(implementation = RoleGetDto.class))),
                    @ApiResponse(responseCode = "400", description = "Некорректные данные"),
                    @ApiResponse(responseCode = "404", description = "Роль не найдена")
            })
    public ResponseEntity<RoleGetDto> updateRole(
            @Parameter(description = "ID роли для обновления", required = true)
            @PathVariable Long id,
            @Parameter(description = "Обновленные данные роли", required = true)
            @Valid @RequestBody RoleCreateDto roleCreateDto
    ) {
        RoleGetDto updatedRole = roleService.updateRole(id, roleCreateDto);
        return ResponseEntity.ok(updatedRole);
    }

    @SuppressWarnings("checkstyle:Indentation")
    @DeleteMapping("/{id}")
    @Operation(
            summary = "Удалить роль",
            description = "Удаляет роль по указанному ID")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "204", description = "Роль успешно удалена"),
                    @ApiResponse(responseCode = "404", description = "Роль не найдена")
            })
    public ResponseEntity<Void> deleteRole(
            @Parameter(description = "ID роли для удаления", required = true)
            @PathVariable Long id
    ) {
        roleService.deleteRole(id);
        return ResponseEntity.noContent().build();
    }
}