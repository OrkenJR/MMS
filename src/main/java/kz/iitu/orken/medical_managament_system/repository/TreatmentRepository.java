package kz.iitu.orken.medical_managament_system.repository;

import kz.iitu.orken.medical_managament_system.entity.Disease;
import kz.iitu.orken.medical_managament_system.entity.Treatment;
import kz.iitu.orken.medical_managament_system.entity.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TreatmentRepository extends JpaRepository<Treatment, Long> {
    List<Treatment> findAllByPatient(User patient);
    List<Treatment> findAllByDisease(Disease disease);
    Treatment findTopByDiseaseAndDoctorAndPatient(Disease disease, User doctor, User patient);
}
