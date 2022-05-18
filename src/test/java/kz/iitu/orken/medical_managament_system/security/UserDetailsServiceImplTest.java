package kz.iitu.orken.medical_managament_system.security;

import kz.iitu.orken.medical_managament_system.entity.user.Role;
import kz.iitu.orken.medical_managament_system.entity.user.User;
import kz.iitu.orken.medical_managament_system.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collections;

import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.when;

class UserDetailsServiceImplTest {
    private User user;
    private Role role;
    @Mock
    UserRepository repository;
    @InjectMocks
    UserDetailsServiceImpl userDetailsServiceImpl;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
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
    void testLoadUserByUsername() {
        when(repository.findByUsername(anyString())).thenReturn(user);

        UserDetails result = userDetailsServiceImpl.loadUserByUsername("username");
        Assertions.assertEquals(user.getUsername(), result.getUsername());
        Assertions.assertEquals(user.getPassword(), result.getPassword());
        Assertions.assertEquals(user.getRoles().size(), result.getAuthorities().size());
    }
}

//Generated with love by TestMe :) Please report issues and submit feature requests at: http://weirddev.com/forum#!/testme