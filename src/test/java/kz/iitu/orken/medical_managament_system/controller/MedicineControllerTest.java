package kz.iitu.orken.medical_managament_system.controller;

import kz.iitu.orken.medical_managament_system.entity.Disease;
import kz.iitu.orken.medical_managament_system.entity.Medicine;
import kz.iitu.orken.medical_managament_system.entity.Treatment;
import kz.iitu.orken.medical_managament_system.entity.user.Role;
import kz.iitu.orken.medical_managament_system.entity.user.User;
import kz.iitu.orken.medical_managament_system.repository.DiseaseRepository;
import kz.iitu.orken.medical_managament_system.repository.MedicineRepository;
import kz.iitu.orken.medical_managament_system.repository.TreatmentRepository;
import kz.iitu.orken.medical_managament_system.repository.UserRepository;
import kz.iitu.orken.medical_managament_system.service.impl.ExcelService;
import kz.iitu.orken.medical_managament_system.service.impl.MedicineServiceImpl;
import kz.iitu.orken.medical_managament_system.service.impl.UserServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.cache.CacheManager;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.mockito.Mockito.*;

class MedicineControllerTest {
    private User user;
    private User doctor;
    private Role role;
    private Role doctorRole;
    private Disease disease;
    private Medicine medicine;
    private Treatment treatment;


    @Mock
    UserRepository userRepository;
    @Mock
    DiseaseRepository diseaseRepository;
    @Mock
    MedicineRepository medicineRepository;
    @Mock
    TreatmentRepository treatmentRepository;

    @Mock
    UserServiceImpl userService;
    @Mock
    CacheManager cacheManager;
    @Mock
    ExcelService excelService;
    @Mock
    MedicineServiceImpl medicineService;
    @InjectMocks
    MedicineController medicineController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        ReflectionTestUtils.setField(medicineService, "userService", userService);
        ReflectionTestUtils.setField(userService, "userRepository", userRepository);
        ReflectionTestUtils.setField(medicineService, "diseaseRepository", diseaseRepository);
        ReflectionTestUtils.setField(medicineService, "medicineRepository", medicineRepository);
        ReflectionTestUtils.setField(medicineService, "treatmentRepository", treatmentRepository);
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
        disease = new Disease("test");
        disease.setId(1L);
        medicine = Medicine.builder().id(1L).name("test").price(1L).diseases(Set.of(disease)).build();
        treatment = Treatment.builder().id(1L).description("test").doctor(doctor).patient(user).disease(disease).price(1L).build();
    }

    @Test
    void testFindAllMedicine() {
        List<Medicine> list = Arrays.asList(medicine);

        when(medicineService.findAllMedicine())
                .thenReturn(list);
        ResponseEntity<List<Medicine>> result = medicineController.findAllMedicine();
        Assertions.assertEquals(list, result.getBody());
        Assertions.assertFalse(result.getBody().isEmpty());
        Assertions.assertEquals(medicine, result.getBody().get(0));
        Assertions.assertEquals(1L, result.getBody().get(0).getId());
    }

    @Test
    void testFindAllTreatment() {
        List<Treatment> list = Arrays.asList(treatment);

        when(medicineService.findAllTreatment())
                .thenReturn(list);
        ResponseEntity<List<Treatment>> result = medicineController.findAllTreatment();
        Assertions.assertEquals(list, result.getBody());
        Assertions.assertFalse(result.getBody().isEmpty());
        Assertions.assertEquals(treatment, result.getBody().get(0));
        Assertions.assertEquals(1L, result.getBody().get(0).getId());
    }

    @Test
    void testFindAllDisease() {
        List<Disease> list = Arrays.asList(disease);

        when(medicineService.findAllDisease())
                .thenReturn(list);
        ResponseEntity<List<Disease>> result = medicineController.findAllDisease();
        Assertions.assertEquals(list, result.getBody());
        Assertions.assertFalse(result.getBody().isEmpty());
        Assertions.assertEquals(disease, result.getBody().get(0));
        Assertions.assertEquals(1L, result.getBody().get(0).getId());
    }

    @Test
    void testMyTreatments() {
        List<Treatment> list = List.of(treatment);

        when(medicineService.findAllTreatmentByUser(any()))
                .thenReturn(list);
        ResponseEntity<List<Treatment>> result = medicineController.myTreatments();
        Assertions.assertEquals(list, result.getBody());
        Assertions.assertFalse(result.getBody().isEmpty());
        Assertions.assertEquals(treatment, result.getBody().get(0));
        Assertions.assertEquals(1L, result.getBody().get(0).getId());
    }

    @Test
    void testFindTreatmentByDisease() {
        List<Treatment> list = List.of(treatment);

        when(medicineService.findAllTreatmentByDisease(any(Disease.class)))
                .thenReturn(list);
        when(medicineService.findAllTreatmentByDisease(anyString()))
                .thenReturn(list);
        ResponseEntity<List<Treatment>> result = medicineController.findTreatmentByDisease(anyString());
        Assertions.assertEquals(list, result.getBody());
        Assertions.assertFalse(result.getBody().isEmpty());
        Assertions.assertEquals(treatment, result.getBody().get(0));
        Assertions.assertEquals(1L, result.getBody().get(0).getId());
    }

    @Test
    void testSaveDisease() {
        when(medicineService.saveDisease(disease)).thenReturn(disease);
        when(diseaseRepository.save(disease)).thenReturn(disease);

        ResponseEntity<Disease> result = medicineController.saveDisease(disease);
        Assertions.assertEquals(disease, result.getBody());
        Assertions.assertEquals(disease.getName(), result.getBody().getName());
    }

    @Test
    void testSaveMedicine() {
        when(medicineService.saveMedicine(medicine)).thenReturn(medicine);
        when(medicineRepository.save(medicine)).thenReturn(medicine);

        ResponseEntity<Medicine> result = medicineController.saveMedicine(medicine);
        Assertions.assertEquals(medicine, result.getBody());
        Assertions.assertEquals(medicine.getName(), result.getBody().getName());
    }

    @Test
    void testDeleteMedicine() {
        medicineController.deleteMedicine(1L);
        when(medicineRepository.findMedicineById(anyLong())).thenReturn(medicine);
        medicineRepository.delete(medicine);
        medicineService.deleteMedicine(1L);
        verify(medicineRepository, times(1)).delete(eq(medicine));
    }

    @Test
    void testDeleteDisease() {
        medicineController.deleteDisease(1L);
        when(diseaseRepository.findDiseaseById(anyLong())).thenReturn(disease);
        diseaseRepository.delete(disease);
        medicineService.deleteDisease(1L);
        verify(diseaseRepository, times(1)).delete(eq(disease));
    }

}

//Generated with love by TestMe :) Please report issues and submit feature requests at: http://weirddev.com/forum#!/testme