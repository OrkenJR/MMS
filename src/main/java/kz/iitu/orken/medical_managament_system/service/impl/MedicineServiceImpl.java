package kz.iitu.orken.medical_managament_system.service.impl;

import kz.iitu.orken.medical_managament_system.Exception.NotAllowedException;
import kz.iitu.orken.medical_managament_system.Exception.TransactionException;
import kz.iitu.orken.medical_managament_system.aop.LogAnnotation;
import kz.iitu.orken.medical_managament_system.entity.Disease;
import kz.iitu.orken.medical_managament_system.entity.Medicine;
import kz.iitu.orken.medical_managament_system.entity.Treatment;
import kz.iitu.orken.medical_managament_system.entity.user.User;
import kz.iitu.orken.medical_managament_system.repository.DiseaseRepository;
import kz.iitu.orken.medical_managament_system.repository.MedicineRepository;
import kz.iitu.orken.medical_managament_system.repository.TreatmentRepository;
import kz.iitu.orken.medical_managament_system.repository.UserRepository;
import kz.iitu.orken.medical_managament_system.service.MedicineService;
import kz.iitu.orken.medical_managament_system.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


public class MedicineServiceImpl implements MedicineService {

    private DiseaseRepository diseaseRepository;
    private MedicineRepository medicineRepository;
    private TreatmentRepository treatmentRepository;
    private UserService userService;
    private CacheManager cacheManager;

    @Value("#{new Long('${scheduling.initial-delay-rate}') * 10}")
    private Long delayRate;

    @Autowired
    public MedicineServiceImpl(DiseaseRepository diseaseRepository, MedicineRepository medicineRepository,
                               TreatmentRepository treatmentRepository, UserService userService,
                               CacheManager cacheManager) {
        this.diseaseRepository = diseaseRepository;
        this.medicineRepository = medicineRepository;
        this.treatmentRepository = treatmentRepository;
        this.userService = userService;
        this.cacheManager = cacheManager;
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

    @Override
    @Cacheable(value = "treatment-by-disease", key = "#disease.id + '::disease'")
    @Transactional(propagation = Propagation.NEVER, readOnly = true, isolation = Isolation.REPEATABLE_READ)
    public List<Treatment> findAllTreatmentByDisease(Disease disease) {
        return treatmentRepository.findAll()
                .stream()
                .filter(x -> x.getDisease().getId().equals(disease.getId()))
                .collect(Collectors.toList());
    }

    @Override
    public Treatment setTreatment(User patient, Disease disease) {
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
    @Transactional(propagation = Propagation.NEVER, readOnly = true, isolation = Isolation.SERIALIZABLE)
    public void buyMedicine(Medicine medicine) {
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
        return medicineRepository.save(medicine);
    }

    @Override
    @CacheEvict(value = "treatment-by-disease", key = "#disease.id + '::disease'")
    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true, isolation = Isolation.SERIALIZABLE)
    public void delete(Disease disease) {
        diseaseRepository.delete(disease);
    }

    @Override
    @CacheEvict(value = "all-medicine")
    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true, isolation = Isolation.SERIALIZABLE)
    public void delete(Medicine medicine) {
        medicineRepository.delete(medicine);
    }

    @Override
    public boolean isTreatmentFinished(Treatment treatment) {
        return treatment.getEndDate().before(new Date());
    }

    @Override
    public byte[] exportTreatment() {
        return new byte[0];
    }

    @Override
    public byte[] exportMedicine() {
        return new byte[0];
    }

    @Override
    public byte[] exportDisease() {
        return new byte[0];
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
