package me.bsuir.easyattend.service;

import java.util.List;
import java.util.stream.Collectors;
import me.bsuir.easyattend.dto.create.RoleCreateDto;
import me.bsuir.easyattend.dto.get.RoleGetDto;
import me.bsuir.easyattend.exception.ResourceNotFoundException;
import me.bsuir.easyattend.mapper.RoleMapper;
import me.bsuir.easyattend.model.Role;
import me.bsuir.easyattend.model.RoleType;
import me.bsuir.easyattend.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class RoleService {

    private final RoleRepository roleRepository;
    private final RoleMapper roleMapper;

    @Autowired
    public RoleService(RoleRepository roleRepository, RoleMapper roleMapper) {
        this.roleRepository = roleRepository;
        this.roleMapper = roleMapper;
    }

    @Transactional(readOnly = true)
    public RoleGetDto getRoleById(Long id) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found with id " + id));
        return roleMapper.toDto(role);
    }

    @Transactional(readOnly = true)
    public List<RoleGetDto> getAllRoles() {
        List<Role> roles = roleRepository.findAll();
        return roles.stream()
                .map(roleMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public RoleGetDto createRole(RoleCreateDto roleCreateDto) {
        Role role = roleMapper.toEntity(roleCreateDto);
        Role savedRole = roleRepository.save(role);
        return roleMapper.toDto(savedRole);
    }

    @Transactional
    public RoleGetDto updateRole(Long id, RoleCreateDto roleCreateDto) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found with id " + id));

        roleMapper.updateRoleFromDto(roleCreateDto, role);
        Role updatedRole = roleRepository.save(role);
        return roleMapper.toDto(updatedRole);
    }

    @Transactional
    public void deleteRole(Long id) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found with id " + id));
        roleRepository.delete(role);
    }

    @Transactional(readOnly = true)
    public Role findRoleByName(RoleType roleName) {
        return roleRepository.findByName(roleName)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Role not found with name " + roleName));
    }
}