package kz.iitu.orken.medical_managament_system.entity;

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

class DiseaseTest {

    private Medicine medicine;
    @Mock
    Set<Medicine> medicines;
    @InjectMocks
    Disease disease;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        this.medicine = new Medicine("test", 1L);
    }

    @Test
    void testSetId() {
        ReflectionTestUtils.setField(disease, "id", 1L);
        assertTrue(disease.getId().equals(1L));
    }

    @Test
    void testSetName() {
        ReflectionTestUtils.setField(disease, "name", "test");
        assertEquals(disease.getName(), "test");
    }

    @Test
    void testSetMedicines() {
        Set<Medicine> set = Collections.singleton(medicine);
        ReflectionTestUtils.setField(disease, "medicines", Collections.singleton(medicine));
        assertEquals(disease.getMedicines(), set);
        assertNotEquals(disease.getMedicines().size(), 10);
    }

    @Test
    void testToString() {
        String result = disease.toString();
        Assertions.assertNotNull(result);
    }
}

//Generated with love by TestMe :) Please report issues and submit feature requests at: http://weirddev.com/forum#!/testme