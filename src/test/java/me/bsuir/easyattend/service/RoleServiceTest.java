package me.bsuir.easyattend.service;

import me.bsuir.easyattend.dto.create.RoleCreateDto;
import me.bsuir.easyattend.dto.get.RoleGetDto;
import me.bsuir.easyattend.exception.ResourceNotFoundException;
import me.bsuir.easyattend.mapper.RoleMapper;
import me.bsuir.easyattend.model.Role;
import me.bsuir.easyattend.model.RoleType;
import me.bsuir.easyattend.repository.RoleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RoleServiceTest {

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private RoleMapper roleMapper;

    @InjectMocks
    private RoleService roleService;

    private Role role;
    private RoleGetDto roleGetDto;
    private RoleCreateDto roleCreateDto;

    @BeforeEach
    void setUp() {
        role = new Role();
        role.setId(1L);
        role.setName(RoleType.ADMIN);

        roleGetDto = new RoleGetDto();
        roleGetDto.setId(1L);
        roleGetDto.setName(RoleType.ADMIN);

        roleCreateDto = new RoleCreateDto();
        roleCreateDto.setName(RoleType.ADMIN);
    }

    @Test
    void getRoleById_Success() {
        when(roleRepository.findById(1L)).thenReturn(Optional.of(role));
        when(roleMapper.toDto(role)).thenReturn(roleGetDto);

        RoleGetDto result = roleService.getRoleById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(RoleType.ADMIN, result.getName());
        verify(roleRepository).findById(1L);
        verify(roleMapper).toDto(role);
    }

    @Test
    void getRoleById_NotFound() {
        when(roleRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> roleService.getRoleById(1L));
        verify(roleRepository).findById(1L);
        verifyNoInteractions(roleMapper);
    }

    @Test
    void getAllRoles_Success() {
        List<Role> roles = Arrays.asList(role);
        List<RoleGetDto> roleGetDtos = Arrays.asList(roleGetDto);

        when(roleRepository.findAll()).thenReturn(roles);
        when(roleMapper.toDto(role)).thenReturn(roleGetDto);

        List<RoleGetDto> result = roleService.getAllRoles();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(roleGetDto, result.get(0));
        verify(roleRepository).findAll();
        verify(roleMapper).toDto(role);
    }

    @Test
    void createRole_Success() {
        when(roleMapper.toEntity(roleCreateDto)).thenReturn(role);
        when(roleRepository.save(role)).thenReturn(role);
        when(roleMapper.toDto(role)).thenReturn(roleGetDto);

        RoleGetDto result = roleService.createRole(roleCreateDto);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(RoleType.ADMIN, result.getName());
        verify(roleMapper).toEntity(roleCreateDto);
        verify(roleRepository).save(role);
        verify(roleMapper).toDto(role);
    }

    @Test
    void updateRole_Success() {
        when(roleRepository.findById(1L)).thenReturn(Optional.of(role));
        // Mock the void method using doNothing()
        doNothing().when(roleMapper).updateRoleFromDto(roleCreateDto, role);
        when(roleRepository.save(role)).thenReturn(role);
        when(roleMapper.toDto(role)).thenReturn(roleGetDto);

        RoleGetDto result = roleService.updateRole(1L, roleCreateDto);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(RoleType.ADMIN, result.getName());
        verify(roleRepository).findById(1L);
        verify(roleMapper).updateRoleFromDto(roleCreateDto, role);
        verify(roleRepository).save(role);
        verify(roleMapper).toDto(role);
    }

    @Test
    void updateRole_NotFound() {
        when(roleRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> roleService.updateRole(1L, roleCreateDto));
        verify(roleRepository).findById(1L);
        verifyNoMoreInteractions(roleMapper, roleRepository);
    }

    @Test
    void deleteRole_Success() {
        when(roleRepository.findById(1L)).thenReturn(Optional.of(role));

        roleService.deleteRole(1L);

        verify(roleRepository).findById(1L);
        verify(roleRepository).delete(role);
    }

    @Test
    void deleteRole_NotFound() {
        when(roleRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> roleService.deleteRole(1L));
        verify(roleRepository).findById(1L);
        verifyNoMoreInteractions(roleRepository);
    }

    @Test
    void findRoleByName_Success() {
        when(roleRepository.findByName(RoleType.ADMIN)).thenReturn(Optional.of(role));

        Role result = roleService.findRoleByName(RoleType.ADMIN);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(RoleType.ADMIN, result.getName());
        verify(roleRepository).findByName(RoleType.ADMIN);
    }

    @Test
    void findRoleByName_NotFound() {
        when(roleRepository.findByName(RoleType.ADMIN)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> roleService.findRoleByName(RoleType.ADMIN));
        verify(roleRepository).findByName(RoleType.ADMIN);
    }
}