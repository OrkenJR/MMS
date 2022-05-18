package kz.iitu.orken.medical_managament_system.repository;

import kz.iitu.orken.medical_managament_system.entity.Disease;
import kz.iitu.orken.medical_managament_system.entity.Medicine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MedicineRepository extends JpaRepository<Medicine, Long> {

    List<Medicine> findAllByPriceBetweenOrderById(Long start, Long end);
    Medicine findMedicineByName(String name);
    Medicine findMedicineById(Long id);


}
