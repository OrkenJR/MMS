package kz.iitu.orken.medical_managament_system.service.impl;

import kz.iitu.orken.medical_managament_system.entity.user.Role;
import kz.iitu.orken.medical_managament_system.entity.user.User;
import kz.iitu.orken.medical_managament_system.repository.RoleRepository;
import kz.iitu.orken.medical_managament_system.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.cache.CacheManager;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;

import static org.mockito.Mockito.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserServiceImplTest {

    private User user;
    private User doctor;
    private Role role;
    private Role doctorRole;

    @Mock
    UserRepository userRepository;
    @Mock
    RoleRepository roleRepository;
    @Mock
    ExcelService excelService;
    @Mock
    CacheManager cacheManager;
    @Mock
    SecurityContext securityContext;
    @Mock
    Authentication authentication;
    @InjectMocks
    UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        ReflectionTestUtils.setField(userService, "userRepository", userRepository);
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
        this.doctorRole = Role.builder().name(Role.DOCTOR_ROLE).build();
        doctor = User.builder()
                .username("doctor")
                .roles(Collections.singleton(doctorRole))
                .build();
    }

    @Test
    void testFindAll() {
        List<User> list = Arrays.asList(user);

        when(userRepository.findAll())
                .thenReturn(list);
        List<User> result = userService.findAll();
        Assertions.assertEquals(list, result);
        Assertions.assertFalse(result.isEmpty());
        Assertions.assertEquals(user, result.get(0));
        Assertions.assertEquals(1L, result.get(0).getId());
    }

    @Test
    void testFindByUsername() {
        when(userRepository.findByUsername(anyString())).thenReturn(user);
        User temp = userService.findByUsername(anyString());

        Assertions.assertEquals(user, temp);
        Assertions.assertNotNull(user);
        Assertions.assertEquals(user.getUsername(), temp.getUsername());
        Assertions.assertNotEquals(133L, user.getId());
    }

    @Test
    void testFindById() {
        when(userRepository.findUserById(anyLong())).thenReturn(user);
        User temp = userService.findById(anyLong());

        Assertions.assertEquals(user, temp);
        Assertions.assertNotNull(user);
        Assertions.assertEquals(user.getUsername(), temp.getUsername());
        Assertions.assertNotEquals(133L, user.getId());
    }

    @Test
    void testSave() {
        when(userRepository.save(any(User.class)))
                .thenReturn(user);
        User result = userService.save(user);
        Assertions.assertEquals(user, result);
        Assertions.assertEquals(user.getId(), result.getId());
        Assertions.assertEquals(user.getUsername(), result.getUsername());
    }

    @Test
    void testRandomDoctor() {
        List<User> list = Arrays.asList(user, doctor);

        when(userRepository.findAll())
                .thenReturn(list);
        User result = userService.randomDoctor();
        Assertions.assertEquals(doctor, result);
        Assertions.assertEquals(doctor.getId(), result.getId());
        Assertions.assertEquals(doctor.getUsername(), result.getUsername());
    }

    @Test
    void testGetCurrentUser() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication().getPrincipal())
                .thenReturn(new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(),
                        new ArrayList<>(Arrays.asList(new SimpleGrantedAuthority(role.getName())))));
        when(userRepository.findByUsername(anyString())).thenReturn(user);
        User temp = userService.findByUsername(anyString());
        Assertions.assertEquals(user, temp);
        Assertions.assertNotNull(user);
        Assertions.assertEquals(user.getUsername(), temp.getUsername());
        Assertions.assertNotEquals(133L, user.getId());
    }

    @Test
    void testDelete() {
        userService.delete(user);
        verify(userRepository, times(1)).delete(eq(user));
    }

    @Test
    void testDeleteById() {
        when(userRepository.findUserById(anyLong())).thenReturn(user);
        userService.deleteById(anyLong());
        verify(userRepository, times(1)).delete(eq(user));
    }

    @Test
    void testDeleteRole() {
        User temp = User.builder()
                .roles(user.getRoles())
                .username(user.getUsername())
                .id(user.getId()).build();
        when(userRepository.save(any(User.class)))
                .thenReturn(user);

        userService.deleteRole(user, Collections.singletonList(role.getName()));
        Assertions.assertEquals(temp.getId(), user.getId());
        Assertions.assertNotEquals(user.getRoles().size(), temp.getRoles().size());
        Assertions.assertEquals(user.getUsername(), temp.getUsername());

    }

    @Test
    void testReport() {
        userService.report("message");
    }

    @Test
    void testExport() throws IOException {
        List<User> list = List.of(user);

        when(userRepository.findAll())
                .thenReturn(list);
        when(excelService.exportUsers(any())).thenReturn(new ByteArrayOutputStream());
        ByteArrayResource result = userService.export();
        Assertions.assertEquals(0, result.getByteArray().length);
    }

    @Test
    void testContainsName() {
        boolean result = userService.containsName(Set.of(role), Role.DOCTOR_ROLE);
        Assertions.assertFalse(result);
        result = userService.containsName(Set.of(doctorRole), Role.DOCTOR_ROLE);
        Assertions.assertTrue(result);
    }

}

//Generated with love by TestMe :) Please report issues and submit feature requests at: http://weirddev.com/forum#!/testme