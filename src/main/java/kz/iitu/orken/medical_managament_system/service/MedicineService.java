package kz.iitu.orken.medical_managament_system.service;

import kz.iitu.orken.medical_managament_system.entity.Disease;
import kz.iitu.orken.medical_managament_system.entity.Medicine;
import kz.iitu.orken.medical_managament_system.entity.Treatment;
import kz.iitu.orken.medical_managament_system.entity.user.User;

import java.util.List;

public interface MedicineService {

    List<Medicine> findAllMedicine();
    List<Treatment> findAllTreatment();
    List<Treatment> findAllTreatmentByUser(User user);
    List<Treatment> findAllTreatmentByDisease(Disease disease);
    Treatment setTreatment(User patient, Disease disease);
    void buyMedicine(Medicine medicine);
    Disease saveDisease(Disease disease);
    Medicine saveMedicine(Medicine medicine);
    void delete(Disease disease);
    void delete(Medicine medicine);

    boolean isTreatmentFinished(Treatment treatment);
    byte[] exportTreatment();
    byte[] exportMedicine();
    byte[] exportDisease();
    List<User> findUsersByDisease(Disease disease);


}
