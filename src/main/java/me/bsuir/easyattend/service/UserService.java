package me.bsuir.easyattend.service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import me.bsuir.easyattend.dto.create.UserCreateDto;
import me.bsuir.easyattend.dto.get.RoleGetDto;
import me.bsuir.easyattend.dto.get.UserGetDto;
import me.bsuir.easyattend.exception.ResourceNotFoundException;
import me.bsuir.easyattend.mapper.RoleMapper;
import me.bsuir.easyattend.mapper.UserMapper;
import me.bsuir.easyattend.model.Role;
import me.bsuir.easyattend.model.RoleType;
import me.bsuir.easyattend.model.User;
import me.bsuir.easyattend.repository.EventRepository;
import me.bsuir.easyattend.repository.RegistrationStatusRepository;
import me.bsuir.easyattend.repository.RoleRepository;
import me.bsuir.easyattend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleMapper roleMapper;

    @Autowired
    public UserService(
            UserRepository userRepository,
            UserMapper userMapper,
            RoleRepository roleRepository,
            PasswordEncoder passwordEncoder,
            RoleService roleService,
            RoleMapper roleMapper,
            EventRepository eventRepository,
            RegistrationStatusRepository registrationStatusRepository
    ) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleMapper = roleMapper;
    }

    public UserGetDto getUserById(Long id) {
        return userRepository.findById(id)
                .map(userMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id " + id));
    }

    @Transactional(readOnly = true)
    public List<UserGetDto> getAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream()
                .map(userMapper::toDto).toList();
    }

    @Transactional
    public UserGetDto createUser(UserCreateDto userCreateDto) {
        User user = userMapper.toEntity(userCreateDto);
        user.setPassword(passwordEncoder.encode(userCreateDto.getPassword()));
        user.setRegistrationDate(LocalDateTime.now());

        Set<Role> roles = new HashSet<>();
        if (userCreateDto.getRoleIds() != null && !userCreateDto.getRoleIds().isEmpty()) {
            userCreateDto.getRoleIds().forEach(roleId -> {
                Role role = roleRepository.findById(roleId)
                        .orElseThrow(()
                                -> new ResourceNotFoundException(
                                "Role not found with id "
                                        + roleId));
                roles.add(role);
            });
        } else {
            // Assign default "USER" role if no roles are specified
            Role userRole = roleRepository.findByName(RoleType.USER)
                    .orElseGet(() -> {
                        // If "USER" role doesn't exist, create it
                        Role newRole = new Role();
                        newRole.setName(RoleType.USER);
                        return roleRepository.save(newRole);
                    });
            roles.add(userRole);
        }
        user.setRoles(roles);

        User savedUser = userRepository.save(user);
        UserGetDto userDto = userMapper.toDto(savedUser);
        Set<RoleGetDto> roleGetDtos
                = savedUser
                .getRoles()
                .stream()
                .map(roleMapper::toDto)
                .collect(Collectors.toSet());
        userDto.setRoles(roleGetDtos);

        return userDto;
    }

    @Transactional
    public UserGetDto updateUser(Long id, UserCreateDto userCreateDto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id " + id));

        userMapper.updateUserFromDto(userCreateDto, user);

        if (userCreateDto.getPassword() != null && !userCreateDto.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(userCreateDto.getPassword()));
        }

        if (userCreateDto.getRoleIds() != null) {
            Set<Role> newRoles = new HashSet<>();
            for (Long roleId : userCreateDto.getRoleIds()) {
                Role role = roleRepository.findById(roleId)
                        .orElseThrow(()
                                -> new ResourceNotFoundException(
                                "Role not found with id " + roleId));
                newRoles.add(role);
            }
            user.setRoles(newRoles);
        }

        User updatedUser = userRepository.save(user);
        return userMapper.toDto(updatedUser);
    }

    @Transactional
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id " + id));
        userRepository.delete(user);
    }
}