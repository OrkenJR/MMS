package kz.iitu.orken.medical_managament_system.service.impl;

import kz.iitu.orken.medical_managament_system.Exception.NotAllowedException;
import kz.iitu.orken.medical_managament_system.Exception.NotFoundException;
import kz.iitu.orken.medical_managament_system.Exception.TransactionException;
import kz.iitu.orken.medical_managament_system.aop.LogAnnotation;
import kz.iitu.orken.medical_managament_system.entity.Disease;
import kz.iitu.orken.medical_managament_system.entity.Medicine;
import kz.iitu.orken.medical_managament_system.entity.Treatment;
import kz.iitu.orken.medical_managament_system.entity.user.User;
import kz.iitu.orken.medical_managament_system.repository.DiseaseRepository;
import kz.iitu.orken.medical_managament_system.repository.MedicineRepository;
import kz.iitu.orken.medical_managament_system.repository.TreatmentRepository;
import kz.iitu.orken.medical_managament_system.service.MedicineService;
import kz.iitu.orken.medical_managament_system.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class MedicineServiceImpl implements MedicineService {

    private DiseaseRepository diseaseRepository;
    private MedicineRepository medicineRepository;
    private TreatmentRepository treatmentRepository;
    private UserService userService;
    private CacheManager cacheManager;
    private ExcelService excelService;

    @Value("#{new Long('${scheduling.initial-delay-rate}') * 10}")
    private Long delayRate;

    @Autowired
    public MedicineServiceImpl(DiseaseRepository diseaseRepository, MedicineRepository medicineRepository,
                               TreatmentRepository treatmentRepository, UserService userService,
                               CacheManager cacheManager, ExcelService excelService) {
        this.diseaseRepository = diseaseRepository;
        this.medicineRepository = medicineRepository;
        this.treatmentRepository = treatmentRepository;
        this.userService = userService;
        this.cacheManager = cacheManager;
        this.excelService = excelService;
    }

    @Override
    @LogAnnotation
    @Cacheable("all-medicine")
    @Transactional(propagation = Propagation.REQUIRED, readOnly = true, isolation = Isolation.READ_COMMITTED,
            rollbackFor = TransactionException.class,
            noRollbackForClassName = {"NullpointerException", "NotAllowedException", "RuntimeException"})
    public List<Medicine> findAllMedicine() {
        return medicineRepository.findAll();
    }

    @Override
    @Cacheable("all-treatment")
    @Transactional(propagation = Propagation.REQUIRED, readOnly = true, isolation = Isolation.READ_COMMITTED,
            rollbackFor = TransactionException.class,
            noRollbackForClassName = {"NullpointerException", "NotAllowedException", "RuntimeException"})
    public List<Treatment> findAllTreatment() {
        return treatmentRepository.findAll();
    }

    @Override
    public List<Disease> findAllDisease() {
        return diseaseRepository.findAll();
    }

    @Override
    @Cacheable(value = "treatment-by-user", key = "#user.username + '::treatment'")
    @Transactional(propagation = Propagation.NEVER, readOnly = true, isolation = Isolation.REPEATABLE_READ,
            rollbackFor = TransactionException.class,
            noRollbackForClassName = {"NullpointerException", "NotAllowedException"})
    public List<Treatment> findAllTreatmentByUser(User user) {
        return treatmentRepository.findAll()
                .stream()
                .filter(x -> x.getPatient().getUsername().equals(user.getUsername()))
                .collect(Collectors.toList());
    }


    @Cacheable(value = "treatment-by-disease", key = "#disease.id + '::disease'")
    @Transactional(propagation = Propagation.NEVER, readOnly = true, isolation = Isolation.REPEATABLE_READ)
    public List<Treatment> findAllTreatmentByDisease(Disease disease) {
        return treatmentRepository.findAll()
                .stream()
                .filter(x -> x.getDisease().getId().equals(disease.getId()))
                .collect(Collectors.toList());
    }

    @Override
    public Disease findDiseaseByName(String name) {
        return diseaseRepository.findDiseaseByName(name);
    }

    @Override
    public Medicine findMedicineByName(String name) {
        return medicineRepository.findMedicineByName(name);
    }

    @Override
    public List<Treatment> findAllTreatmentByDisease(String name) {

        Disease disease = diseaseRepository.findDiseaseByName(name);
        if (disease != null) {
            return findAllTreatmentByDisease(disease);
        }
        return new ArrayList<>();
    }

    @Override
    public Treatment setTreatment(User patient, Disease disease) {
        Disease disease1 = findDiseaseByName(disease.getName());
        if (disease1 == null) {
            disease = saveDisease(disease);
        }
        return Treatment
                .builder()
                .patient(patient)
                .disease(disease)
                .description(disease.getName())
                .createdDate(new Date())
                .doctor(userService.randomDoctor())
                .build();
    }

    @Override
    public Treatment updateTreatment(String description, Long price, Long id) {
        Treatment treatment = treatmentRepository.findTreatmentById(id);
        if (treatment == null) {
            throw new NotFoundException("Could not find treatment with id: " + id);
        }

        treatment.setDescription(description);
        treatment.setPrice(price);
        treatment.setEndDate(new Date());
        return treatment;
    }

    @Override
    @Transactional(propagation = Propagation.NEVER, readOnly = true, isolation = Isolation.SERIALIZABLE)
    public void buyMedicine(String medicineName) {

        Medicine medicine = findMedicineByName(medicineName);
        if (medicine == null) {
            throw new NotFoundException("Could not found medicine by name: " + medicineName);
        }

        User user = userService.getCurrentUser();
        int size = (int) user.getTreatmentsAsPatient().stream().filter(x -> x.getDisease().getMedicines().contains(medicine)).count();
        if (size == 0) {
            throw new NotAllowedException(String.format("Medicine %s is not allowed for %s", medicine.getName(), user.getUsername()));
        }

    }

    @Override
    @CacheEvict(value = "treatment-by-disease", key = "#disease.id + '::disease'")
    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true, isolation = Isolation.SERIALIZABLE)
    public Disease saveDisease(Disease disease) {
        return diseaseRepository.save(disease);
    }

    @Override
    @CacheEvict(value = "all-medicine")
    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true, isolation = Isolation.SERIALIZABLE)
    public Medicine saveMedicine(Medicine medicine) {
        diseaseRepository.saveAll(medicine.getDiseases());
        return medicineRepository.save(medicine);
    }

    @Override
    @CacheEvict(value = "treatment-by-disease")
    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true, isolation = Isolation.SERIALIZABLE)
    public void deleteDisease(Long diseaseId) {
        Disease disease = diseaseRepository.findDiseaseById(diseaseId);
        Optional.ofNullable(disease).ifPresent(diseaseRepository::delete);
    }

    @Override
    @CacheEvict(value = "all-medicine")
    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true, isolation = Isolation.SERIALIZABLE)
    public void deleteMedicine(Long medicineId) {
        Medicine medicine = medicineRepository.findMedicineById(medicineId);
        Optional.ofNullable(medicine).ifPresent(medicineRepository::delete);
    }

    @Override
    public boolean isTreatmentFinished(Treatment treatment) {
        return treatment.getEndDate().before(new Date());
    }

    @Override
    public ByteArrayResource exportTreatment() {
        ByteArrayOutputStream stream;
        try {
            stream = excelService.exportTreatment(findAllTreatment());
        } catch (Exception e) {
            stream = new ByteArrayOutputStream();
        }
        return new ByteArrayResource(stream.toByteArray());
    }

    @Override
    public ByteArrayResource exportMedicine() {
        ByteArrayOutputStream stream;
        try {
            stream = excelService.exportMedicine(findAllMedicine());
        } catch (Exception e) {
            stream = new ByteArrayOutputStream();
        }
        return new ByteArrayResource(stream.toByteArray());
    }

    @Override
    public ByteArrayResource exportDisease() {
        ByteArrayOutputStream stream;
        try {
            stream = excelService.exportDisease(findAllDisease());
        } catch (Exception e) {
            stream = new ByteArrayOutputStream();
        }
        return new ByteArrayResource(stream.toByteArray());
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, readOnly = true, isolation = Isolation.SERIALIZABLE)
    public List<User> findUsersByDisease(Disease disease) {
        List<User> allUsers = userService.findAll();
        return allUsers.stream()
                .filter(x -> x.getTreatmentsAsPatient()
                        .stream().anyMatch(e -> e.getDisease().getId().equals(disease.getId())))
                .collect(Collectors.toList());
    }

    @Scheduled(fixedDelay = 60000 * 60 * 12, initialDelay = 60000 * 60 * 12) // every 12 hours
    public void evictAllCaches() {
        List<String> caches = Arrays.asList("all-medicine", "treatment-by-disease",
                "treatment-by-user", "all-treatment", "all-medicine");
        caches.forEach(cacheName -> {
            Objects.requireNonNull(cacheManager.getCache(cacheName)).clear();
        });
    }

}
