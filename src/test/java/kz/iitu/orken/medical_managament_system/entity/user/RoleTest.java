package kz.iitu.orken.medical_managament_system.entity.user;

import kz.iitu.orken.medical_managament_system.entity.Disease;
import kz.iitu.orken.medical_managament_system.entity.Treatment;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RoleTest {
    private User user;
    @Mock
    Set<User> users;
    @InjectMocks
    Role role;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        this.user = User.builder().username("username").firstName("test").build();
    }

    @Test
    void testSetId() {
        ReflectionTestUtils.setField(role, "id", 1L);
        assertEquals(1L, (long) role.getId());
    }

    @Test
    void testSetName() {
        ReflectionTestUtils.setField(role, "name", "test");
        assertEquals("test", role.getName());
    }

    @Test
    void testSetUsers() {
        Set<User> set = Collections.singleton(user);
        ReflectionTestUtils.setField(role, "users", Collections.singleton(user));
        assertEquals(role.getUsers(), set);
        assertEquals(role.getUsers().size(), set.size());
        assertNotEquals(role.getUsers().stream().findFirst().get().getUsername(), "adfd");
    }

    @Test
    void testToString() {
        String result = role.toString();
        Assertions.assertNotNull(result);
        assertDoesNotThrow(() -> {role.toString();});
    }

}

//Generated with love by TestMe :) Please report issues and submit feature requests at: http://weirddev.com/forum#!/testme