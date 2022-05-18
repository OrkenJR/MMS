package kz.iitu.orken.medical_managament_system.entity;

import kz.iitu.orken.medical_managament_system.entity.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import java.text.SimpleDateFormat;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class TreatmentTest {
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");

    @Mock
    Date createdDate;
    @Mock
    Date endDate;
    @Mock
    Disease disease;
    @Mock
    User doctor;
    @Mock
    User patient;
    @InjectMocks
    Treatment treatment;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        disease.setName("test");
        doctor.setUsername("test");
        patient.setUsername("test2");
    }

    @Test
    void testSetId() {
        ReflectionTestUtils.setField(treatment, "id", 1L);
        assertTrue(treatment.getId().equals(1L));
    }

    @Test
    void testSetDescription() {
        ReflectionTestUtils.setField(treatment, "description", "test");
        assertEquals(treatment.getDescription(), "test");
    }

    @Test
    void testSetCreatedDate() {
        Date date = new Date();
        String toTest;
        ReflectionTestUtils.setField(treatment, "createdDate", date);
        assertEquals(treatment.getCreatedDate(), date);
        assertDoesNotThrow(() -> {
            dateFormat.format(treatment.getCreatedDate());
        });
    }

    @Test
    void testSetEndDate() {
        Date date = new Date();
        ReflectionTestUtils.setField(treatment, "endDate", date);
        assertEquals(treatment.getEndDate(), date);
        assertDoesNotThrow(() -> {
            dateFormat.format(treatment.getEndDate());
        });
    }

    @Test
    void testSetPrice() {
        ReflectionTestUtils.setField(treatment, "price", 1L);
        assertEquals(treatment.getPrice(), 1L);
    }

    @Test
    void testSetDisease() {
        ReflectionTestUtils.setField(treatment, "disease", disease);
        assertEquals(treatment.getDisease().getName(), disease.getName());
        assertEquals(treatment.getDisease(), disease);
        assertNotEquals(new Disease("test"), treatment.getDisease());
    }

    @Test
    void testSetDoctor() {
        ReflectionTestUtils.setField(treatment, "doctor", doctor);
        assertEquals(treatment.getDoctor(), doctor);
        assertNotEquals(treatment.getDoctor(), patient);
    }

    @Test
    void testSetPatient() {
        ReflectionTestUtils.setField(treatment, "patient", patient);
        assertEquals(treatment.getPatient(), patient);
        assertNotEquals(treatment.getPatient(), doctor);
    }

}

//Generated with love by TestMe :) Please report issues and submit feature requests at: http://weirddev.com/forum#!/testme