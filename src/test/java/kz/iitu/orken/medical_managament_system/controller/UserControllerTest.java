package kz.iitu.orken.medical_managament_system.controller;

import kz.iitu.orken.medical_managament_system.entity.user.Role;
import kz.iitu.orken.medical_managament_system.entity.user.User;
import kz.iitu.orken.medical_managament_system.repository.RoleRepository;
import kz.iitu.orken.medical_managament_system.repository.UserRepository;
import kz.iitu.orken.medical_managament_system.service.UserService;
import kz.iitu.orken.medical_managament_system.service.impl.ExcelService;
import kz.iitu.orken.medical_managament_system.service.impl.UserServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;

class UserControllerTest {

    private User user;
    private Role role;

    @Mock
    UserRepository userRepository;
    @Mock
    RoleRepository roleRepository;
    @Mock
    ExcelService excelService;
    @Mock
    UserService service;
    @Mock
    SecurityContext securityContext;
    @Mock
    Authentication authentication;
    @InjectMocks
    UserController userController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        service = new UserServiceImpl();
        ReflectionTestUtils.setField(service, "userRepository", userRepository);
        ReflectionTestUtils.setField(service, "roleRepository", roleRepository);
        ReflectionTestUtils.setField(service, "excelService", excelService);
        ReflectionTestUtils.setField(userController, "service", service);
        this.role = Role.builder().name("test").build();
        user = User.builder()
                .id(1L)
                .firstName("test")
                .lastName("test")
                .username("test")
                .password("test")
                .email("test")
                .roles(Collections.singleton(role))
                .build();
    }

    @Test
    void testFindAll() {
        List<User> users = List.of(user);
        when(service.findAll()).thenReturn(users);

        ResponseEntity<List<User>> result = userController.findAll();
        Assertions.assertEquals(users.size(), result.getBody().size());
        Assertions.assertEquals(users, result.getBody());
    }

    @Test
    void testFindByUsername() {
        when(service.findByUsername(any())).thenReturn(user);

        ResponseEntity<User> result = userController.findByUsername(anyString());
        Assertions.assertEquals(user, result.getBody());
        Assertions.assertEquals(user.getUsername(), result.getBody().getUsername());
    }

    @Test
    void testSave() {
        when(service.save(user)).thenReturn(user);
        when(roleRepository.findByName(anyString())).thenReturn(role);
        when(userRepository.save(user)).thenReturn(user);

        ResponseEntity<User> result = userController.save(user);
        Assertions.assertEquals(user, result.getBody());
        Assertions.assertEquals(user.getUsername(), result.getBody().getUsername());
    }

    @Test
    void testMyProfile() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication().getPrincipal())
                .thenReturn(new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(),
                        new ArrayList<>(Arrays.asList(new SimpleGrantedAuthority(role.getName())))));
        when(userRepository.findByUsername(anyString())).thenReturn(user);
        User temp = service.findByUsername(anyString());
        Assertions.assertEquals(user, temp);
        Assertions.assertNotNull(user);
        Assertions.assertEquals(user.getUsername(), temp.getUsername());
        Assertions.assertNotEquals(133L, user.getId());

        ResponseEntity<User> result = userController.myProfile();
        Assertions.assertEquals(user, result.getBody());
    }

    @Test
    void testDeleteUser() {
        userController.deleteUser(1L);
        when(userRepository.findUserById(anyLong())).thenReturn(user);
        service.delete(user);
        verify(userRepository, times(1)).delete(eq(user));
    }

    @Test
    void testDeleteUserRole() {
        User temp = User.builder()
                .roles(user.getRoles())
                .username(user.getUsername())
                .id(user.getId()).build();
        when(userRepository.save(any(User.class)))
                .thenReturn(user);

        userController.deleteUserRole(user.getId(), Collections.singletonList(role.getName()));


        service.deleteRole(user, Collections.singletonList(role.getName()));
        Assertions.assertEquals(temp.getId(), user.getId());
        Assertions.assertNotEquals(user.getRoles().size(), temp.getRoles().size());
        Assertions.assertEquals(user.getUsername(), temp.getUsername());

    }

}

//Generated with love by TestMe :) Please report issues and submit feature requests at: http://weirddev.com/forum#!/testme