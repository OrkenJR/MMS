package kz.iitu.orken.medical_managament_system.controller;

import kz.iitu.orken.medical_managament_system.entity.Disease;
import kz.iitu.orken.medical_managament_system.entity.Medicine;
import kz.iitu.orken.medical_managament_system.entity.Treatment;
import kz.iitu.orken.medical_managament_system.service.MedicineService;
import kz.iitu.orken.medical_managament_system.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping(value = "/medicine", consumes = "application/json")
public class MedicineController {

    private UserService userService;
    private MedicineService medicineService;

    @Autowired
    public MedicineController(UserService userService, MedicineService medicineService) {
        this.userService = userService;
        this.medicineService = medicineService;
    }


    @GetMapping("/listMedicine")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyAuthority('doctor', 'admin')")
    public ResponseEntity<List<Medicine>> findAllMedicine() {
        return new ResponseEntity<>(medicineService.findAllMedicine(), HttpStatus.OK);
    }

    @GetMapping("/listTreatment")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyAuthority('admin', 'doctor')")
    public ResponseEntity<List<Treatment>> findAllTreatment() {
        return new ResponseEntity<>(medicineService.findAllTreatment(), HttpStatus.OK);
    }

    @GetMapping("/listDisease")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<Disease>> findAllDisease() {
        return new ResponseEntity<>(medicineService.findAllDisease(), HttpStatus.OK);
    }

    @GetMapping("/myTreatments")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<Treatment>> myTreatments() {
        return new ResponseEntity<>(medicineService.findAllTreatmentByUser(userService.getCurrentUser()), HttpStatus.OK);
    }

    @GetMapping("/treatmentsByDisease")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyAuthority('admin', 'doctor')")
    public ResponseEntity<List<Treatment>> findTreatmentByDisease(@RequestParam String name) {
        return new ResponseEntity<>(medicineService.findAllTreatmentByDisease(name), HttpStatus.OK);
    }

    @PostMapping("/disease")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyAuthority('admin', 'doctor')")
    public ResponseEntity<Disease> saveDisease(@RequestBody Disease disease) {
        return new ResponseEntity<>(medicineService.saveDisease(disease), HttpStatus.OK);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyAuthority('admin', 'doctor')")
    public ResponseEntity<Medicine> saveMedicine(@RequestBody Medicine medicine) {
        return new ResponseEntity<>(medicineService.saveMedicine(medicine), HttpStatus.OK);
    }

    @DeleteMapping("/{medicineId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String> deleteMedicine(@PathVariable Long medicineId) {
        medicineService.deleteMedicine(medicineId);
        return new ResponseEntity<>("Successfully deleted medicine with id: " + medicineId, HttpStatus.OK);
    }

    @DeleteMapping("/disease/{diseaseId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String> deleteDisease(@PathVariable Long diseaseId) {
        medicineService.deleteDisease(diseaseId);
        return new ResponseEntity<>("Successfully deleted disease with id: " + diseaseId, HttpStatus.OK);
    }

    @PostMapping("/getTreatment")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Treatment> gettingTreatment(@RequestBody Disease disease) {
        return new ResponseEntity<>(medicineService.setTreatment(userService.getCurrentUser(), disease), HttpStatus.OK);
    }

    @PostMapping("/buyMedicine")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String> buyMedicine(@RequestParam String medicineName) {
        medicineService.buyMedicine(medicineName);
        return new ResponseEntity<>("You bought a medicine: " + medicineName, HttpStatus.OK);
    }

    @GetMapping("/list/excel")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('admin')")
    public ResponseEntity<ByteArrayResource> exportMedicine() {
        HttpHeaders header = new HttpHeaders();
        header.setContentType(new MediaType("application", "force-download"));
        header.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=medicineList.xlsx");
        return new ResponseEntity<>(medicineService.exportMedicine(), header, HttpStatus.OK);
    }

    @GetMapping("/listDisease/excel")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('admin')")
    public ResponseEntity<ByteArrayResource> exportDisease() {
        HttpHeaders header = new HttpHeaders();
        header.setContentType(new MediaType("application", "force-download"));
        header.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=diseaseList.xlsx");
        return new ResponseEntity<>(medicineService.exportDisease(), header, HttpStatus.OK);
    }

    @GetMapping("/listTreatment/excel")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('admin')")
    public ResponseEntity<ByteArrayResource> exportTreatment() {
        HttpHeaders header = new HttpHeaders();
        header.setContentType(new MediaType("application", "force-download"));
        header.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=treatmentList.xlsx");
        return new ResponseEntity<>(medicineService.exportTreatment(), header, HttpStatus.OK);
    }

}
