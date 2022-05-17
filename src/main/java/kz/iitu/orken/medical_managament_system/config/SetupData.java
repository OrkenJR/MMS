package kz.iitu.orken.medical_managament_system.config;

import kz.iitu.orken.medical_managament_system.entity.Disease;
import kz.iitu.orken.medical_managament_system.entity.Medicine;
import kz.iitu.orken.medical_managament_system.entity.Treatment;
import kz.iitu.orken.medical_managament_system.entity.user.Role;
import kz.iitu.orken.medical_managament_system.entity.user.User;
import kz.iitu.orken.medical_managament_system.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Set;

@Component
public class SetupData implements ApplicationListener<ContextRefreshedEvent> {

    private boolean alreadySetup = false;
    private UserRepository userRepository;
    private RoleRepository roleRepository;
    private MedicineRepository medicineRepository;
    private TreatmentRepository treatmentRepository;
    private DiseaseRepository diseaseRepository;

    @Autowired
    public SetupData(UserRepository userRepository, RoleRepository roleRepository, MedicineRepository medicineRepository, TreatmentRepository treatmentRepository, DiseaseRepository diseaseRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.medicineRepository = medicineRepository;
        this.treatmentRepository = treatmentRepository;
        this.diseaseRepository = diseaseRepository;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if (alreadySetup) {
            return;
        }

        Role doctorRole = createRoleIfNotFound(Role.DOCTOR_ROLE);
        Role adminRole = createRoleIfNotFound(Role.ADMIN_ROLE);
        Role customerRole = createRoleIfNotFound(Role.CUSTOMER_ROLE);

        User doctor = createUserIfNotFound("doctor", doctorRole);
        User admin = createUserIfNotFound("admin", adminRole);
        User customer = createUserIfNotFound("customer", customerRole);

        Disease disease = createDiseaseIfNotFound("Pneumonia");
        Disease disease1 = createDiseaseIfNotFound("Malaria");
        Disease disease2 = createDiseaseIfNotFound("Heart disease");


        createMedicineIfNotFound("levofloxacin", 1000L, disease);
        createMedicineIfNotFound("Chloroquine phosphate", 5000L, disease1);
        createMedicineIfNotFound("Aspirin", 3233L, disease2);

        createTreatmentIfNotFound("smth", 999L, disease, doctor, customer);

        alreadySetup = true;
    }

    @Transactional
    Role createRoleIfNotFound(final String name) {
        Role role = roleRepository.findByName(name);
        if (role == null) {
            role = Role.builder().name(name).build();
            role = roleRepository.save(role);
        }
        return role;
    }

    @Transactional
    User createUserIfNotFound(final String name, final Role role) {
        User user = userRepository.findByUsername(name);
        if (user == null) {
            user = User.builder().firstName(name).lastName(name).username(name).password("test").build();
            user.setRoles(Set.of(role));
            user = userRepository.save(user);
        }
        return user;
    }

    Disease createDiseaseIfNotFound(final String name) {
        Disease disease = diseaseRepository.findDiseaseByName(name);
        if (disease == null) {
            disease = new Disease(name);
            disease = diseaseRepository.save(disease);
        }
        return disease;
    }

    Medicine createMedicineIfNotFound(final String name, final Long price, final Disease disease) {
        Medicine medicine = medicineRepository.findMedicineByName(name);
        if (medicine == null) {
            medicine = new Medicine(name, price);
            medicine.setDiseases(Set.of(disease));
            medicine = medicineRepository.save(medicine);
        }
        return medicine;
    }

    Treatment createTreatmentIfNotFound(final String description, final Long price,
                                       final Disease disease,
                                       final User doctor, final User patient) {
        Treatment treatment = treatmentRepository.findTopByDiseaseAndDoctorAndPatient(disease, doctor, patient);
        if (treatment == null) {
            treatment = Treatment.builder()
                    .description(description)
                    .disease(disease)
                    .createdDate(new Date())
                    .doctor(doctor)
                    .patient(patient)
                    .price(price)
                    .build();
            treatment = treatmentRepository.save(treatment);
        }
        return treatment;
    }

}
