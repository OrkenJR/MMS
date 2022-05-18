package kz.iitu.orken.medical_managament_system.service.impl;

import kz.iitu.orken.medical_managament_system.entity.Disease;
import kz.iitu.orken.medical_managament_system.entity.Medicine;
import kz.iitu.orken.medical_managament_system.entity.Treatment;
import kz.iitu.orken.medical_managament_system.entity.user.Role;
import kz.iitu.orken.medical_managament_system.entity.user.User;
import kz.iitu.orken.medical_managament_system.repository.DiseaseRepository;
import kz.iitu.orken.medical_managament_system.repository.MedicineRepository;
import kz.iitu.orken.medical_managament_system.repository.TreatmentRepository;
import kz.iitu.orken.medical_managament_system.repository.UserRepository;
import kz.iitu.orken.medical_managament_system.service.UserService;
import org.apache.poi.ss.formula.functions.T;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.cache.CacheManager;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

import static org.mockito.Mockito.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class MedicineServiceImplTest {
    private User user;
    private User doctor;
    private Role role;
    private Role doctorRole;
    private Disease disease;
    private Medicine medicine;
    private Treatment treatment;


    @Spy
    UserRepository userRepository;
    @Mock
    DiseaseRepository diseaseRepository;
    @Mock
    MedicineRepository medicineRepository;
    @Mock
    TreatmentRepository treatmentRepository;

    @Spy @InjectMocks
    UserServiceImpl userService;
    @Mock
    CacheManager cacheManager;
    @Mock
    ExcelService excelService;
    @InjectMocks
    MedicineServiceImpl medicineService;

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

        when(medicineRepository.findAll())
                .thenReturn(list);
        List<Medicine> result = medicineService.findAllMedicine();
        Assertions.assertEquals(list, result);
        Assertions.assertFalse(result.isEmpty());
        Assertions.assertEquals(medicine, result.get(0));
        Assertions.assertEquals(1L, result.get(0).getId());
    }

    @Test
    void testFindAllTreatment() {
        List<Treatment> list = Arrays.asList(treatment);

        when(treatmentRepository.findAll())
                .thenReturn(list);
        List<Treatment> result = medicineService.findAllTreatment();
        Assertions.assertEquals(list, result);
        Assertions.assertFalse(result.isEmpty());
        Assertions.assertEquals(treatment, result.get(0));
        Assertions.assertEquals(1L, result.get(0).getId());
    }

    @Test
    void testFindAllDisease() {
        List<Disease> list = Arrays.asList(disease);

        when(diseaseRepository.findAll())
                .thenReturn(list);
        List<Disease> result = medicineService.findAllDisease();
        Assertions.assertEquals(list, result);
        Assertions.assertFalse(result.isEmpty());
        Assertions.assertEquals(disease, result.get(0));
    }

    @Test
    void testFindAllTreatmentByUser() {
        List<Treatment> list = Arrays.asList(treatment);

        when(treatmentRepository.findAll())
                .thenReturn(list);
        List<Treatment> result = medicineService.findAllTreatmentByUser(user);
        Assertions.assertEquals(list, result);
        Assertions.assertFalse(result.isEmpty());
        Assertions.assertEquals(treatment, result.get(0));
        Assertions.assertEquals(1L, result.get(0).getId());

        when(treatmentRepository.findAll())
                .thenReturn(list);
        result = medicineService.findAllTreatmentByUser(doctor);
        Assertions.assertTrue(result.isEmpty());
    }

    @Test
    void testFindAllTreatmentByDisease() {
        List<Treatment> list = List.of(treatment);

        when(diseaseRepository.findDiseaseByName(anyString())).thenReturn(disease);
        when(treatmentRepository.findAll())
                .thenReturn(list);
        List<Treatment> result = medicineService.findAllTreatmentByDisease(anyString());
        Assertions.assertEquals(list, result);
        Assertions.assertFalse(result.isEmpty());
        Assertions.assertEquals(treatment, result.get(0));
        Assertions.assertEquals(1L, result.get(0).getId());
        Assertions.assertEquals(treatment.getDisease().getName(), disease.getName());

        when(diseaseRepository.findDiseaseByName(anyString())).thenReturn(new Disease("asdf"));
        result = medicineService.findAllTreatmentByUser(doctor);
        Assertions.assertTrue(result.isEmpty());
    }

    @Test
    void testFindDiseaseByName() {
        when(diseaseRepository.findDiseaseByName(anyString())).thenReturn(disease);
        Disease result = medicineService.findDiseaseByName(anyString());

        Assertions.assertEquals(disease, result);
        Assertions.assertNotEquals("testdf", disease.getName());
        Assertions.assertEquals(disease.getName(), result.getName());
    }

    @Test
    void testFindMedicineByName() {
        when(medicineRepository.findMedicineByName(anyString())).thenReturn(medicine);

        Medicine result = medicineService.findMedicineByName(anyString());
        Assertions.assertEquals(medicine, result);
        Assertions.assertNotEquals("testdf", medicine.getName());
        Assertions.assertEquals(medicine.getName(), result.getName());
    }

    @Test
    void testSetTreatment() {
        List<User> list = Arrays.asList(doctor);

        when(userRepository.findAll())
                .thenReturn(list);
        when(diseaseRepository.findDiseaseByName(anyString())).thenReturn(disease);
        when(userService.randomDoctor()).thenReturn(doctor);

        Treatment result = medicineService.setTreatment(user, new Disease("test2"));

        Assertions.assertEquals(user, result.getPatient());
        Assertions.assertEquals(doctor, treatment.getDoctor());
        Assertions.assertNotEquals(user.getUsername(), treatment.getDoctor().getUsername());
    }

    @Test
    void testUpdateTreatment() {
        when(treatmentRepository.findTreatmentById(anyLong())).thenReturn(treatment);

        Treatment result = medicineService.updateTreatment("testss", 100L, treatment.getId());

        Assertions.assertEquals(100L, result.getPrice());
        Assertions.assertNotEquals("test", result.getDescription());

    }



    @Test
    void testSaveDisease() {
        when(diseaseRepository.save(any())).thenReturn(disease);
        Disease result = medicineService.saveDisease(disease);
        Assertions.assertEquals(disease, result);
    }

    @Test
    void testSaveMedicine() {
        when(medicineRepository.save(any())).thenReturn(medicine);
        Medicine result = medicineService.saveMedicine(medicine);
        Assertions.assertEquals(medicine, result);
    }

    @Test
    void testDeleteDisease() {
        when(diseaseRepository.findDiseaseById(anyLong())).thenReturn(disease);
        medicineService.deleteDisease(anyLong());
        verify(diseaseRepository, times(1)).delete(eq(disease));
    }

    @Test
    void testDeleteMedicine() {
        when(medicineRepository.findMedicineById(anyLong())).thenReturn(medicine);
        medicineService.deleteMedicine(anyLong());
        verify(medicineRepository, times(1)).delete(eq(medicine));
    }

    @Test
    void testIsTreatmentFinished() {
        treatment.setEndDate(Date
                .from(LocalDateTime.now().minusDays(1).atZone(ZoneId.systemDefault())
                        .toInstant()));
        boolean result = medicineService.isTreatmentFinished(treatment);
        Assertions.assertTrue(result);

        treatment.setEndDate(Date
                .from(LocalDateTime.now().plusDays(1).atZone(ZoneId.systemDefault())
                        .toInstant()));
        result = medicineService.isTreatmentFinished(treatment);
        Assertions.assertFalse(result);
    }

    @Test
    void testExportTreatment() throws IOException {
        List<Treatment> list = List.of(treatment);

        when(treatmentRepository.findAll())
                .thenReturn(list);
        when(excelService.exportTreatment(any())).thenReturn(new ByteArrayOutputStream());
        ByteArrayResource result = medicineService.exportTreatment();
        Assertions.assertEquals(0, result.getByteArray().length);
    }

    @Test
    void testExportMedicine() throws IOException {
        List<Medicine> list = List.of(medicine);

        when(medicineRepository.findAll())
                .thenReturn(list);
        when(excelService.exportMedicine(any())).thenReturn(new ByteArrayOutputStream());
        ByteArrayResource result = medicineService.exportMedicine();
        Assertions.assertEquals(0, result.getByteArray().length);
    }

    @Test
    void testExportDisease() throws IOException {
        List<Disease> list = List.of(disease);

        when(diseaseRepository.findAll())
                .thenReturn(list);
        when(excelService.exportDisease(any())).thenReturn(new ByteArrayOutputStream());
        ByteArrayResource result = medicineService.exportDisease();
        Assertions.assertEquals(0, result.getByteArray().length);
    }

    @Test
    void testFindUsersByDisease() {
        user.setTreatmentsAsPatient(List.of(treatment));
        List<User> list = List.of(user);
        when(userService.findAll()).thenReturn(list);
        List<User> result = medicineService.findUsersByDisease(disease);

        Assertions.assertEquals(list.size(), result.size());
        Assertions.assertEquals(list.get(0).getUsername(), result.get(0).getUsername());

        result = medicineService.findUsersByDisease(new Disease("test"));

        Assertions.assertNotEquals(list.size(), result.size());
    }

}

//Generated with love by TestMe :) Please report issues and submit feature requests at: http://weirddev.com/forum#!/testme