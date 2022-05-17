package kz.iitu.orken.medical_managament_system.repository;

import kz.iitu.orken.medical_managament_system.entity.Disease;
import kz.iitu.orken.medical_managament_system.entity.Medicine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DiseaseRepository extends JpaRepository<Disease, Long> {

    List<Disease> findAllByMedicinesContains(Medicine medicine);
    Disease findDiseaseByName(String name);
    Disease findDiseaseById(Long id);

    @Query("select case when count(d)> 0 then true else false end from Disease d where lower(d.name) like lower(:name)")
    boolean isDiseaseExistByName(@Param("name") String name);

}
