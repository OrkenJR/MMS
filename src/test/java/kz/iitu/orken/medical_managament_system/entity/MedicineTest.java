package kz.iitu.orken.medical_managament_system.entity;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class MedicineTest {
    private Disease disease;
    @Mock
    Set<Disease> diseases;
    @InjectMocks
    Medicine medicine;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        this.disease = new Disease("test");
    }

    @Test
    void testSetId() {
        ReflectionTestUtils.setField(medicine, "id", 1L);
        assertTrue(medicine.getId().equals(1L));
    }

    @Test
    void testSetName() {
        ReflectionTestUtils.setField(medicine, "name", "test");
        assertEquals(medicine.getName(), "test");
    }

    @Test
    void testSetPrice() {
        ReflectionTestUtils.setField(medicine, "price", 1L);
        assertEquals(1L, medicine.getPrice());
    }

    @Test
    void testSetDiseases() {
        Set<Disease> diseases = Collections.singleton(disease);
        ReflectionTestUtils.setField(medicine, "diseases", diseases);
        assertEquals(diseases.size(), medicine.getDiseases().size());
        assertEquals(diseases, medicine.getDiseases());
    }

    @Test
    void testToString() {
        String result = medicine.toString();
        Assertions.assertNotNull(result);
    }
}

//Generated with love by TestMe :) Please report issues and submit feature requests at: http://weirddev.com/forum#!/testme