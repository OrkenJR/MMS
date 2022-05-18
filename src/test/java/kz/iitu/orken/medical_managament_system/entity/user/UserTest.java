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

class UserTest {

    private Role role;
    private Treatment treatment;

    @Mock
    Set<Role> roles;
    @Mock
    List<Treatment> treatmentsAsPatient;
    @Mock
    List<Treatment> treatmentsAsDoctor;
    @InjectMocks
    User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        this.role = Role.builder().name("test").build();
        this.treatment = Treatment.builder().price(1L).description("test").build();
    }

    @Test
    void testSetId() {
        ReflectionTestUtils.setField(user, "id", 1L);
        assertTrue(user.getId().equals(1L));
    }

    @Test
    void testSetEmail() {
        ReflectionTestUtils.setField(user, "email", "test");
        assertEquals(user.getEmail(), "test");
    }

    @Test
    void testSetFirstName() {
        ReflectionTestUtils.setField(user, "firstName", "test");
        assertEquals(user.getFirstName(), "test");
    }

    @Test
    void testSetLastName() {
        ReflectionTestUtils.setField(user, "lastName", "test");
        assertEquals(user.getLastName(), "test");
    }

    @Test
    void testSetUsername() {
        ReflectionTestUtils.setField(user, "username", "test");
        assertEquals(user.getUsername(), "test");
    }

    @Test
    void testSetPassword() {
        ReflectionTestUtils.setField(user, "password", "test");
        assertEquals(user.getPassword(), "test");
    }

    @Test
    void testSetRoles() {
        ReflectionTestUtils.setField(user, "roles", Collections.singleton(role));
        assertEquals(user.getRoles(), Collections.singleton(role));
    }

    @Test
    void testSetTreatmentsAsPatient() {
        ReflectionTestUtils.setField(user, "treatmentsAsPatient", Collections.singletonList(treatment));
        assertEquals(user.getTreatmentsAsPatient(), Collections.singletonList(treatment));
    }

    @Test
    void testSetTreatmentsAsDoctor() {
        ReflectionTestUtils.setField(user, "treatmentsAsDoctor", Collections.singletonList(treatment));
        assertEquals(user.getTreatmentsAsDoctor(), Collections.singletonList(treatment));
    }

    @Test
    void testToString() {
        String result = user.toString();
        Assertions.assertNotNull(result);
    }

}

//Generated with love by TestMe :) Please report issues and submit feature requests at: http://weirddev.com/forum#!/testme