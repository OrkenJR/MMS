package kz.iitu.orken.medical_managament_system.service;

import kz.iitu.orken.medical_managament_system.entity.Disease;
import kz.iitu.orken.medical_managament_system.entity.Medicine;
import kz.iitu.orken.medical_managament_system.entity.Treatment;
import kz.iitu.orken.medical_managament_system.entity.user.User;
import org.springframework.core.io.ByteArrayResource;

import java.util.List;

public interface MedicineService {

    List<Medicine> findAllMedicine();
    List<Treatment> findAllTreatment();
    List<Disease> findAllDisease();
    List<Treatment> findAllTreatmentByUser(User user);
    List<Treatment> findAllTreatmentByDisease(String disease);
    Disease findDiseaseByName(String name);
    Medicine findMedicineByName(String name);
    Treatment setTreatment(User patient, Disease disease);
    void buyMedicine(String medicine);
    Disease saveDisease(Disease disease);
    Medicine saveMedicine(Medicine medicine);
    void deleteDisease(Long disease);
    void deleteMedicine(Long medicine);

    boolean isTreatmentFinished(Treatment treatment);
    ByteArrayResource exportTreatment();
    ByteArrayResource exportMedicine();
    ByteArrayResource exportDisease();
    List<User> findUsersByDisease(Disease disease);


}
