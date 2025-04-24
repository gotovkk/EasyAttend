package me.bsuir.easyattend.service;

import me.bsuir.easyattend.dto.create.UserCreateDto;
import me.bsuir.easyattend.dto.get.RoleGetDto;
import me.bsuir.easyattend.dto.get.UserGetDto;
import me.bsuir.easyattend.exception.ResourceNotFoundException;
import me.bsuir.easyattend.mapper.RoleMapper;
import me.bsuir.easyattend.mapper.UserMapper;
import me.bsuir.easyattend.model.Role;
import me.bsuir.easyattend.model.RoleType;
import me.bsuir.easyattend.model.User;
import me.bsuir.easyattend.repository.RoleRepository;
import me.bsuir.easyattend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private RoleMapper roleMapper;

    @InjectMocks
    private UserService userService;

    private User testUser;
    private UserCreateDto testCreateDto;
    private UserGetDto testGetDto;
    private Role testRole;
    private RoleGetDto testRoleGetDto;

    @BeforeEach
    void setUp() {
        // Setup test Role
        testRole = new Role();
        testRole.setId(1L);
        testRole.setName(RoleType.USER);

        // Setup test RoleGetDto
        testRoleGetDto = new RoleGetDto();
        testRoleGetDto.setId(1L);
        testRoleGetDto.setName(RoleType.USER);

        // Setup test User
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setPassword("encodedPassword");
        testUser.setRegistrationDate(LocalDateTime.now());
        testUser.setRoles(new HashSet<>(Set.of(testRole)));

        // Setup test UserCreateDto
        testCreateDto = new UserCreateDto();
        testCreateDto.setUsername("testuser");
        testCreateDto.setPassword("password");
        testCreateDto.setRoleIds(Set.of(1L));

        // Setup test UserGetDto
        testGetDto = new UserGetDto();
        testGetDto.setId(1L);
        testGetDto.setUsername("testuser");
        testGetDto.setRoles(Set.of(testRoleGetDto));
    }

    @Test
    void getUserById_ShouldReturnUser_WhenExists() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userMapper.toDto(testUser)).thenReturn(testGetDto);

        UserGetDto result = userService.getUserById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("testuser", result.getUsername());
        verify(userRepository).findById(1L);
        verify(userMapper).toDto(testUser);
    }

    @Test
    void getUserById_ShouldThrowException_WhenNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.getUserById(1L));
        verify(userRepository).findById(1L);
        verifyNoInteractions(userMapper);
    }

    @Test
    void getAllUsers_ShouldReturnUsersList_WhenUsersExist() {
        when(userRepository.findAll()).thenReturn(List.of(testUser));
        when(userMapper.toDto(testUser)).thenReturn(testGetDto);

        List<UserGetDto> result = userService.getAllUsers();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("testuser", result.get(0).getUsername());
        verify(userRepository).findAll();
        verify(userMapper).toDto(testUser);
    }

    @Test
    void getAllUsers_ShouldReturnEmptyList_WhenNoUsers() {
        when(userRepository.findAll()).thenReturn(Collections.emptyList());

        List<UserGetDto> result = userService.getAllUsers();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(userRepository).findAll();
        verifyNoInteractions(userMapper);
    }

    @Test
    void createUser_ShouldReturnCreatedUser_WithSpecifiedRoles() {
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");
        when(roleRepository.findById(1L)).thenReturn(Optional.of(testRole));
        when(userMapper.toEntity(testCreateDto)).thenReturn(testUser);
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(userMapper.toDto(testUser)).thenReturn(testGetDto);
        when(roleMapper.toDto(testRole)).thenReturn(testRoleGetDto);

        UserGetDto result = userService.createUser(testCreateDto);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(1, result.getRoles().size());
        assertEquals(RoleType.USER, result.getRoles().iterator().next().getName());
        verify(passwordEncoder).encode("password");
        verify(roleRepository).findById(1L);
        verify(userMapper).toEntity(testCreateDto);
        verify(userRepository).save(any(User.class));
        verify(userMapper).toDto(testUser);
        verify(roleMapper).toDto(testRole);
    }

    @Test
    void createUser_ShouldReturnCreatedUser_WithDefaultRole_WhenRoleIdsNull() {
        testCreateDto.setRoleIds(null);

        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");
        when(roleRepository.findByName(RoleType.USER)).thenReturn(Optional.of(testRole));
        when(userMapper.toEntity(testCreateDto)).thenReturn(testUser);
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(userMapper.toDto(testUser)).thenReturn(testGetDto);
        when(roleMapper.toDto(testRole)).thenReturn(testRoleGetDto);

        UserGetDto result = userService.createUser(testCreateDto);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(1, result.getRoles().size());
        assertEquals(RoleType.USER, result.getRoles().iterator().next().getName());
        verify(passwordEncoder).encode("password");
        verify(roleRepository).findByName(RoleType.USER);
        verify(userMapper).toEntity(testCreateDto);
        verify(userRepository).save(any(User.class));
        verify(userMapper).toDto(testUser);
        verify(roleMapper).toDto(testRole);
    }

    @Test
    void createUser_ShouldReturnCreatedUser_WithDefaultRole_WhenRoleIdsEmpty() {
        testCreateDto.setRoleIds(Collections.emptySet());

        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");
        when(roleRepository.findByName(RoleType.USER)).thenReturn(Optional.of(testRole));
        when(userMapper.toEntity(testCreateDto)).thenReturn(testUser);
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(userMapper.toDto(testUser)).thenReturn(testGetDto);
        when(roleMapper.toDto(testRole)).thenReturn(testRoleGetDto);

        UserGetDto result = userService.createUser(testCreateDto);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(1, result.getRoles().size());
        assertEquals(RoleType.USER, result.getRoles().iterator().next().getName());
        verify(passwordEncoder).encode("password");
        verify(roleRepository).findByName(RoleType.USER);
        verify(userMapper).toEntity(testCreateDto);
        verify(userRepository).save(any(User.class));
        verify(userMapper).toDto(testUser);
        verify(roleMapper).toDto(testRole);
    }

    @Test
    void createUser_ShouldCreateDefaultRole_WhenNotExists() {
        testCreateDto.setRoleIds(null);

        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");
        when(roleRepository.findByName(RoleType.USER)).thenReturn(Optional.empty());
        when(roleRepository.save(any(Role.class))).thenReturn(testRole);
        when(userMapper.toEntity(testCreateDto)).thenReturn(testUser);
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(userMapper.toDto(testUser)).thenReturn(testGetDto);
        when(roleMapper.toDto(testRole)).thenReturn(testRoleGetDto);

        UserGetDto result = userService.createUser(testCreateDto);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(1, result.getRoles().size());
        assertEquals(RoleType.USER, result.getRoles().iterator().next().getName());
        verify(passwordEncoder).encode("password");
        verify(roleRepository).findByName(RoleType.USER);
        verify(roleRepository).save(any(Role.class));
        verify(userMapper).toEntity(testCreateDto);
        verify(userRepository).save(any(User.class));
        verify(userMapper).toDto(testUser);
        verify(roleMapper).toDto(testRole);
    }

    @Test
    void createUser_ShouldThrowException_WhenRoleNotFound() {
        testCreateDto.setRoleIds(Set.of(999L));

        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");
        when(roleRepository.findById(999L)).thenReturn(Optional.empty());
        when(userMapper.toEntity(testCreateDto)).thenReturn(testUser);

        assertThrows(ResourceNotFoundException.class, () -> userService.createUser(testCreateDto));
        verify(passwordEncoder).encode("password");
        verify(roleRepository).findById(999L);
        verify(userMapper).toEntity(testCreateDto);
        verifyNoMoreInteractions(userRepository, roleMapper);
    }

    @Test
    void updateUser_ShouldUpdateUser_WithNewPasswordAndRoles() {
        testCreateDto.setPassword("newpassword");
        testCreateDto.setRoleIds(Set.of(1L));

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(roleRepository.findById(1L)).thenReturn(Optional.of(testRole));
        when(passwordEncoder.encode("newpassword")).thenReturn("newEncodedPassword");
        doNothing().when(userMapper).updateUserFromDto(testCreateDto, testUser);
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(userMapper.toDto(testUser)).thenReturn(testGetDto);

        UserGetDto result = userService.updateUser(1L, testCreateDto);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("testuser", result.getUsername());
        verify(userRepository).findById(1L);
        verify(roleRepository).findById(1L);
        verify(passwordEncoder).encode("newpassword");
        verify(userMapper).updateUserFromDto(testCreateDto, testUser);
        verify(userRepository).save(any(User.class));
        verify(userMapper).toDto(testUser);
    }

    @Test
    void updateUser_ShouldUpdateUser_WithoutPasswordChange() {
        testCreateDto.setPassword(null);
        testCreateDto.setRoleIds(Set.of(1L));

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(roleRepository.findById(1L)).thenReturn(Optional.of(testRole));
        doNothing().when(userMapper).updateUserFromDto(testCreateDto, testUser);
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(userMapper.toDto(testUser)).thenReturn(testGetDto);

        UserGetDto result = userService.updateUser(1L, testCreateDto);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(userRepository).findById(1L);
        verify(roleRepository).findById(1L);
        verifyNoInteractions(passwordEncoder);
        verify(userMapper).updateUserFromDto(testCreateDto, testUser);
        verify(userRepository).save(any(User.class));
        verify(userMapper).toDto(testUser);
    }

    @Test
    void updateUser_ShouldUpdateUser_WithoutRoleChange() {
        testCreateDto.setPassword("newpassword");
        testCreateDto.setRoleIds(null);

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(passwordEncoder.encode("newpassword")).thenReturn("newEncodedPassword");
        doNothing().when(userMapper).updateUserFromDto(testCreateDto, testUser);
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(userMapper.toDto(testUser)).thenReturn(testGetDto);

        UserGetDto result = userService.updateUser(1L, testCreateDto);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(userRepository).findById(1L);
        verify(passwordEncoder).encode("newpassword");
        verify(userMapper).updateUserFromDto(testCreateDto, testUser);
        verify(userRepository).save(any(User.class));
        verify(userMapper).toDto(testUser);
        verifyNoInteractions(roleRepository);
    }

    @Test
    void updateUser_ShouldThrowException_WhenUserNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.updateUser(1L, testCreateDto));
        verify(userRepository).findById(1L);
        verifyNoMoreInteractions(userMapper, roleRepository, passwordEncoder, userRepository);
    }

    @Test
    void deleteUser_ShouldDeleteUser_WhenExists() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        doNothing().when(userRepository).delete(testUser);

        userService.deleteUser(1L);

        verify(userRepository).findById(1L);
        verify(userRepository).delete(testUser);
    }

    @Test
    void deleteUser_ShouldThrowException_WhenUserNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.deleteUser(1L));
        verify(userRepository).findById(1L);
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void createUsersBulk_ShouldCreateMultipleUsers() {
        // Arrange
        UserCreateDto dto1 = new UserCreateDto();
        dto1.setUsername("user1");
        dto1.setPassword("pass1");
        dto1.setEmail("user1@example.com");
        dto1.setRoleIds(Set.of(1L));

        UserCreateDto dto2 = new UserCreateDto();
        dto2.setUsername("user2");
        dto2.setPassword("pass2");
        dto2.setEmail("user2@example.com");
        dto2.setRoleIds(Set.of(1L));

        List<UserCreateDto> dtos = List.of(dto1, dto2);

        User user1 = new User();
        user1.setId(1L);
        user1.setUsername("user1");
        user1.setPassword("encoded1");
        user1.setEmail("user1@example.com");

        User user2 = new User();
        user2.setId(2L);
        user2.setUsername("user2");
        user2.setPassword("encoded2");
        user2.setEmail("user2@example.com");

        UserGetDto getDto1 = new UserGetDto();
        getDto1.setId(1L);
        getDto1.setUsername("user1");
        getDto1.setEmail("user1@example.com");

        UserGetDto getDto2 = new UserGetDto();
        getDto2.setId(2L);
        getDto2.setUsername("user2");
        getDto2.setEmail("user2@example.com");

        Role testRole = new Role();
        testRole.setId(1L);
        testRole.setName(RoleType.USER);

        RoleGetDto roleGetDto = new RoleGetDto();
        roleGetDto.setId(1L);
        roleGetDto.setName(RoleType.USER);

        when(userRepository.findByUsername("user1")).thenReturn(Optional.empty());
        when(userRepository.findByUsername("user2")).thenReturn(Optional.empty());

        when(roleRepository.findById(1L)).thenReturn(Optional.of(testRole));

        when(passwordEncoder.encode("pass1")).thenReturn("encoded1");
        when(passwordEncoder.encode("pass2")).thenReturn("encoded2");

        when(userMapper.toEntity(dto1)).thenReturn(user1);
        when(userMapper.toEntity(dto2)).thenReturn(user2);

        when(userRepository.saveAll(anyList())).thenReturn(List.of(user1, user2));

        when(userMapper.toDto(user1)).thenReturn(getDto1);
        when(userMapper.toDto(user2)).thenReturn(getDto2);

        when(roleMapper.toDto(testRole)).thenReturn(roleGetDto);

        List<UserGetDto> result = userService.createUsersBulk(dtos);

        assertNotNull(result);
        assertEquals(2, result.size(), "Should return 2 created users");

        verify(userRepository, times(2)).findByUsername(anyString());
        verify(userRepository, times(1)).saveAll(anyList());
        verify(userMapper, times(2)).toEntity(any(UserCreateDto.class));
        verify(userMapper, times(2)).toDto(any(User.class));
        verify(passwordEncoder, times(2)).encode(anyString());
        verify(roleRepository, times(2)).findById(1L);

    }
}